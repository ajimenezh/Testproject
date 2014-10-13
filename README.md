Testproject
===========

This is a test project to test various features
-----------------------------------------------

Registration
------------

This part is a very simple registration process. The user just have to introduce an username and
the phone acts as the password. The usernames are unique, so to make the verification process faster
I use a user hash in redis in the server, and once we have checked the user does not exist, we introduce it in the 
PostgreSQL. This way if the user exists we can return the information faster. Because of simultaneous 
requests it still can happen that the SQL request returns an error.

Topics
------
The topics appear in the main activity when opening the app if you are registered. To create a new topic or search
a fragment is opened, so the transitions are very fast. All the topics are stored in the SQL database and recovered 
sorted by creation date.

TODO:
- Use Redis to cache the answers.
- Order the topics by some function of the creation date, the number of likes and the comments.
- Only recover X number of topics and lazy load the rest when needed.

A polling service is kept in the backgound to check if there are new topics.

Comments
--------
The comments of a topic are stored in the SQL database, but also a cache of recent requests is stored in redis. When
a comment is added, if the topic is contained in cache, it is also inserted in the cache.
The same system of polling is used here.

Follow
-------
A system of follows is implemented, so when you are following some topic, and there is a new comment, you 
can receive a push notification. The notification service uses GCM and can wake the phone to show it.

Like
----
You can also like a topic and the number of likes is showed. The likes are going to be used to order the topics.

Typeahead Search
----------------
Tipically, the typeahead search is programmed using a prefix or radix tree, but because I use redis for fast access,
using this data structure does not improve the running time because of the natire of redis, so we only use a hash
of the words in the topics titles and usernames.

TODO:
Use elasticsearch as a full-text search in the comments additionally to the topic titles.
