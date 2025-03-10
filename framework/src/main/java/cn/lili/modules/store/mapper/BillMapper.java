package cn.lili.modules.store.mapper;


import cn.lili.modules.store.entity.dos.Bill;
import cn.lili.modules.store.entity.vos.BillListVO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 结算单数据处理层
 *
 * @author Chopper
 * @date 2020/11/17 4:27 下午
 */
public interface BillMapper extends BaseMapper<Bill> {

    @Select("select b.id,b.sn,b.start_time,b.end_time,b.bill_status,b.store_name,b.bill_price,b.create_time from li_bill as b ${ew.customSqlSegment}")
    IPage<BillListVO> queryBillPage(IPage<BillListVO> page, @Param(Constants.WRAPPER) Wrapper<BillListVO> queryWrapper);

    @Select("SELECT SUM( final_price ) AS orderPrice,SUM( commission_price ) AS commissionPrice" +
            ",SUM( distribution_rebate ) AS distributionCommission,SUM( site_coupon_commission ) AS siteCouponCommission" +
            ",SUM( bill_price ) AS billPrice FROM li_store_flow ${ew.customSqlSegment}")
    Bill getOrderBill(@Param(Constants.WRAPPER) QueryWrapper<Bill> queryWrapper);

    @Select("SELECT SUM( final_price ) AS refundPrice,SUM( commission_price ) AS refundCommissionPrice" +
            ",SUM( distribution_rebate ) AS distributionRefundCommission,SUM( site_coupon_commission ) AS siteCouponRefundCommission" +
            ",SUM( bill_price ) AS billPrice FROM li_store_flow ${ew.customSqlSegment}")
    Bill getRefundBill(@Param(Constants.WRAPPER) QueryWrapper<Bill> queryWrapper);
}