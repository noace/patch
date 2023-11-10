package com.kyee.upgrade.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description：RabbitMq配置类
 * @author：DangHangHang
 * @date：2023-07-13
 */
@Configuration
public class RabbitMqConfig {

    @Bean
    public Queue queue() {

        return new Queue("hello");
    }
}
