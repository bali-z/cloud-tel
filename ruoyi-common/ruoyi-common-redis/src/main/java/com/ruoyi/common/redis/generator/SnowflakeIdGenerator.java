package com.ruoyi.common.redis.generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 雪花算法迭代生成器
 * @author dz
 */
@SuppressWarnings("ALL")
public class SnowflakeIdGenerator {

    // 起始时间戳（可根据需求调整）
    private static final long EPOCH = 1672444800000L;

    private static final long WORKER_ID_BITS = 5L;
    private static final long DATACENTER_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    private static final long MAX_WORKER_ID = (1L << WORKER_ID_BITS) - 1;
    private static final long MAX_DATACENTER_ID = (1L << DATACENTER_ID_BITS) - 1;

    private static final long SEQUENCE_MASK = (1L << SEQUENCE_BITS) - 1;

    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    private long lastTimestamp = -1L;
    private long sequence = 0L;

    private final long workerId;
    private final long datacenterId;

    @Autowired
    public RedisTemplate redisTemplate;

    public SnowflakeIdGenerator(long workerId, long datacenterId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException("Worker ID out of bounds: " + workerId);
        }
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException("Datacenter ID out of bounds: " + datacenterId);
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    public synchronized String nextId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate ID.");
        }

        if (timestamp == lastTimestamp) {
            // 使用 Redis 分布式计数器来生成序列号
            String redisKey = "snowflake:sequence:" + timestamp;
            sequence = redisTemplate.opsForValue().increment(redisKey, 1);
            redisTemplate.expire(redisKey, 1, TimeUnit.MILLISECONDS);
            if (sequence > SEQUENCE_MASK) {
                // 等待下一毫秒
                timestamp = waitNextMillis(lastTimestamp);
                sequence = 0;
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;

        // 生成唯一 ID
        long id = ((timestamp - EPOCH) << TIMESTAMP_LEFT_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;

        return Long.toString(id);
    }

    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}
