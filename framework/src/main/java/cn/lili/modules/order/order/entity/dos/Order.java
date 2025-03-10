package cn.lili.modules.order.order.entity.dos;

import cn.hutool.json.JSONUtil;
import cn.lili.base.BaseEntity;
import cn.lili.common.utils.BeanUtil;
import cn.lili.modules.base.entity.enums.ClientTypeEnum;
import cn.lili.modules.order.cart.entity.enums.DeliveryMethodEnum;
import cn.lili.modules.order.order.entity.dto.PriceDetailDTO;
import cn.lili.modules.order.order.entity.enums.DeliverStatusEnum;
import cn.lili.modules.order.order.entity.enums.OrderStatusEnum;
import cn.lili.modules.order.order.entity.enums.OrderTypeEnum;
import cn.lili.modules.order.order.entity.enums.PayStatusEnum;
import cn.lili.modules.promotion.entity.dos.PromotionGoods;
import cn.lili.modules.promotion.entity.enums.PromotionTypeEnum;
import cn.lili.modules.order.cart.entity.dto.TradeDTO;
import cn.lili.modules.order.cart.entity.vo.CartVO;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import java.util.Optional;

/**
 * 订单
 *
 * @author Chopper
 * @date 2020/11/17 7:30 下午
 */
@Data
@Entity
@Table(name = "li_order")
@TableName("li_order")
@ApiModel(value = "订单")
public class Order extends BaseEntity {


    private static final long serialVersionUID = 2233811628066468683L;
    @ApiModelProperty("订单编号")
    private String sn;

    @ApiModelProperty("交易编号 关联Trade")
    private String tradeSn;

    @ApiModelProperty(value = "店铺ID")
    private String storeId;

    @ApiModelProperty(value = "店铺名称")
    private String storeName;

    @ApiModelProperty(value = "会员ID")
    private String memberId;

    @ApiModelProperty(value = "用户名")
    private String memberName;

    /**
     * @see OrderStatusEnum
     */
    @ApiModelProperty(value = "订单状态")
    private String orderStatus;

    /**
     * @see PayStatusEnum
     */
    @ApiModelProperty(value = "付款状态")
    private String payStatus;
    /**
     * @see DeliverStatusEnum
     */
    @ApiModelProperty(value = "货运状态")
    private String deliverStatus;

    @ApiModelProperty(value = "第三方付款流水号")
    private String receivableNo;

    @ApiModelProperty(value = "支付方式")
    private String paymentMethod;

    @ApiModelProperty(value = "支付时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date paymentTime;

    @ApiModelProperty(value = "收件人姓名")
    private String consigneeName;

    @ApiModelProperty(value = "收件人手机")
    private String consigneeMobile;

    /**
     * @see DeliveryMethodEnum
     */
    @ApiModelProperty(value = "配送方式")
    private String deliveryMethod;

    @ApiModelProperty(value = "地址名称， '，'分割")
    private String consigneeAddressPath;

    @ApiModelProperty(value = "地址id，'，'分割 ")
    private String consigneeAddressIdPath;

    @ApiModelProperty(value = "详细地址")
    private String consigneeDetail;

    @ApiModelProperty(value = "总价格")
    private Double flowPrice;

    @ApiModelProperty(value = "商品价格")
    private Double goodsPrice;

    @ApiModelProperty(value = "运费")
    private Double freightPrice;

    @ApiModelProperty(value = "优惠的金额")
    private Double discountPrice;

    //修改金额
    @ApiModelProperty(value = "修改价格")
    private Double updatePrice;

    @ApiModelProperty(value = "发货单号")
    private String logisticsNo;

    @ApiModelProperty(value = "物流公司CODE")
    private String logisticsCode;

    @ApiModelProperty(value = "物流公司名称")
    private String logisticsName;

    @ApiModelProperty(value = "订单商品总重量")
    private Double weight;

    @ApiModelProperty(value = "商品数量")
    private Integer goodsNum;

    @ApiModelProperty(value = "买家订单备注")
    private String remark;

    @ApiModelProperty(value = "订单取消原因")
    private String cancelReason;

    @ApiModelProperty(value = "完成时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date completeTime;

    @ApiModelProperty(value = "送货时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date logisticsTime;

    @ApiModelProperty(value = "支付方式返回的交易号")
    private String payOrderNo;

    /**
     * @see ClientTypeEnum
     */
    @ApiModelProperty(value = "订单来源")
    private String clientType;

    @ApiModelProperty(value = "是否需要发票")
    private Boolean needReceipt;

    @ApiModelProperty(value = "是否为其他订单下的订单，如果是则为依赖订单的sn，否则为空")
    private String parentOrderSn;

    @ApiModelProperty(value = "是否为某订单类型的订单，如果是则为订单类型的id，否则为空")
    private String promotionId;

    /**
     * @see OrderTypeEnum
     */
    @ApiModelProperty(value = "订单类型")
    private String orderType;

    @Column(columnDefinition = "TEXT")
    @ApiModelProperty(value = "价格详情")
    private String priceDetail;

    @ApiModelProperty(value = "订单是否支持原路退回")
    private Boolean canReturn;

    @ApiModelProperty(value = "提货码")
    private String qrCode;

    @ApiModelProperty(value = "分销员ID")
    private String distributionId;

    @ApiModelProperty(value = "使用的店铺会员优惠券id(,区分)")
    private String useStoreMemberCouponIds;

    @ApiModelProperty(value = "使用的平台会员优惠券id")
    private String usePlatformMemberCouponId;

    public Order() {

    }

    public Order(CartVO cartVO, TradeDTO tradeDTO) {
        String oldId = this.getId();
        if (tradeDTO.getMemberAddress() != null) {
            BeanUtil.copyProperties(tradeDTO.getMemberAddress(), this);
        }
        BeanUtil.copyProperties(tradeDTO, this);
        BeanUtil.copyProperties(cartVO.getPriceDetailDTO(), this);
        BeanUtil.copyProperties(cartVO, this);
        this.setId(oldId);
        this.setOrderType(OrderTypeEnum.NORMAL.name());
        //促销信息填充
        if (cartVO.getSkuList().get(0).getPromotions() != null) {
            Optional<String> pintuanId = cartVO.getSkuList().get(0).getPromotions().stream().filter(i -> i.getPromotionType().equals(PromotionTypeEnum.PINTUAN.name())).map(PromotionGoods::getPromotionId).findFirst();
            if (pintuanId.isPresent()) {
                promotionId = pintuanId.get();
                this.setOrderType(OrderTypeEnum.PINTUAN.name());
                if (tradeDTO.getParentOrderSn() == null) {
                    this.setParentOrderSn("");
                }
            }
        }

        //设置默认支付状态
        this.setOrderStatus(OrderStatusEnum.UNPAID.name());
        this.setPayStatus(PayStatusEnum.UNPAID.name());
        this.setDeliverStatus(DeliverStatusEnum.UNDELIVERED.name());
        //如果有收货地址，才记录收货地址
        if (tradeDTO.getMemberAddress() != null) {
            this.setConsigneeAddressIdPath(tradeDTO.getMemberAddress().getConsigneeAddressIdPath());
            this.setConsigneeAddressPath(tradeDTO.getMemberAddress().getConsigneeAddressPath());
            this.setConsigneeDetail(tradeDTO.getMemberAddress().getDetail());
            this.setConsigneeMobile(tradeDTO.getMemberAddress().getMobile());
            this.setConsigneeName(tradeDTO.getMemberAddress().getName());
        }
        //平台优惠券判定
        if (tradeDTO.getPlatformCoupon() != null) {
            this.setUsePlatformMemberCouponId(tradeDTO.getPlatformCoupon().getMemberCoupon().getId());
        }
        //店铺优惠券判定
        if (tradeDTO.getStoreCoupons() != null && !tradeDTO.getStoreCoupons().isEmpty()) {
            StringBuilder storeCouponIds = new StringBuilder();
            for (String s : tradeDTO.getStoreCoupons().keySet()) {
                storeCouponIds.append(s).append(",");
            }
            this.setUseStoreMemberCouponIds(storeCouponIds.toString());
        }
        this.setTradeSn(tradeDTO.getSn());
        this.setRemark(cartVO.getRemark());
        this.setFreightPrice(tradeDTO.getPriceDetailDTO().getFreightPrice());
    }

    public PriceDetailDTO getPriceDetailDTO() {

        try {
            return JSONUtil.toBean(priceDetail, PriceDetailDTO.class);
        } catch (Exception e) {
            return null;
        }
    }

    public void setPriceDetailDTO(PriceDetailDTO priceDetail) {
        this.priceDetail = JSONUtil.toJsonStr(priceDetail);
    }

}