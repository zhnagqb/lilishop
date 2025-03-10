package cn.lili.modules.member.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 会员积分
 *
 * @author Bulbasaur
 * @date 2020/12/14 16:31
 */
@Data
public class MemberPointMessage {

    @ApiModelProperty(value = "积分")
    private Long point;

    @ApiModelProperty(value = "类型 1为增加")
    private Integer type;

    @ApiModelProperty(value = "会员id")
    private String memberId;
}
