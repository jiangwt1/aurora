package com.aurora.controller;

import com.aurora.annotation.OptLog;
import com.aurora.model.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.aurora.constant.OptTypeConstant.DELETE;
import static com.aurora.constant.RabbitMQConstant.EMAIL_QUEUE;
import static com.aurora.constant.RabbitMQConstant.SUBSCRIBE_QUEUE;

@Api(tags = "消息队列管理")
@RestController
public class RabbitMQController {

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @ApiOperation(value = "查看队列消息数量")
    @GetMapping("/admin/rabbitmq/queue/count")
    public ResultVO<Map<String, Object>> getQueueMessageCount() {
        Map<String, Object> result = new HashMap<>();
        
        Properties emailQueueProps = rabbitAdmin.getQueueProperties(EMAIL_QUEUE);
        Properties subscribeQueueProps = rabbitAdmin.getQueueProperties(SUBSCRIBE_QUEUE);
        
        result.put("emailQueue", emailQueueProps != null ? emailQueueProps.get("QUEUE_MESSAGE_COUNT") : 0);
        result.put("subscribeQueue", subscribeQueueProps != null ? subscribeQueueProps.get("QUEUE_MESSAGE_COUNT") : 0);
        
        return ResultVO.ok(result);
    }

    @OptLog(optType = DELETE)
    @ApiOperation(value = "清空邮件队列")
    @DeleteMapping("/admin/rabbitmq/queue/email")
    public ResultVO<?> purgeEmailQueue() {
        rabbitAdmin.purgeQueue(EMAIL_QUEUE);
        return ResultVO.ok("邮件队列已清空");
    }

    @OptLog(optType = DELETE)
    @ApiOperation(value = "清空订阅队列")
    @DeleteMapping("/admin/rabbitmq/queue/subscribe")
    public ResultVO<?> purgeSubscribeQueue() {
        rabbitAdmin.purgeQueue(SUBSCRIBE_QUEUE);
        return ResultVO.ok("订阅队列已清空");
    }

    @OptLog(optType = DELETE)
    @ApiOperation(value = "清空所有队列")
    @DeleteMapping("/admin/rabbitmq/queue/all")
    public ResultVO<?> purgeAllQueues() {
        rabbitAdmin.purgeQueue(EMAIL_QUEUE);
        rabbitAdmin.purgeQueue(SUBSCRIBE_QUEUE);
        return ResultVO.ok("所有队列已清空");
    }
}
