package cn.lili.modules.order.cart.entity.dto;

import cn.lili.modules.member.entity.dos.MemberAddress;
import cn.lili.modules.order.cart.entity.enums.SuperpositionPromotionEnum;
import cn.lili.modules.order.order.entity.dto.PriceDetailDTO;
import cn.lili.modules.order.order.entity.vo.OrderVO;
import cn.lili.modules.order.order.entity.vo.ReceiptVO;
import cn.lili.modules.order.cart.entity.enums.CartTypeEnum;
import cn.lili.modules.order.cart.entity.vo.CartSkuVO;
import cn.lili.modules.order.cart.entity.vo.CartVO;
import cn.lili.modules.order.cart.entity.vo.PriceDetailVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 购物车视图
 *
 * @author Chopper
 * @date 2020-03-25 2:30 下午
 */
@Data
public class TradeDTO implements Serializable {

    private static final long serialVersionUID = -3137165707807057810L;

    @ApiModelProperty(value = "sn")
    private String sn;

    @ApiModelProperty(value = "是否为其他订单下的订单，如果是则为依赖订单的sn，否则为空")
    private String parentOrderSn;

    @ApiModelProperty(value = "购物车列表")
    private List<CartVO> cartList;

    @ApiModelProperty(value = "整笔交易中所有的规格商品")
    private List<CartSkuVO> skuList;

    @ApiModelProperty(value = "购物车车计算后的总价")
    private PriceDetailVO priceDetailVO;

    @ApiModelProperty(value = "购物车车计算后的总价")
    private PriceDetailDTO priceDetailDTO;

    @ApiModelProperty(value = "发票信息")
    private ReceiptVO receiptVO;

    @ApiModelProperty(value = "是否需要发票")
    private Boolean needReceipt;


    @ApiModelProperty(value = "不支持配送方式")
    private List<CartSkuVO> notSupportFreight;

    /**
     * 购物车类型
     */
    private CartTypeEnum cartTypeEnum;

    /**
     * key 为商家id
     * value 为商家优惠券
     * 商家优惠券
     */
    private Map<String, MemberCouponDTO> storeCoupons;

    /**
     * key 为商家id
     * value 为商家优惠券
     * 商家优惠券
     */
    private List<StoreRemarkDTO> storeRemark;

    /**
     * sku促销连线 包含满优惠
     * <p>
     * KEY值为 sku_id+"_"+SuperpositionPromotionEnum
     * VALUE值为 对应的活动ID
     *
     * @see SuperpositionPromotionEnum
     */
    private Map<String, String> skuPromotionDetail;

    /**
     * 使用平台优惠券，一笔订单只能使用一个平台优惠券
     */
    private MemberCouponDTO platformCoupon;

    /**
     * 收货地址
     */
    private MemberAddress memberAddress;

    /**
     * 客户端类型
     */
    private String clientType;

    /**
     * 买家名称
     */
    private String memberName;

    /**
     * 买家id
     */
    private String memberId;

    /**
     * 分销商id
     */
    private String distributionId;

    /**
     * 订单vo
     */
    private List<OrderVO> orderVO;

    public TradeDTO(CartTypeEnum cartTypeEnum) {
        this.skuList = new ArrayList<>();
        this.cartList = new ArrayList<>();
        this.skuPromotionDetail = new HashMap<>();
        this.storeCoupons = new HashMap<>();
        this.storeCoupons = new HashMap<>();
        this.priceDetailDTO = new PriceDetailDTO();
        this.cartTypeEnum = cartTypeEnum;
        this.needReceipt = false;
    }

    public TradeDTO() {
        this(CartTypeEnum.CART);
    }
}
