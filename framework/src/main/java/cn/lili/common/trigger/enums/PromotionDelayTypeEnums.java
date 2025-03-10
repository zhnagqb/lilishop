package cn.lili.common.trigger.enums;

/**
 * 延时任务类型
 *
 * @author paulG
 * @since 2021/5/7
 */
public enum PromotionDelayTypeEnums {

    /**
     * 促销活动
     */
    PROMOTION("促销活动"),
    /**
     * 拼团订单
     */
    PINTUAN_ORDER("拼团订单");

    private String description;

    PromotionDelayTypeEnums(String description) {
        this.description = description;
    }

}
