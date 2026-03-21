package com.aurora.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendLinkAdminDTO {

    private Integer id;

    private String linkName;

    private String linkAvatar;

    private String linkAddress;

    private String linkIntro;

    private LocalDateTime createTime;

    private Integer isReview;

}
