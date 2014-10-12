import redis
import redis_parameters as rParams
from sets import Set

r = redis.from_url(rParams.redis_url)


class RedisSearch:

    conn = 0
    exactHash = 'A54Sd2_'
    prefixHash = 'nU96D_'
    wordCountHash = '7GreR_'

    def __init__(self):
        self.conn = redis.from_url(rParams.redis_url)


    def insert(self, text, index = None):

        pipe = self.conn.pipeline()

        words = text.split(' ')

        for word in words:

            pipe.sadd(self.exactHash + word, index)

            for i in range(len(word)-1):

                prefix = word[0:i+1]

                pipe.incr(self.wordCountHash + prefix, 1)

                pipe.sadd(self.prefixHash + prefix, index)

        pipe.execute()


    def search(self, word, number_elements=0, exact=False):

        exactWords = self.conn.smembers(self.exactHash + word)

        if (exact):
            return exactWords

        if (number_elements != 0 and number_elements > len(exactWords)):
            return exactWords
        else:

            v = exactWords

            words = self.conn.smembers(self.prefixHash + word)

            for element in words:
                v = v | words

            return list(v)


    def query(self, query, number_elements=0):

        words = query.split(' ')

        possibleMatches = ()

        empty = True

        tmp = words[0:-1]

        for word in tmp:

            v = self.search(word, exact=True)

            if empty:
                empty = False
                possibleMatches = Set(v)
            else:
                possibleMatches = possibleMatches & Set(v)

        v = self.search(words[-1], exact=False)

        if empty:
            possibleMatches = Set(v)
        else:
            possibleMatches = possibleMatches & Set(v)

        return list(possibleMatches)





