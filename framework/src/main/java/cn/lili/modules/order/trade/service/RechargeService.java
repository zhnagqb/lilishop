package cn.lili.modules.order.trade.service;

import cn.lili.common.vo.PageVO;
import cn.lili.modules.order.trade.entity.dos.Recharge;
import cn.lili.modules.order.trade.entity.vo.RechargeQueryVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 预存款充值记录业务层
 *
 * @author pikachu
 * @date 2020-02-25 14:10:16
 */
public interface RechargeService extends IService<Recharge> {

    /**
     * 创建充值订单
     *
     * @param price 价格
     * @return 预存款充值记录
     */
    Recharge recharge(Double price);

    /**
     * 查询充值订单列表
     *
     * @param page            分页数据
     * @param rechargeQueryVO 查询条件
     * @return
     */
    IPage<Recharge> rechargePage(PageVO page, RechargeQueryVO rechargeQueryVO);


    /**
     * 支付成功
     *
     * @param sn    充值订单编号
     * @param receivableNo 流水no
     */
    void paySuccess(String sn, String receivableNo);

    /**
     * 根据充值订单号查询充值信息
     *
     * @param sn 充值订单号
     * @return
     */
    Recharge getRecharge(String sn);

}