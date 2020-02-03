package com.newcoder.community.kafka;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.newcoder.community.domain.DiscussPost;
import com.newcoder.community.domain.Event;
import com.newcoder.community.domain.Message;
import com.newcoder.community.service.DiscussPostService;
import com.newcoder.community.service.ElasticsearchService;
import com.newcoder.community.service.MessageService;
import com.newcoder.community.util.CommunityConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Kafka 消息消费者
 */
@Component
@Slf4j
public class EventConsumer {

    @Autowired
    MessageService messageService;

    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    ElasticsearchService elasticsearchService;

    /**
     * 监听主题消息，将消息插入数据库中
     * @param record
     */
    @KafkaListener(topics = {CommunityConstant.TOPIC_COMMENT, CommunityConstant.TOPIC_LIKE, CommunityConstant.TOPIC_FOLLOW})
    public void handleMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            log.error("消息为空!");
            return;
        }
        // 将消息转为事件
        Event event = JSON.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            log.error("消息格式有误!");
            return;
        }

        // 创建消息对象
        Message message = new Message()
                .setStatus(0)
                .setFromId(CommunityConstant.SYSTEM_USER_ID)
                .setCreateTime(new Date())
                .setConversationId(event.getTopic())
                .setToId(event.getEntityUserId());
        // 设置消息内容
        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());

        if (!event.getData().isEmpty()) {
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));

        // 保存消息
        messageService.insert(message);
    }

    /**
     * 监听发布帖子事件处理
     * @param record
     */
    @KafkaListener(topics = CommunityConstant.TOPIC_PUBLISH)
    public void publish(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            log.error("消息为空!");
            return;
        }
        // 将消息转为事件
        Event event = JSON.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            log.error("消息格式有误!");
            return;
        }

        // 查询帖子
        DiscussPost post = discussPostService.getDiscussPostById(event.getEntityId());
        // 保存在es中
        elasticsearchService.save(post);
    }

}
