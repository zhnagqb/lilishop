package cn.lili.modules.distribution.mapper;

import cn.hutool.core.date.DateTime;
import cn.lili.modules.distribution.entity.dos.DistributionOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * 分销订单数据处理层
 *
 * @author pikachu
 * @date 2020-03-15 10:45:56
 */
public interface DistributionOrderMapper extends BaseMapper<DistributionOrder> {

    /**
     * 分销提佣
     */
    @Select("UPDATE li_distribution AS d " +
            "SET d.can_rebate =(d.can_rebate +(SELECT SUM( dorder.rebate ) FROM li_distribution_order AS dorder WHERE dorder.distribution_id = d.id AND " +
            "dorder.distribution_order_status=#{distributionOrderStatus} AND dorder.settle_cycle< #{settleCycle} ))")
    void rebate(String distributionOrderStatus, DateTime settleCycle);

}