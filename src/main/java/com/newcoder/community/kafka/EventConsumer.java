package com.newcoder.community.kafka;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.model.PutObjectResult;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

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

    @Autowired
    OSSClient ossClient;

    @Autowired
    ThreadPoolTaskScheduler taskScheduler;

    @Value("${wk.image.command}")
    private String wkCommand;

    @Value("${wk.image.storage}")
    private String storage;

    @Value("${oss.bucketName}")
    private String bucketName;

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

    /**
     * 监听删帖事件
     * @param record
     */
    @KafkaListener(topics = {CommunityConstant.TOPIC_DELETE})
    public void delete(ConsumerRecord record) {
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
        // 将ES中对应的帖子删除
        elasticsearchService.deleteById(event.getEntityId());
    }

    /**
     * 消费分享监听事件
     * @param record
     */
    @KafkaListener(topics = {CommunityConstant.TOPIC_SHARE})
    public void handleShare(ConsumerRecord record) {
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
        // 获取参数
        String url = (String) event.getData().get("url");
        String fileName = (String) event.getData().get("fileName");
        String suffix = (String) event.getData().get("suffix");
        // 拼装命令
        String command = wkCommand + " --quality 75 " + url + " " + storage + "/" + fileName + suffix;
        try {
            // 执行命令
            Runtime.getRuntime().exec(command);
            log.info("生成长图成功[ " + storage + "/" + fileName + " ]");
        } catch (IOException e) {
            e.printStackTrace();
            log.info("生成长图失败: " + e.getMessage());
        }

        // 启动线程上传至云服务器
        UploadTask task = new UploadTask(fileName, suffix);
        ScheduledFuture<?> future = taskScheduler.scheduleAtFixedRate(task, 500);
        task.setFuture(future);
    }

    /**
     * 执行上传任务线程
     */
    private class UploadTask implements Runnable {

        // 文件名称
        private String fileName;

        // 文件后缀
        private String suffix;

        // 文件上传返回值
        private Future future;

        // 开始时间
        private long startTime;

        // 上传次数
        private int uploadTimes;

        public UploadTask(String fileName, String suffix) {
            this.fileName = fileName;
            this.suffix = suffix;
            this.startTime = System.currentTimeMillis();
        }

        public void setFuture(Future future) {
            this.future = future;
        }

        @Override
        public void run() {
            // 生成图片失败
            if (System.currentTimeMillis() - startTime > 30000) {
                log.error("生成图片时间过长!终止任务: " + fileName);
                future.cancel(true);
                return;
            }
            // 上传失败
            if (uploadTimes > 3) {
                log.error("上传次数过多! " + fileName);
                future.cancel(true);
                return;
            }
            // 执行上传
            String path = storage + "/" + fileName + suffix;
            File file = new File(path);
            if (file.exists()) {
                // 开始上传
                log.info(String.format("开始第%d次上传%s", ++uploadTimes, fileName));
                try {
                    ossClient.putObject(bucketName, fileName + suffix, new FileInputStream(file));
                    log.info(String.format("第%d次上传%s成功!", uploadTimes, fileName));
                    future.cancel(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.info(String.format("第%d次上传%s失败!", uploadTimes, fileName));
                }
            } else {
                log.info("等待图片上传至本地!");
            }
        }
    }

}
