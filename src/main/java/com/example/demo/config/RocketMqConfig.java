package com.example.demo.config;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RocketMqConfig {

    @Value("${rocketmq.name-server}")
    private String nameServer;

    @Value("${rocketmq.producer.group:SCHEDULER_PRODUCER_GROUP}")
    private String producerGroup;

    /**
     * 顯式配置並啟動 RocketMQ 生產者。
     * @return DefaultMQProducer 實例。
     */
    @Bean(destroyMethod = "shutdown") // 使用 initMethod/destroyMethod 確保生命週期管理
    public DefaultMQProducer defaultMQProducer() {
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
        producer.setNamesrvAddr(nameServer);
        producer.setSendMsgTimeout(5000);
        producer.setRetryTimesWhenSendFailed(3);
        producer.setVipChannelEnabled(false);
        return producer;
    }
}
