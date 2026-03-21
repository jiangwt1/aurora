package com.aurora.service.impl;

import com.alibaba.fastjson2.JSON;
import com.aurora.model.dto.EmailDTO;
import com.aurora.model.dto.FriendLinkAdminDTO;
import com.aurora.model.dto.FriendLinkDTO;
import com.aurora.entity.FriendLink;
import com.aurora.mapper.FriendLinkMapper;
import com.aurora.model.vo.FriendLinkReviewVO;
import com.aurora.service.FriendLinkService;
import com.aurora.service.SystemConfigProviderService;
import com.aurora.util.BeanCopyUtil;
import com.aurora.util.EmailUtil;
import com.aurora.util.PageUtil;
import com.aurora.model.vo.ConditionVO;
import com.aurora.model.vo.FriendLinkVO;
import com.aurora.model.dto.PageResultDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.aurora.util.CommonUtil.checkEmail;
@Service
public class FriendLinkServiceImpl extends ServiceImpl<FriendLinkMapper, FriendLink> implements FriendLinkService {

    @Autowired
    private FriendLinkMapper friendLinkMapper;
    
    @Autowired
    private SystemConfigProviderService configProvider;

    @Autowired
    private EmailUtil emailUtil;


    @Override
    public List<FriendLinkDTO> listFriendLinks() {
        LambdaQueryWrapper<FriendLink> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FriendLink::getIsReview, 1);
        List<FriendLink> friendLinks = friendLinkMapper.selectList(queryWrapper);
        return BeanCopyUtil.copyList(friendLinks, FriendLinkDTO.class);
    }

    @Override
    public PageResultDTO<FriendLinkAdminDTO> listFriendLinksAdmin(ConditionVO conditionVO) {
        Page<FriendLink> page = new Page<>(PageUtil.getCurrent(), PageUtil.getSize());
        Page<FriendLink> friendLinkPage = friendLinkMapper.selectPage(page, new LambdaQueryWrapper<FriendLink>()
                .like(StringUtils.isNotBlank(conditionVO.getKeywords()), FriendLink::getLinkName, conditionVO.getKeywords()));
        List<FriendLinkAdminDTO> friendLinkBackDTOs = BeanCopyUtil.copyList(friendLinkPage.getRecords(), FriendLinkAdminDTO.class);
        return new PageResultDTO<>(friendLinkBackDTOs, (int) friendLinkPage.getTotal());
    }

    @Override
    public void saveOrUpdateFriendLink(FriendLinkVO friendLinkVO) {
        FriendLink friendLink = BeanCopyUtil.copyObject(friendLinkVO, FriendLink.class);
        this.saveOrUpdate(friendLink);
    }


    @Override
    public void applyLink(FriendLinkVO friendLinkVO) {
        FriendLink friendLink = BeanCopyUtil.copyObject(friendLinkVO, FriendLink.class);
        String domain = extractDomain(friendLink.getLinkAddress());
        if (domain != null) {
            // 使用 friendLinkMapper 查询是否存在包含该域名的记录
            LambdaQueryWrapper<FriendLink> queryWrapper = new LambdaQueryWrapper<FriendLink>()
                    .like(FriendLink::getLinkAddress, domain);
            boolean exists = friendLinkMapper.selectCount(queryWrapper) > 0;
            if (exists) {
                // 域名已存在，不做处理
            } else {
                friendLink.setIsReview(0);
                this.save(friendLink);
                sendReviewEmail(friendLink);
            }
        }

    }
    private String extractDomain(String url) {
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            if (domain != null) {
                // 去除 www 前缀
                return domain.startsWith("www.") ? domain.substring(4) : domain;
            }
        } catch (URISyntaxException e) {
            // Invalid URL
        }
        return null;
    }
    public void sendReviewEmail(FriendLink friendLink) {
        String url = configProvider.getConfig("friendLink.url", "https://www.gysy.ltd");
        String email = configProvider.getConfig("friendLink.email", "");
        
        // 生成审核链接
        String reviewUrl = url +"/api/review/" + friendLink.getId();
        String approveUrl = reviewUrl+ "?action=approve";
        String rejectUrl = reviewUrl+ "?action=reject";
        // 构建邮件内容的变量
        Map<String, Object> map = new HashMap<>();
        map.put("linkName", friendLink.getLinkName());
        map.put("linkAvatar", friendLink.getLinkAvatar());
        map.put("linkAddress", friendLink.getLinkAddress());
        map.put("linkIntro", friendLink.getLinkIntro());
        map.put("approveUrl", approveUrl);
        map.put("rejectUrl", rejectUrl);
        // 构建邮件对象
        EmailDTO emailDTO = EmailDTO.builder()
                .email(email)
                .subject("友链审核提醒")
                .commentMap(map)
                .template("friend_link_review.html")
                .build();
        if (!checkEmail(email)) {
            return;
        }
        emailUtil.sendHtmlMail(emailDTO);
    }

    @Override
    public void reviewFriendLink(FriendLinkReviewVO friendLinkReviewVO) {
        Integer linkId = friendLinkReviewVO.getLinkId();
        Integer isReview = friendLinkReviewVO.getIsReview();
        FriendLink friendLink = new FriendLink();
        friendLink.setId(linkId);
        friendLink.setIsReview(isReview);
        this.updateById(friendLink);
    }

}
