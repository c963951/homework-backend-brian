package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.List;

@Configuration
public class RedisConfig {


    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 1. Key 序列化器: 使用 StringSerialiser 確保 Key 在 Redis 中可讀
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // 2. Value 序列化器: 使用 Jackson 序列化為 JSON，保證數據在 Redis 中可讀
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 定義並加載用於原子性取任務的 Lua 腳本
     */
    @Bean
    public DefaultRedisScript<List> atomicFetchAndRemoveScript() {
        DefaultRedisScript<List> script = new DefaultRedisScript<>();
        // 腳本路徑必須匹配 resources/scripts/atomic_fetch_and_remove.lua
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/TaskFetchRemove.lua")));
        // 返回結果是 Task ID 的列表
        script.setResultType(List.class);
        return script;
    }
}
