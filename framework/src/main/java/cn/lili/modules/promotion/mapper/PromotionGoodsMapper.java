package cn.lili.modules.promotion.mapper;

import cn.lili.modules.promotion.entity.dos.PromotionGoods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;

/**
 * 促销商品数据处理层
 *
 * @author Chopper
 * @date 2020/8/21
 */
public interface PromotionGoodsMapper extends BaseMapper<PromotionGoods> {


    /**
     * 查询参加活动促销商品是否同时参加指定类型的活动
     *
     * @param promotionType 促销类型
     * @param skuId         skuId
     * @param startTime     开始时间
     * @param endTime       结束时间
     * @return 共参加了几种活动
     */
    @Select("select count(0) from li_promotion_goods where promotion_type = #{promotionType} and sku_id = #{skuId} and (" +
            "( start_time < #{startTime}  && end_time > #{startTime} ) || ( start_time < #{endTime}  && end_time > #{endTime} ) || " +
            "( start_time < #{startTime}  && end_time > #{endTime} ) || ( start_time > #{startTime}  && end_time < #{endTime} )" +
            " || promotion_status = 'START' )")
    Integer selectInnerOverlapPromotionGoods(@Param("promotionType") String promotionType,
                                             @Param("skuId") String skuId,
                                             @Param("startTime") Date startTime,
                                             @Param("endTime") Date endTime);

    /**
     * 查询参加活动促销商品是否同时参加指定类型的活动
     *
     * @param promotionType 促销类型
     * @param skuId         skuId
     * @param startTime     开始时间
     * @param endTime       结束时间
     * @return 共参加了几种活动
     */
    @Select("select count(0) from li_promotion_goods where promotion_type = #{promotionType} and sku_id = #{skuId} and (" +
            "( start_time < #{startTime}  && end_time > #{startTime} ) || ( start_time < #{endTime}  && end_time > #{endTime} ) || " +
            "( start_time < #{startTime}  && end_time > #{endTime} ) || ( start_time > #{startTime}  && end_time < #{endTime} )" +
            " || promotion_status = 'START' ) and promotion_id != #{promotionId}")
    Integer selectInnerOverlapPromotionGoodsWithout(@Param("promotionType") String promotionType,
                                             @Param("skuId") String skuId,
                                             @Param("startTime") Date startTime,
                                             @Param("endTime") Date endTime,
                                             @Param("promotionId") String promotionId);
}