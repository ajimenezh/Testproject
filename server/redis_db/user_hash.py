import redis
import redis_parameters as rParams

r = redis.from_url(rParams.redis_url)

hash = "84Gtf"

def addUser(id, username):
    conn = redis.from_url(rParams.redis_url)

    conn.set(hash + username, True)
    conn.set(hash + str(id), username)

def get(id):
    conn = redis.from_url(rParams.redis_url)

    return conn.get(hash + str(id))

def checkUser(username):
    conn = redis.from_url(rParams.redis_url)

    return conn.exists(hash + username)