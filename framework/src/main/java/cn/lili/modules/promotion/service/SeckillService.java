package cn.lili.modules.promotion.service;

import cn.lili.common.vo.PageVO;
import cn.lili.modules.promotion.entity.dos.Seckill;
import cn.lili.modules.promotion.entity.vos.SeckillSearchParams;
import cn.lili.modules.promotion.entity.vos.SeckillVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 秒杀业务层
 *
 * @author Chopper
 * @date 2020/11/18 9:45 上午
 */
public interface SeckillService extends IService<Seckill> {


    /**
     * 从mysql中根据条件获取限时抢购分页列表
     *
     * @param queryParam 查询参数
     * @param pageVo     分页参数
     * @return 限时抢购分页列表
     */
    IPage<Seckill> getSeckillByPageFromMysql(SeckillSearchParams queryParam, PageVO pageVo);

    /**
     * 从mongo中根据条件获取限时抢购分页列表
     *
     * @param queryParam 查询参数
     * @param pageVo     分页参数
     * @return 限时抢购分页列表
     */
    IPage<SeckillVO> getSeckillByPageFromMongo(SeckillSearchParams queryParam, PageVO pageVo);

    /**
     * 从mongo中获取限时抢购信息
     *
     * @param id 限时抢购id
     * @return 限时抢购信息
     */
    SeckillVO getSeckillByIdFromMongo(String id);

    /**
     * 保存限时抢购
     *
     * @param seckill 限时抢购信息
     * @return 是否保存成功
     */
    boolean saveSeckill(SeckillVO seckill);

    /**
     * 商家报名限时抢购活动
     *
     * @param storeId   商家编号
     * @param seckillId 限时抢购编号
     */
    void storeApply(String storeId, String seckillId);

    /**
     * 修改限时抢购
     *
     * @param seckillVO 限时抢购信息
     * @return 是否修改成功
     */
    boolean modifySeckill(SeckillVO seckillVO);

    /**
     * 删除限时抢购
     *
     * @param id 限时抢购编号
     */
    void deleteSeckill(String id);

    /**
     * 开启一个限时抢购
     *
     * @param id 限时抢购编号
     */
    void openSeckill(String id);

    /**
     * 关闭一个限时抢购
     *
     * @param id 限时抢购编号
     */
    void closeSeckill(String id);

    /**
     * 获取当前可参与的活动数量
     * @return 可参与活动数量
     */
    Integer getApplyNum();
}