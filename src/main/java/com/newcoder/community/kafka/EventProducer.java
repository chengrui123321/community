package com.newcoder.community.kafka;

import com.alibaba.fastjson.JSONObject;
import com.newcoder.community.domain.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka 消息发送者
 */
@Component
public class EventProducer {

    @Autowired
    KafkaTemplate kafkaTemplate;

    /**
     * 处理事件，将消息发送到指定主题
     * @param event
     */
    public void fireEvent(Event event) {
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }

}
