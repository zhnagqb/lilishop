package cn.lili.modules.payment.kit.plugin.alipay;

import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.net.URLEncoder;
import cn.hutool.json.JSONUtil;
import cn.lili.common.enums.ResultCode;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.enums.ResultUtil;
import cn.lili.common.utils.SnowFlake;
import cn.lili.common.utils.StringUtils;
import cn.lili.common.vo.ResultMessage;
import cn.lili.config.properties.ApiProperties;
import cn.lili.modules.payment.entity.RefundLog;
import cn.lili.modules.payment.kit.CashierSupport;
import cn.lili.modules.payment.kit.Payment;
import cn.lili.modules.payment.kit.dto.PayParam;
import cn.lili.modules.payment.kit.dto.PaymentSuccessParams;
import cn.lili.modules.payment.kit.enums.PaymentMethodEnum;
import cn.lili.modules.payment.kit.params.dto.CashierParam;
import cn.lili.modules.payment.service.PaymentService;
import cn.lili.modules.payment.service.RefundLogService;
import cn.lili.modules.system.entity.dos.Setting;
import cn.lili.modules.system.entity.dto.payment.AlipayPaymentSetting;
import cn.lili.modules.system.entity.enums.SettingEnum;
import cn.lili.modules.system.service.SettingService;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.*;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.AlipayTradeCancelResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 支付宝支付
 *
 * @author Chopper
 * @date 2020/12/17 09:55
 */
@Slf4j
@Component
public class AliPayPlugin implements Payment {
    //支付日志
    @Autowired
    private PaymentService paymentService;
    //退款日志
    @Autowired
    private RefundLogService refundLogService;
    //收银台
    @Autowired
    private CashierSupport cashierSupport;
    //设置
    @Autowired
    private SettingService settingService;
    //API域名
    @Autowired
    private ApiProperties apiProperties;


    @Override
    public ResultMessage<Object> h5pay(HttpServletRequest request, HttpServletResponse response, PayParam payParam) {


        CashierParam cashierParam = cashierSupport.cashierParam(payParam);
        //请求订单编号
        String outTradeNo = SnowFlake.getIdStr();
        //准备支付参数
        AlipayTradeWapPayModel payModel = new AlipayTradeWapPayModel();
        payModel.setBody(cashierParam.getTitle());
        payModel.setSubject(cashierParam.getDetail());
        payModel.setTotalAmount(cashierParam.getPrice() + "");
        //回传数据
        payModel.setPassbackParams(URLEncoder.createAll().encode(JSONUtil.toJsonStr(payParam), StandardCharsets.UTF_8));
        //3分钟超时
        payModel.setTimeoutExpress("3m");
        payModel.setOutTradeNo(outTradeNo);
        payModel.setProductCode("QUICK_WAP_PAY");
        try {
            AliPayRequest.wapPay(response, payModel, callbackUrl(apiProperties.getBuyer(), PaymentMethodEnum.ALIPAY),
                    notifyUrl(apiProperties.getBuyer(), PaymentMethodEnum.ALIPAY));
        } catch (Exception e) {
            log.error("H5支付异常", e);
            throw new ServiceException(ResultCode.ALIPAY_EXCEPTION);
        }
        return null;
    }


    @Override
    public ResultMessage<Object> JSApiPay(HttpServletRequest request, PayParam payParam) {
        throw new ServiceException("当前支付通道暂不支持");
    }

    @Override
    public ResultMessage<Object> appPay(HttpServletRequest request, PayParam payParam) {
        try {

            CashierParam cashierParam = cashierSupport.cashierParam(payParam);
            //请求订单编号
            String outTradeNo = SnowFlake.getIdStr();

            AlipayTradeAppPayModel payModel = new AlipayTradeAppPayModel();

            payModel.setBody(cashierParam.getTitle());
            payModel.setSubject(cashierParam.getDetail());
            payModel.setTotalAmount(cashierParam.getPrice() + "");

            //3分钟超时
            payModel.setTimeoutExpress("3m");
            //回传数据
            payModel.setPassbackParams(URLEncoder.createAll().encode(JSONUtil.toJsonStr(payParam), StandardCharsets.UTF_8));
            payModel.setOutTradeNo(outTradeNo);
            payModel.setProductCode("QUICK_MSECURITY_PAY");

            String orderInfo = AliPayRequest.appPayToResponse(payModel, notifyUrl(apiProperties.getBuyer(), PaymentMethodEnum.ALIPAY)).getBody();
            return ResultUtil.data(orderInfo);
        } catch (AlipayApiException e) {
            log.error("支付宝支付异常：", e);
            throw new ServiceException(ResultCode.ALIPAY_EXCEPTION);
        } catch (Exception e) {
            log.error("支付业务异常：", e);
            throw new ServiceException(ResultCode.PAY_ERROR);
        }
    }

    @Override
    public ResultMessage<Object> nativePay(HttpServletRequest request, PayParam payParam) {

        try {
            CashierParam cashierParam = cashierSupport.cashierParam(payParam);

            AlipayTradePrecreateModel payModel = new AlipayTradePrecreateModel();

            //请求订单编号
            String outTradeNo = SnowFlake.getIdStr();

            payModel.setBody(cashierParam.getTitle());
            payModel.setSubject(cashierParam.getDetail());
            payModel.setTotalAmount(cashierParam.getPrice() + "");

            //回传数据
            payModel.setPassbackParams(URLEncoder.createAll().encode(JSONUtil.toJsonStr(payParam), StandardCharsets.UTF_8));
//        payModel.setStoreId("store_id");
            payModel.setTimeoutExpress("3m");
            payModel.setOutTradeNo(outTradeNo);

            String resultStr = AliPayRequest.tradePrecreatePayToResponse(payModel, notifyUrl(apiProperties.getBuyer(), PaymentMethodEnum.ALIPAY)).getBody();
            JSONObject jsonObject = JSONObject.parseObject(resultStr);
            return ResultUtil.data(jsonObject.getJSONObject("alipay_trade_precreate_response").getString("qr_code"));
        } catch (Exception e) {
            log.error("支付业务异常：", e);
            throw new ServiceException(ResultCode.PAY_ERROR);
        }
    }


    @Override
    public void refund(RefundLog refundLog) {
        AlipayTradeRefundModel model = new AlipayTradeRefundModel();
        //这里取支付回调时返回的流水
        if (StringUtils.isNotEmpty(refundLog.getPaymentReceivableNo())) {
            model.setTradeNo(refundLog.getPaymentReceivableNo());
        } else {
            throw new ServiceException(ResultCode.ALIPAY_PARAMS_EXCEPTION);
        }
        model.setRefundAmount(refundLog.getTotalAmount() + "");
        model.setRefundReason(refundLog.getRefundReason());
        model.setOutRequestNo(refundLog.getOutOrderNo());
        //交互退款
        try {
            AlipayTradeRefundResponse alipayTradeRefundResponse = AliPayApi.tradeRefundToResponse(model);
            log.error("支付宝退款，参数：{},支付宝响应：{}", JSONUtil.toJsonStr(model), JSONUtil.toJsonStr(alipayTradeRefundResponse));
            if (alipayTradeRefundResponse.isSuccess()) {
                refundLog.setIsRefund(true);
                refundLog.setReceivableNo(refundLog.getOutOrderNo());
            } else {
                refundLog.setErrorMessage(String.format("错误码：%s,错误原因：%s", alipayTradeRefundResponse.getSubCode(), alipayTradeRefundResponse.getSubMsg()));
            }
            refundLogService.save(refundLog);
        } catch (Exception e) {
            log.error("支付退款异常：", e);
            throw new ServiceException(ResultCode.PAY_ERROR);
        }

    }

    @Override
    public void cancel(RefundLog refundLog) {
        AlipayTradeCancelModel model = new AlipayTradeCancelModel();
        //这里取支付回调时返回的流水
        if (StringUtils.isNotEmpty(refundLog.getPaymentReceivableNo())) {
            model.setTradeNo(refundLog.getPaymentReceivableNo());
        } else {
            log.error("退款时，支付参数为空导致异常：{}", refundLog);
            throw new ServiceException(ResultCode.ALIPAY_PARAMS_EXCEPTION);
        }
        try {
            //与阿里进行交互
            AlipayTradeCancelResponse alipayTradeCancelResponse = AliPayApi.tradeCancelToResponse(model);
            if (alipayTradeCancelResponse.isSuccess()) {
                refundLog.setIsRefund(true);
                refundLog.setReceivableNo(refundLog.getOutOrderNo());
            } else {
                refundLog.setErrorMessage(String.format("错误码：%s,错误原因：%s", alipayTradeCancelResponse.getSubCode(), alipayTradeCancelResponse.getSubMsg()));
            }
            refundLogService.save(refundLog);
        } catch (Exception e) {
            log.error("支付宝退款异常",e);
        }
    }

    @Override
    public void refundNotify(HttpServletRequest request) {
        //不需要实现
    }

    @Override
    public void callBack(HttpServletRequest request) {
        verifyNotify(request);
        log.info("支付同步回调：");
    }

    @Override
    public void notify(HttpServletRequest request) {
        verifyNotify(request);
        log.info("支付异步通知：");
    }

    /**
     * 验证支付结果
     *
     * @param request
     */
    private void verifyNotify(HttpServletRequest request) {
        try {
            AlipayPaymentSetting alipayPaymentSetting = alipayPaymentSetting();
            // 获取支付宝反馈信息
            Map<String, String> map = AliPayApi.toMap(request);
            log.info("支付回调响应：{}", JSONUtil.toJsonStr(map));
            boolean verifyResult = AlipaySignature.rsaCertCheckV1(map, alipayPaymentSetting.getAlipayPublicCertPath(), "UTF-8",
                    "RSA2");

            String payParamStr = map.get("passback_params");
            String payParamJson = URLDecoder.decode(payParamStr, StandardCharsets.UTF_8);
            PayParam payParam = JSONUtil.toBean(payParamJson, PayParam.class);


            if (verifyResult) {
                String tradeNo = map.get("trade_no");
                Double totalAmount = Double.parseDouble(map.get("total_amount"));
                PaymentSuccessParams paymentSuccessParams =
                        new PaymentSuccessParams(PaymentMethodEnum.ALIPAY.name(), tradeNo, totalAmount, payParam);

                paymentService.success(paymentSuccessParams);
                log.info("支付回调通知：支付成功-参数：{},回调参数:{}", map, payParam);
            } else {
                log.info("支付回调通知：支付失败-参数：{}", map);
            }
        } catch (AlipayApiException e) {
            log.error("支付回调通知异常", e);
        }

    }

    /**
     * 获取微信支付配置
     *
     * @return
     */
    private AlipayPaymentSetting alipayPaymentSetting() {
        Setting setting = settingService.get(SettingEnum.ALIPAY_PAYMENT.name());
        if (setting != null) {
            return JSONUtil.toBean(setting.getSettingValue(), AlipayPaymentSetting.class);
        }
        throw new ServiceException(ResultCode.ALIPAY_NOT_SETTING);
    }


}