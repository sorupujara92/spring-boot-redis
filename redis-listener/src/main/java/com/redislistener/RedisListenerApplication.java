package com.redislistener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.util.function.Predicate;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;

@SpringBootApplication
@Slf4j
public class RedisListenerApplication extends JedisPubSub {


	private final ObjectMapper mapper;

	private Thread subscriptionThread;

	public static void main(String[] args) {
		SpringApplication.run(RedisListenerApplication.class, args);
	}

	private final JedisPool jedisPool;

	@Value("${listener.redis.keyspace-pattern:__keyevent@[0-9]__:((expired)|(del)) USERS}")
	private String keySpacePattern;



	private final Predicate<String> isDeleteEvent = channel -> channel.endsWith("del") || channel.contains("del");

	@Autowired
	public RedisListenerApplication(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
		this.mapper = Jackson2ObjectMapperBuilder
				.json()
				.modules(new JavaTimeModule())
				.build();
	}

	@PostConstruct
	public void run() {
		this.subscriptionThread = new Thread(() -> {
			try (Jedis jedis = jedisPool.getResource()) {
				jedis.psubscribe(this, keySpacePattern);
			} catch (JedisConnectionException e) {
				log.error("Subscription connection threw an exception: ", e);
			}
		}, "Subscription Thread");
		this.subscriptionThread.start();
	}

	@PreDestroy
	public void stop() {
		if (this.isSubscribed()) {
			this.punsubscribe();
		}
		if (subscriptionThread.isAlive()) {
			subscriptionThread.interrupt();
		}
	}

	@Override
	public void onPSubscribe(String channel, int subscribedChannels) {
		log.info(String.format("Subscribed to channel <%s>. Number of subscribed channels: %d", channel, subscribedChannels));
	}

	@Override
	public void onPUnsubscribe(String channel, int subscribedChannels) {
		log.info(String.format("Unsubscribed from channel <%s>. Number of unsubscribed channels: %d", channel, subscribedChannels));
	}

	@Override
	public void onPMessage(String pattern, String channel, String key) {
		log.info(String.format("Received a message: pattern = %s, channel = %s, key = %s", pattern, channel, key));
	}

}
