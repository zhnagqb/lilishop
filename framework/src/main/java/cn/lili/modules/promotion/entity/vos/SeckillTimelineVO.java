package cn.lili.modules.promotion.entity.vos;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 限时抢购时刻视图对象
 *
 * @author paulG
 * @date 2020/8/27
 **/
@Data
public class SeckillTimelineVO implements Serializable {

    private static final long serialVersionUID = -8171512491016990179L;

    @ApiModelProperty(value = "时刻")
    private Integer timeLine;

    @ApiModelProperty(value = "秒杀开始时间，这个是时间戳")
    private Long startTime;

    @ApiModelProperty(value = "距离本组活动开始的时间，秒为单位。如果活动的开始时间是10点，服务器时间为8点，距离开始还有多少时间")
    private Long distanceStartTime;

    @ApiModelProperty(value = "本组活动内的限时抢购商品列表")
    private List<SeckillGoodsVO> seckillGoodsList;

}
