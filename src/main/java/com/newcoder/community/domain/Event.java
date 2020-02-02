package com.newcoder.community.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka 事件
 */
@Data
@Accessors(chain = true)
public class Event {

    private String topic;
    private Integer userId;
    private Integer entityType;
    private Integer entityId;
    private Integer entityUserId;
    private Map<String, Object> data = new HashMap<>();

    public Event setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
}
