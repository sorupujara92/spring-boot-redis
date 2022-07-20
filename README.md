# spring-boot-redis-cache

## Using Redis cache in Spring Boot

Redis is a high performance open source cache store that keeps cached content in-memory for quick retrieval of data.
Redis can be used as a cache store, database even a message broker. We will concentrate on the caching aspects in this
article. Redis provides basic set of data-types such as Strings, Hashes, Lists, Sets and Streams. We can even store
binary content such as the byte data of Serialized Java objects.

## Redis in Action

Hit the below API to get data stored into redis with HashOperation.

```
POST : http://localhost:8080/users

With Payload as 
{   "name": "Pulpen Pilot",
    "age": 21,
    "id" : 131
}
```

To set the timeout or TTL of the key hit

```
POST http://localhost:8080/users/settimeout
```

Default added is 10 seconds.

To get all the users from Key hash of Redis

```
GET http://localhost:8080/users/
```


Finally, Redis is one of the many officially supported cache solution by Spring Boot. Try the rest and chose the one
that fits best for you.

Medium Link

https://medium.com/@souravpujara_18425/redis-and-redis-notifications-with-spring-boot-d5517181ac3a