package com.example.history_social_backend.common.messaging.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import java.time.Duration;

@Slf4j
@Configuration
public class RedisMessagingConfig {

    @Value("${messaging.redis.stream.name}")
    private String streamName;

    @Value("${messaging.redis.stream.consumer-group}")
    private String consumerGroup;

    @Value("${messaging.redis.stream.consumer-name}")
    private String consumerName;

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());

        template.setValueSerializer(RedisSerializer.json());
        template.setHashValueSerializer(RedisSerializer.json());

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public boolean initializeConsumerGroup(
            RedisTemplate<String, Object> redisTemplate) {

        try {
            redisTemplate.opsForStream()
                    .add(MapRecord.create(
                            streamName,
                            java.util.Map.of("init", "1")));

            redisTemplate.opsForStream()
                    .createGroup(
                            streamName,
                            ReadOffset.latest(),
                            consumerGroup);

            log.info("Created consumer group {}", consumerGroup);

        } catch (Exception e) {
            log.info("Consumer group may already exist");
        }

        return true;
    }

    @Bean
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public StreamMessageListenerContainer<String, MapRecord<String, Object, Object>> streamMessageListenerContainer(
            RedisConnectionFactory connectionFactory) {

        var options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                .builder()
                .pollTimeout(Duration.ofSeconds(2))
                .batchSize(10)
                .build();

        return (StreamMessageListenerContainer) StreamMessageListenerContainer.create(
                connectionFactory,
                options);
    }

    public String getStreamName() {
        return streamName;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public String getConsumerName() {
        return consumerName;
    }
}