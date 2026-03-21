package com.aurora.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "友链审核")
public class FriendLinkReviewVO {
    @NotNull(message = "友链ID不能为空")
    @ApiModelProperty(name = "linkId", value = "友链ID", required = true, dataType = "Integer")
    private Integer linkId;
    @NotNull(message = "审核状态不能为空")
    @ApiModelProperty(name = "isReview", value = "审核状态", required = true, dataType = "Integer")
    private Integer isReview;
}
