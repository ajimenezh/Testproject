import redis
import redis_parameters as rParams
import flask
import json

r = redis.from_url(rParams.redis_url)


class RedisCache:

    hash_count = "Gdt6d"
    hash_set = "hdi6h"
    hash_prefix = "diC62n"

    def __init__(self):
        self.conn = redis.from_url(rParams.redis_url)

    def insertDocument(self, key, document):

        if (not self.conn.sismember(self.hash_set, self.hash_prefix + key)):

            if (self.conn.get(self.hash_count)==None):
                self.conn.set(self.hash_count, 0)

            print self.conn.get(self.hash_count)

            if (self.conn.get(self.hash_count) >= 100):

                #Random.
                # TODO: Substitue for oldest topics.
                toErase = self.conn.srandmember(self.hash_set, 10)

                tot = 0
                for item in toErase:
                    if (self.conn.sismember(self.hash_set, item) == 1):
                        self.conn.srem(self.hash_set, item)
                        tot = tot + 1

                cnt = int(self.conn.get(self.hash_count))
                self.conn.set(self.hash_count, cnt+1-tot)


            else:
                cnt = int(self.conn.get(self.hash_count))
                self.conn.set(self.hash_count, cnt+1)

            self.conn.sadd(self.hash_set, self.hash_prefix + key)

            self.conn.set(self.hash_prefix + key, document)
        else:
            self.conn.set(self.hash_prefix + key, document)


    def checkExist(self, key):
        if self.conn.sismember(self.hash_set, self.hash_prefix + key):
            return True
        else:
            return False


    def getDocument(self, key):
        if self.conn.sismember(self.hash_set, self.hash_prefix + key):
            tmp = self.conn.get(self.hash_prefix + key)
            return tmp
        return None
