package com.aurora.service;

import com.aurora.model.dto.FriendLinkAdminDTO;
import com.aurora.model.dto.FriendLinkDTO;
import com.aurora.entity.FriendLink;
import com.aurora.model.vo.ConditionVO;
import com.aurora.model.vo.FriendLinkReviewVO;
import com.aurora.model.vo.FriendLinkVO;
import com.aurora.model.dto.PageResultDTO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface FriendLinkService extends IService<FriendLink> {

    List<FriendLinkDTO> listFriendLinks();

    PageResultDTO<FriendLinkAdminDTO> listFriendLinksAdmin(ConditionVO conditionVO);

    void saveOrUpdateFriendLink(FriendLinkVO friendLinkVO);
    // 友链申请
    void applyLink(FriendLinkVO friendLinkVO);

    void reviewFriendLink(FriendLinkReviewVO friendLinkReviewVO);

}
