package com.hibiznet.hr.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories(enableKeyspaceEvents = org.springframework.data.redis.core.RedisKeyValueAdapter.EnableKeyspaceEvents.OFF)
public class RedisConfig {
}
