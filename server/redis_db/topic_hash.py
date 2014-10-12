import redis
import redis_parameters as rParams

r = redis.from_url(rParams.redis_url)


hash = "89fhT"


def addTopic(id, topic):
    conn = redis.from_url(rParams.redis_url)

    conn.set(hash + str(id), topic)


def get(id):
    conn = redis.from_url(rParams.redis_url)

    return conn.get(hash + str(id))