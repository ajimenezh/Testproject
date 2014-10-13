from flask import Flask
import flask
from flask import request
import models
import datetime
import redis_db.user_hash as userHash
import redis_db.topic_hash as topicHash
import redis_db.redis_typeaheadsearch as redis_search
import gcm_utils
import redis_db.redis_topic_cache as redis_topic_cache
import json

app = Flask(__name__)

@app.route('/')
def hello_world():
    return 'Hello World!'

@app.route('/get_topics')
def get_topics():

    topics = []

    try:
        with models.psql_db.transaction():
            topics = models.Topic.select(models.Topic, models.fn.Count(models.Comment.comment_id).alias('comment_cnt')).join(models.Comment).group_by(models.Topic)
            topics = models.Topic.select(models.Topic, models.fn.Count(models.Like.user_id) \
                        .alias('likes_cnt'), models.fn.Count(models.Comment.user_id) \
                        .alias('comments_cnt'))\
                        .join(models.Like, join_type=models.peewee.JOIN_LEFT_OUTER)\
                        .switch(models.Topic) \
                        .join(models.Comment, join_type=models.peewee.JOIN_LEFT_OUTER) \
                        .group_by(models.Topic)
    except:
        print ('Failure: Getting topics.')
        return 'Fail'

    v = []
    for t in topics:
        element = t.toObject()
        element['comments_cnt'] = t.comments_cnt
        element['likes_cnt'] = t.likes_cnt
        v.append(element)

    return flask.jsonify({'results':v})

@app.route('/get_comments')
def get_comments():

    topic = request.args.get('topic')

    rc = redis_topic_cache.RedisCache()

    if (rc.checkExist(topic)):
        print ("cache")
        return rc.getDocument(topic)

    comments = []

    try:
        with models.psql_db.transaction():
            comments = models.Comment.select().join(models.Topic).where(models.Topic.topic_id == topic)
    except:
        print ('Failure: Getting comments.')
        return 'Fail'

    v = []
    for t in comments:
        v.append(t.toObject())

    rc.insertDocument(topic, json.dumps({'results':v}))

    return flask.jsonify({'results':v})

@app.route('/add_topic')
def add_topic():

    topictitle = request.args.get('topictitle')
    topic = None

    try:
        with models.psql_db.transaction():
            topic = models.Topic.create(title=topictitle, description='', creation_date=datetime.date.today(), user_id=1)
    except models.peewee.IntegrityError:
        print ("Failure: Inserting topic")
        return 'Fail'


    topicHash.addTopic(topic.topic_id, topic.title)

    redisSearch = redis_search.RedisSearch()
    redisSearch.insert(topic.title, topic.topic_id)

    return flask.jsonify(topic.toObject())

@app.route('/add_comment')
def add_comment():

    topic = request.args.get('topic')
    text = request.args.get('comment')

    comment = None

    try:
        with models.psql_db.transaction():
            comment = models.Comment.create(text=text, creation_date=datetime.date.today(), user_id=1, topic_id=topic, response_comment_id=0)
    except models.peewee.IntegrityError:
        print ("Failure: Inserting comment")
        return 'Fail'

    v = models.Follow.select().where(models.Follow.topic_id==topic).join(models.User)

    for el in v:
        gcm_utils.send(el.t.user_id.gcm_id)


    rc = redis_topic_cache.RedisCache()

    if (rc.checkExist(topic)):
        v = rc.getDocument(topic)

        v = json.loads(v)
        v['results'].append(comment.toObject())

        rc.insertDocument(topic, json.dumps(v))


    return flask.jsonify(comment.toObject())

@app.route('/register_user')
def register_user():

    username = request.args.get('username')

    if (userHash.checkUser(username)):
        return "Fail"

    user = ''

    try:
            user = models.User.create(username=username)
    except models.peewee.IntegrityError:
        print ('Failure: %s is already in use' % username)
        return "Fail"

    userHash.addUser(user.user_id, user.username)
    redisSearch = redis_search.RedisSearch()
    redisSearch.insert(user.username, user.user_id)

    return flask.jsonify(user.toObject())

@app.route('/search')
def search():

    query = request.args.get('query')

    redisSearch = redis_search.RedisSearch()
    results = redisSearch.query(query)

    searchResults = []

    for element in results:
        if (userHash.checkUser(element)):
            searchResults.append({"type":"user", "user_id":element, "user":userHash.get(element)})
        else:
            tmp = topicHash.get(element)
            if (tmp != None):
                searchResults.append({"type":"topic", "topic_id":element, "topic":tmp})

    if (len(searchResults) > 10):
        searchResults = searchResults[0:10]

    print (searchResults)

    return flask.jsonify({'results':searchResults})


@app.route('/follow')
def follow():

    user_id = request.args.get('user_id')
    topic_id = request.args.get('topic_id')

    try:
        with models.psql_db.transaction():
            models.Follow.create(user_id=int(user_id), topic_id=int(topic_id))
    except models.peewee.IntegrityError:
        print ('Failure: In follow.')
        return "Fail"

    return "Ok"


@app.route('/unfollow')
def unfollow():

    user_id = request.args.get('user_id')
    topic_id = request.args.get('topic_id')

    try:
        with models.psql_db.transaction():
            follow = models.Follow.get(models.Follow.topic_id == int(topic_id))
            follow.delete_instance()
    except models.peewee.IntegrityError:
        print ('Failure: In follow.')
        return "Fail"

    return "Ok"

@app.route('/register_gcm')
def register_gcm():

    user_id = request.args.get('user_id')
    gcmid = request.args.get('gcm_id')

    try:
        with models.psql_db.transaction():
            user = models.User.get(user_id=user_id)
            user.gcm_id = gcmid
            user.save()
    except models.peewee.IntegrityError:
        print ('Failure: In follow.')
        return "Fail"

    return "Ok"


@app.route('/like')
def like():

    user_id = request.args.get('user_id')
    topic_id = request.args.get('topic_id')

    try:
        with models.psql_db.transaction():
            models.Like.create(user_id=int(user_id), topic_id=int(topic_id))
    except models.peewee.IntegrityError:
        print ('Failure: In follow.')
        return "Fail"

    return "Ok"


@app.route('/unlike')
def unlike():

    user_id = request.args.get('user_id')
    topic_id = request.args.get('topic_id')

    try:
        with models.psql_db.transaction():
            like = models.Like.get(models.Like.topic_id == int(topic_id))
            like.delete_instance()
    except models.peewee.IntegrityError:
        print ('Failure: In follow.')
        return "Fail"

    return "Ok"


if __name__ == '__main__':
    app.debug = True
    app.run(host='0.0.0.0')
