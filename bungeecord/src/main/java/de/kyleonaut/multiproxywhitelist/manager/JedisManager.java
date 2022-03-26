package de.kyleonaut.multiproxywhitelist.manager;

import lombok.Cleanup;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @author kyleonaut
 */
public class JedisManager {

    private final JedisPool jedisPool;

    public JedisManager(String host, int port, String password) {
        final JedisPoolConfig config = new JedisPoolConfig();
        config.setMinIdle(16);
        config.setMaxIdle(128);
        config.setMaxTotal(128);
        this.jedisPool = new JedisPool(config, host, port, 1000, password);
        System.out.println("[MultiProxyWhitelist] Connected to Redis Server!");
    }

    public void subscribe(String channel, Consumer<String> messageConsumer) {
        CompletableFuture.runAsync(() -> {
            @Cleanup final Jedis jedis = getJedis();
            jedis.subscribe(new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    messageConsumer.accept(message);
                }
            }, channel);
        });
    }

    public void publish(String channel, String message) {
        CompletableFuture.runAsync(() -> {
            @Cleanup final Jedis jedis = jedisPool.getResource();
            jedis.publish(channel, message);
        });
    }

    private Jedis getJedis() {
        return this.jedisPool.getResource();
    }

    public void shutdown() {
        this.jedisPool.close();
    }

}
