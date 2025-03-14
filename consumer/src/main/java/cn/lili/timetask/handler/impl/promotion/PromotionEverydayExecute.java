package cn.lili.timetask.handler.impl.promotion;

import cn.lili.modules.order.cart.entity.vo.FullDiscountVO;
import cn.lili.modules.promotion.entity.dos.MemberCoupon;
import cn.lili.modules.promotion.entity.dos.PromotionGoods;
import cn.lili.modules.promotion.entity.enums.MemberCouponStatusEnum;
import cn.lili.modules.promotion.entity.enums.PromotionStatusEnum;
import cn.lili.modules.promotion.entity.vos.CouponVO;
import cn.lili.modules.promotion.entity.vos.PintuanVO;
import cn.lili.modules.promotion.service.*;
import cn.lili.modules.search.service.EsGoodsIndexService;
import cn.lili.timetask.handler.EveryDayExecute;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 促销活动每日定时器
 *
 * @author Chopper
 * @date 2021/3/18 3:23 下午
 */
@Component

public class PromotionEverydayExecute implements EveryDayExecute {

    //Mongo
    @Autowired
    private MongoTemplate mongoTemplate;
    //es
    @Autowired
    private EsGoodsIndexService esGoodsIndexService;
    //满额活动
    @Autowired
    private FullDiscountService fullDiscountService;
    //拼团
    @Autowired
    private PintuanService pintuanService;
    //优惠券
    @Autowired
    private CouponService couponService;
    //会员优惠券
    @Autowired
    private MemberCouponService memberCouponService;
    //促销商品
    @Autowired
    private PromotionGoodsService promotionGoodsService;


    /**
     * 将已过期的促销活动置为结束
     */
    @Override
    public void execute() {

        Query query = new Query();
//        结束条件 活动关闭/活动结束
        query.addCriteria(Criteria.where("promotionStatus").ne(PromotionStatusEnum.END.name())
                .orOperator(Criteria.where("promotionStatus").ne(PromotionStatusEnum.CLOSE.name())));
        query.addCriteria(Criteria.where("endTime").lt(new Date()));

        List<String> promotionIds = new ArrayList<>();

        //关闭满减活动
        List<FullDiscountVO> fullDiscountVOS = mongoTemplate.find(query, FullDiscountVO.class);
        if (!fullDiscountVOS.isEmpty()) {
            List<String> ids = new ArrayList<>();
            for (FullDiscountVO vo : fullDiscountVOS) {
                vo.setPromotionStatus(PromotionStatusEnum.END.name());
                if (vo.getPromotionGoodsList() != null && !vo.getPromotionGoodsList().isEmpty()) {
                    for (PromotionGoods promotionGoods : vo.getPromotionGoodsList()) {
                        promotionGoods.setPromotionStatus(PromotionStatusEnum.END.name());
                        esGoodsIndexService.deleteEsGoodsPromotionByPromotionId(promotionGoods.getSkuId(), vo.getId());
                    }
                }
                mongoTemplate.save(vo);
                ids.add(vo.getId());
            }
            fullDiscountService.update(this.getUpdatePromotionWrapper(ids));
            promotionIds.addAll(ids);
        }
        //关闭拼团活动
        List<PintuanVO> pintuanVOS = mongoTemplate.find(query, PintuanVO.class);
        if (!pintuanVOS.isEmpty()) {
            //准备修改活动的id
            List<String> ids = new ArrayList<>();
            for (PintuanVO vo : pintuanVOS) {
                vo.setPromotionStatus(PromotionStatusEnum.END.name());
                if (vo.getPromotionGoodsList() != null && !vo.getPromotionGoodsList().isEmpty()) {
                    for (PromotionGoods promotionGoods : vo.getPromotionGoodsList()) {
                        promotionGoods.setPromotionStatus(PromotionStatusEnum.END.name());
                        esGoodsIndexService.deleteEsGoodsPromotionByPromotionId(promotionGoods.getSkuId(), vo.getId());
                    }
                }
                mongoTemplate.save(vo);
                ids.add(vo.getId());
            }
            pintuanService.update(this.getUpdatePromotionWrapper(ids));
            promotionIds.addAll(ids);
        }

        //关闭优惠券活动
        List<CouponVO> couponVOS = mongoTemplate.find(query, CouponVO.class);
        if (!couponVOS.isEmpty()) {
            List<String> ids = new ArrayList<>();
            for (CouponVO vo : couponVOS) {
                vo.setPromotionStatus(PromotionStatusEnum.END.name());
                if (vo.getPromotionGoodsList() != null && !vo.getPromotionGoodsList().isEmpty()) {
                    for (PromotionGoods promotionGoods : vo.getPromotionGoodsList()) {
                        promotionGoods.setPromotionStatus(PromotionStatusEnum.END.name());
                        esGoodsIndexService.deleteEsGoodsPromotionByPromotionId(promotionGoods.getSkuId(), vo.getId());
                    }
                }
                mongoTemplate.save(vo);
                ids.add(vo.getId());
            }
            couponService.update(this.getUpdatePromotionWrapper(ids));
            LambdaUpdateWrapper<MemberCoupon> memberCouponLambdaUpdateWrapper = new LambdaUpdateWrapper<MemberCoupon>().in(MemberCoupon::getCouponId, ids).set(MemberCoupon::getMemberCouponStatus, MemberCouponStatusEnum.EXPIRE.name());
            memberCouponService.update(memberCouponLambdaUpdateWrapper);
            promotionIds.addAll(ids);
        }

        promotionGoodsService.update(this.getUpdatePromotionGoodsWrapper(promotionIds));

    }

    /**
     * 获取促销修改查询条件 修改活动状态
     * @param ids
     * @return
     */
    private UpdateWrapper getUpdatePromotionWrapper(List<String> ids) {
        UpdateWrapper updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", ids);
        updateWrapper.set("promotion_status", PromotionStatusEnum.END.name());
        return updateWrapper;
    }
    /**
     * 获取商品的促销修改查询条件 修改商品状态
     * @param ids
     * @return
     */
    private UpdateWrapper getUpdatePromotionGoodsWrapper(List<String> ids) {
        UpdateWrapper updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("promotion_id", ids);
        updateWrapper.set("promotion_status", PromotionStatusEnum.END.name());
        return updateWrapper;
    }
}
