package com.aurora.controller;

import com.aurora.annotation.OptLog;
import com.aurora.entity.FriendLink;
import com.aurora.enums.FilePathEnum;
import com.aurora.model.dto.FriendLinkAdminDTO;
import com.aurora.model.dto.FriendLinkDTO;
import com.aurora.model.vo.FriendLinkReviewVO;
import com.aurora.model.vo.ResultVO;
import com.aurora.service.FriendLinkService;
import com.aurora.model.vo.ConditionVO;
import com.aurora.model.vo.FriendLinkVO;
import com.aurora.model.dto.PageResultDTO;
import com.aurora.strategy.context.UploadStrategyContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

import static com.aurora.constant.OptTypeConstant.*;

@Api(tags = "友链模块")
@RestController
public class FriendLinkController {

    @Autowired
    private FriendLinkService friendLinkService;

    @Autowired
    private UploadStrategyContext uploadStrategyContext;

    @ApiOperation(value = "上传友链头像")
    @ApiImplicitParam(name = "file", value = "友链头像", required = true, dataType = "MultipartFile")
    @PostMapping("/links/avatar")
    public ResultVO<String> uploadLinkAvatar(MultipartFile file) {
        return ResultVO.ok(uploadStrategyContext.executeUploadStrategy(file, FilePathEnum.CONFIG.getPath()));
    }

    @ApiOperation(value = "申请友链")
    @PostMapping("/add/links")
    public ResultVO<?> applyLink(@RequestBody @Valid FriendLinkVO friendLinkVO) {
        friendLinkService.applyLink(friendLinkVO);
        return ResultVO.ok();
    }
    @ApiOperation(value = "查看友链列表")
    @GetMapping("/links")
    public ResultVO<List<FriendLinkDTO>> listFriendLinks() {
        return ResultVO.ok(friendLinkService.listFriendLinks());
    }

    @ApiOperation(value = "查看后台友链列表")
    @GetMapping("/admin/links")
    public ResultVO<PageResultDTO<FriendLinkAdminDTO>> listFriendLinkDTO(ConditionVO conditionVO) {
        return ResultVO.ok(friendLinkService.listFriendLinksAdmin(conditionVO));
    }

    @OptLog(optType = SAVE_OR_UPDATE)
    @ApiOperation(value = "保存或修改友链")
    @PostMapping("/admin/links")
    public ResultVO<?> saveOrUpdateFriendLink(@Valid @RequestBody FriendLinkVO friendLinkVO) {
        friendLinkService.saveOrUpdateFriendLink(friendLinkVO);
        return ResultVO.ok();
    }
    @OptLog(optType = DELETE)
    @ApiOperation(value = "删除友链")
    @DeleteMapping("/admin/links")
    public ResultVO<?> deleteFriendLink(@RequestBody List<Integer> linkIdList) {
        friendLinkService.removeByIds(linkIdList);
        return ResultVO.ok();
    }
    @ApiOperation(value = "审核友链")
    @GetMapping("/review/{linkId}")
    public ResultVO<FriendLinkAdminDTO> reviewLink(@PathVariable Integer linkId, @RequestParam String action) {
        if (linkId == null || action == null) {
            return ResultVO.fail("友链ID和操作类型不能为空");
        }
        // 检查该友链是否已经审核过
        FriendLink friendLink = friendLinkService.getById(linkId);
        if (friendLink == null) {
            System.out.println("友链ID无效");
            return ResultVO.fail("友链ID无效");
        }
        if (friendLink.getIsReview() != null && friendLink.getIsReview() != 0) {
            System.out.println("该友链已审核，无法重复操作");
            return ResultVO.fail("该友链已审核，无法重复操作");
        }
        // 处理审核操作（同意/拒绝）
        if ("approve".equals(action)) {
            // 同意逻辑
            friendLinkService.reviewFriendLink(new FriendLinkReviewVO(linkId, 1));
            System.out.println("友链审核通过");
            return ResultVO.ok();
        } else if ("reject".equals(action)) {
            // 拒绝逻辑
            friendLinkService.reviewFriendLink(new FriendLinkReviewVO(linkId, 2));
            System.out.println("友链审核不通过");
            return ResultVO.ok();
        }
        System.out.println("无效的操作类型");
        return ResultVO.fail("无效的操作类型");
    }
}
