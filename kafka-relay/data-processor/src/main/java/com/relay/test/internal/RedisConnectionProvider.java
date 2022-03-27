package com.relay.test.internal;

import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

public class RedisConnectionProvider {
    private Jedis currentJedisClient;

    private Jedis initializeJedisClient() {
        // Create a Jedis client.
        Jedis jedisClient = new Jedis("localhost");
        return jedisClient;
    }

    public Jedis getJedisClient() {
        if (currentJedisClient != null) {
            return this.currentJedisClient;
        }
        return this.currentJedisClient = initializeJedisClient();
    }

    // todo: remove
    public static void main(String[] args) {
        try {
            RedisConnectionProvider redisConnectionProvider = new RedisConnectionProvider();

            // Create a Jedis client.
            Jedis jedisClient = redisConnectionProvider.getJedisClient();

            // Prepare the employee information to insert.
            String userid = "1";
            Map<String, String> userProfile = new HashMap<String, String>();
            userProfile.put("name", "John");
            userProfile.put("age", "35");
            userProfile.put("language", "Redis");

            // Insert the data.
            String result = jedisClient.hmset(userid, userProfile);
            System.out.println("HMSET returned " + result + ": id=1, name=John, age=35, language=Redis");

            // Query the data.
            Map<String, String> userData = jedisClient.hgetAll(userid);
            System.out.println("Query result: name=" + userData.get("name") +
                    ", age=" + userData.get("age") + ", language=" + userData.get("language"));

            // Close the client.
            jedisClient.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
