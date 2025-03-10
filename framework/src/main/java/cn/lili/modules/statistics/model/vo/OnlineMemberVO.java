package cn.lili.modules.statistics.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * 在线会员
 *
 * @author Chopper
 * @date 2021-02-21 09:59
 */
@Data
@AllArgsConstructor
public class OnlineMemberVO {

    //在线时间
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH")
    private Date date;

    //在线会员人数
    private Integer num;

}
