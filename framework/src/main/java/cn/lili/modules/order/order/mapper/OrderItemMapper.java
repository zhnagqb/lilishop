package cn.lili.modules.order.order.mapper;

import cn.lili.modules.order.order.entity.dos.OrderItem;
import cn.lili.modules.order.order.entity.vo.OrderSimpleVO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 子订单数据处理层
 *
 * @author Chopper
 * @date 2020/11/17 7:34 下午
 */
public interface OrderItemMapper extends BaseMapper<OrderItem> {

    @Select("SELECT * FROM li_order_item AS oi INNER JOIN li_order AS o ON oi.order_sn=o.sn ${ew.customSqlSegment}")
    List<OrderItem> waitEvaluate(@Param(Constants.WRAPPER) Wrapper<OrderSimpleVO> queryWrapper);

}