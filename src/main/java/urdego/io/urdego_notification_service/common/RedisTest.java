package urdego.io.urdego_notification_service.common;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisTest {
    private final StringRedisTemplate redisTemplate;

    @PostConstruct
    public void redisConnectionTest() {
        try {
            redisTemplate.opsForValue().set("testKey", "connected");
            String value = redisTemplate.opsForValue().get("testKey");
            log.info("Redis connected : {} ",value);
        }catch (Exception e){
            log.error("Redis connection test failed",e);
        }
    }
}
