package cn.lili.modules.member.entity.dos;

import cn.lili.base.BaseEntity;
import cn.lili.modules.message.entity.enums.MessageStatusEnum;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 会员消息
 *
 * @author Chopper
 * @date 2020-02-25 14:10:16
 */
@Data
@Entity
@Table(name = "li_member_message")
@TableName("li_member_message")
@ApiModel(value = "会员消息")
public class MemberMessage extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "会员id")
    private String memberId;

    @ApiModelProperty(value = "会员名称")
    private String memberName;

    @ApiModelProperty(value = "消息标题")
    private String title;

    @ApiModelProperty(value = "消息内容")
    private String content;

    /**
     * @see MessageStatusEnum
     */
    @ApiModelProperty(value = "状态 0默认未读 1已读 2回收站")
    private String status = MessageStatusEnum.UN_READY.name();

}