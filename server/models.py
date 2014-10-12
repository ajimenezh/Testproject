import peewee


psql_db = peewee.PostgresqlDatabase('d25t4ouh6949q6', host='ec2-54-83-199-115.compute-1.amazonaws.com', port='5432', dbname='d25t4ouh6949q6', user='jqzsztkjoynriu', password='cErbo7-ZFppun0TfLkWEOZRd4N', sslmode='require')


class BaseModel(peewee.Model):
    """A base model that will use our Postgresql database"""
    class Meta:
        database = psql_db


class User(BaseModel):
    username = peewee.CharField(unique=True, index=True)
    user_id = peewee.PrimaryKeyField()
    gcm_id = peewee.CharField(null=True)

    def toObject(self):
        return {'user_id': self.user_id, 'username':self.username}


class Topic(BaseModel):
    topic_id = peewee.PrimaryKeyField()
    title = peewee.CharField()
    description = peewee.TextField()
    creation_date = peewee.DateTimeField(index=True)
    user_id = peewee.ForeignKeyField(User, related_name='topics')

    def toObject(self):
        return {'topic_id': self.topic_id, 'title':self.title, 'description':self.description, 'creation_date':str(self.creation_date), 'user_id':self.user_id.toObject()}


class Comment(BaseModel):
    comment_id = peewee.PrimaryKeyField()
    text = peewee.TextField()
    creation_date = peewee.DateTimeField()
    user_id = peewee.ForeignKeyField(User, related_name='comments')
    topic_id = peewee.ForeignKeyField(Topic, related_name='topic')
    response_comment_id = peewee.IntegerField()

    def toObject(self):
        return {'comment_id': self.comment_id, 'text':self.text, 'creation_date':str(self.creation_date), 'user_id':self.user_id.toObject(), 'response_comment_id': self.response_comment_id}


class Follow(BaseModel):
    topic_id = peewee.ForeignKeyField(Topic, related_name='followers', index=True)
    user_id = peewee.ForeignKeyField(User, related_name='following')

class GcmId(BaseModel):
    gcm_id = peewee.CharField()
    user_id = peewee.ForeignKeyField(User)

#psql_db.connect()
#psql_db.drop_tables([User, Topic, Follow, Comment])
#psql_db.create_tables([User, Topic, Follow, Comment])

#psql_db.connect()
#v = Follow.select().where(Follow.topic_id=='2').join(user)

#for t in v:
#    print (t.topic_id)
#    print (t.user_id)
#    print (t.user_id.gcm_id)
#try:
#    user = User.create(username="Test")

#    print (user.user_id)
#    print (user.username)
#except:
#    print ("Error")
