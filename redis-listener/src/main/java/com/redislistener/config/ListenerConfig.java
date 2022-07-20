package com.redislistener.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Slf4j
@Configuration
@Component
public class ListenerConfig {

  @Value("${spring.redis.host}")
  private String redisHost;

  @Value("${listener.redis.keyspace-pattern:__keyevent@[0-9]__:((expired)|(del) USERS)}")
  private String keySpacePattern;


  @Bean(destroyMethod = "close")
  @Scope("prototype")
  public JedisPool jedisPool() {
    log.info(String.format("Establishing a Redis connection pool to: %s", redisHost));
    JedisPoolConfig config = new JedisPoolConfig();
    config.setMinEvictableIdleTimeMillis(10_000);
    config.setMinIdle(4);
    config.setMaxIdle(12);
    return new JedisPool(config, redisHost);
  }


}