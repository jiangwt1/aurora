package com.aurora.consumer;


import com.alibaba.fastjson2.JSON;
import com.aurora.entity.Article;
import com.aurora.entity.UserInfo;
import com.aurora.model.dto.EmailDTO;
import com.aurora.service.ArticleService;
import com.aurora.service.SystemConfigProviderService;
import com.aurora.service.UserInfoService;
import com.aurora.util.EmailUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.aurora.constant.CommonConstant.TRUE;
import static com.aurora.constant.RabbitMQConstant.SUBSCRIBE_QUEUE;

@Component
@RabbitListener(queues = SUBSCRIBE_QUEUE)
public class SubscribeConsumer {

    @Autowired
    private SystemConfigProviderService configProvider;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private EmailUtil emailUtil;

    @RabbitHandler
    public void process(byte[] data) {
        String websiteUrl = configProvider.getConfig("website.url", "https://www.gysy.ltd");
        String websiteName = configProvider.getConfig("website.name", "Aurora博客");
        String websiteMessage = configProvider.getConfig("website.message", "");
        
        Integer articleId = JSON.parseObject(new String(data), Integer.class);
        Article article = articleService.getOne(new LambdaQueryWrapper<Article>().eq(Article::getId, articleId));
        List<UserInfo> users = userInfoService.list(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getIsSubscribe, TRUE));
        List<String> emails = users.stream().map(UserInfo::getEmail).collect(Collectors.toList());
        for (String email : emails) {
            EmailDTO emailDTO = new EmailDTO();
            Map<String, Object> map = new HashMap<>();
            emailDTO.setEmail(email);
            emailDTO.setSubject("文章订阅");
            emailDTO.setTemplate("common.html");
            String url = websiteUrl + "/articles/" + articleId;
            String name = websiteName; // 获取网站名称
            map.put("name",websiteName);
            map.put("message",websiteMessage);
            if (article.getUpdateTime() == null) {
                map.put("content", name
                        + "<a style=\"text-decoration:none;color:#12addb\" href=\"" + url + "\">点击查看</a>");
            } else {
                map.put("content",  name + "《"  + article.getArticleTitle() + "》进行了更新，"
                        + "<a style=\"text-decoration:none;color:#12addb\" href=\"" + url + "\">点击查看</a>");
            }
            emailDTO.setCommentMap(map);
            emailUtil.sendHtmlMail(emailDTO);
        }
    }

}
