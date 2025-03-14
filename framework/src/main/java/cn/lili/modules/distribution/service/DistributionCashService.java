package cn.lili.modules.distribution.service;

import cn.lili.common.vo.PageVO;
import cn.lili.modules.distribution.entity.dos.DistributionCash;
import cn.lili.modules.distribution.entity.vos.DistributionCashSearchParams;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 分销佣金业务层
 *
 * @author pikachu
 * @date 2020-03-14 23:04:56
 */
public interface DistributionCashService extends IService<DistributionCash> {

    /**
     * 提交分销提现申请
     *
     * @param applyMoney
     */
    Boolean cash(Double applyMoney);

    /**
     * 获取当前会员的分销提现分页列表
     *
     * @return
     */
    IPage<DistributionCash> getDistributionCash(PageVO page);

    /**
     * 获取分销员提现分页列表
     *
     * @param distributionCashSearchParams 搜索条件
     * @return 分销员提现分页列表
     */
    IPage<DistributionCash> getDistributionCash(DistributionCashSearchParams distributionCashSearchParams);

    /**
     * 审核分销提现申请
     *
     * @param id     分销提现申请ID
     * @param result 处理结果
     * @return 分销提现申请
     */
    DistributionCash audit(@PathVariable String id, @RequestParam String result);

    /**
     * 待处理分销员提现申请数量
     *
     * @return 待处理分销员提现申请数量
     */
    Integer newDistributionCash();
}