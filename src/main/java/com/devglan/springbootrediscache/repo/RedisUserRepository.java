package com.devglan.springbootrediscache.repo;

import com.devglan.springbootrediscache.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RedisUserRepository {

    private HashOperations hashOperations;

    private RedisTemplate<String, User> redisTemplate;

    public RedisUserRepository(RedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
        this.redisTemplate.setKeySerializer(new StringRedisSerializer());
        this.redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        this.redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        this.redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        this.hashOperations = this.redisTemplate.opsForHash();
    }

    public void save(User user){
        try {
            hashOperations.put("USERS", user.getId(), new ObjectMapper().writeValueAsString(user));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
    public void setTimeOut(){
        redisTemplate.expire("USERS", 10, TimeUnit.SECONDS);
    }
    public List<User> findAll(){
        System.out.println("in find all");
//            Thread.sleep(1000000);
        System.out.println(" done in find all");

        return hashOperations.values("USERS");
    }

    public User findById(String id){
        try {
            return (User) new ObjectMapper().readValue(hashOperations.get("USERS", id).toString(),User.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void update(User user){
        save(user);
    }

    public void delete(String id){
        hashOperations.delete("USER", id);
    }

    public void multiGetUsers(List<String> userIds){
        hashOperations.multiGet("USER", userIds);
    }

}
