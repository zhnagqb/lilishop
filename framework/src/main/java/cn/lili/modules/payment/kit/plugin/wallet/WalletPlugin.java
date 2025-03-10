package cn.lili.modules.payment.kit.plugin.wallet;

import cn.lili.common.enums.ResultCode;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.enums.ResultUtil;
import cn.lili.common.vo.ResultMessage;
import cn.lili.modules.member.service.MemberWalletService;
import cn.lili.modules.order.trade.entity.enums.DepositServiceTypeEnum;
import cn.lili.modules.payment.entity.RefundLog;
import cn.lili.modules.payment.kit.CashierSupport;
import cn.lili.modules.payment.kit.Payment;
import cn.lili.modules.payment.kit.dto.PayParam;
import cn.lili.modules.payment.kit.dto.PaymentSuccessParams;
import cn.lili.modules.payment.kit.enums.PaymentMethodEnum;
import cn.lili.modules.payment.kit.params.dto.CashierParam;
import cn.lili.modules.payment.service.PaymentService;
import cn.lili.modules.payment.service.RefundLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * WalletPlugin
 *
 * @author Chopper
 * @version v1.0
 * 2021-02-20 10:14
 */
@Slf4j
@Component
public class WalletPlugin implements Payment {

    //支付日志
    @Autowired
    private PaymentService paymentService;
    //退款日志
    @Autowired
    private RefundLogService refundLogService;
    //会员余额
    @Autowired
    private MemberWalletService memberWalletService;
    //收银台
    @Autowired
    private CashierSupport cashierSupport;

    @Override
    public ResultMessage<Object> h5pay(HttpServletRequest request, HttpServletResponse response, PayParam payParam) {

        savePaymentLog(payParam);
        return ResultUtil.success(ResultCode.PAY_SUCCESS);
    }

    @Override
    public ResultMessage<Object> JSApiPay(HttpServletRequest request, PayParam payParam) {
        savePaymentLog(payParam);
        return ResultUtil.success(ResultCode.PAY_SUCCESS);
    }

    @Override
    public ResultMessage<Object> appPay(HttpServletRequest request, PayParam payParam) {
        savePaymentLog(payParam);
        return ResultUtil.success(ResultCode.PAY_SUCCESS);
    }

    @Override
    public ResultMessage<Object> nativePay(HttpServletRequest request, PayParam payParam) {
        savePaymentLog(payParam);
        return ResultUtil.success(ResultCode.PAY_SUCCESS);
    }

    @Override
    public ResultMessage<Object> mpPay(HttpServletRequest request, PayParam payParam) {

        savePaymentLog(payParam);
        return ResultUtil.success(ResultCode.PAY_SUCCESS);
    }

    @Override
    public void cancel(RefundLog refundLog) {

        try {
            memberWalletService.increase(refundLog.getTotalAmount(),
                    refundLog.getMemberId(),
                    "取消[" + refundLog.getOrderSn() + "]订单，退还金额[" + refundLog.getTotalAmount() + "]",
                    DepositServiceTypeEnum.WALLET_REFUND.name());
            refundLog.setIsRefund(true);
            refundLogService.save(refundLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存支付日志
     *
     * @param payParam 支付参数
     */
    private void savePaymentLog(PayParam payParam) {
        //获取支付收银参数
        CashierParam cashierParam = cashierSupport.cashierParam(payParam);
        this.callBack(payParam, cashierParam);
    }

    @Override
    public void refund(RefundLog refundLog) {
        try {
            memberWalletService.increase(refundLog.getTotalAmount(),
                    refundLog.getMemberId(),
                    "售后[" + refundLog.getAfterSaleNo() + "]审批，退还金额[" + refundLog.getTotalAmount() + "]", DepositServiceTypeEnum.WALLET_REFUND.name());
            refundLog.setIsRefund(true);
            refundLogService.save(refundLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 支付订单
     *
     * @param payParam     支付参数
     * @param cashierParam 收银台参数
     */
    public void callBack(PayParam payParam, CashierParam cashierParam) {

        //支付信息
        try {
            if (UserContext.getCurrentUser() == null) {
                throw new ServiceException(ResultCode.USER_NOT_LOGIN);
            }
            boolean result = memberWalletService.reduce(
                    cashierParam.getPrice(),
                    UserContext.getCurrentUser().getId(),
                    "订单[" + cashierParam.getOrderSns() + "]支付金额[" + cashierParam.getPrice() + "]",
                    DepositServiceTypeEnum.WALLET_PAY.name()
            );
            if (result) {
                try {
                    PaymentSuccessParams paymentSuccessParams = new PaymentSuccessParams(
                            PaymentMethodEnum.WALLET.name(),
                            "",
                            cashierParam.getPrice(),
                            payParam
                    );

                    paymentService.success(paymentSuccessParams);
                    log.info("支付回调通知：余额支付：{}", payParam);
                } catch (ServiceException e) {
                    //业务异常，则支付手动回滚
                    memberWalletService.increase(
                            cashierParam.getPrice(),
                            UserContext.getCurrentUser().getId(),
                            "订单[" + cashierParam.getOrderSns() + "]支付异常，余额返还[" + cashierParam.getPrice() + "]",
                            DepositServiceTypeEnum.WALLET_REFUND.name()
                    );
                    throw e;
                }
            } else {
                throw new ServiceException(ResultCode.WALLET_INSUFFICIENT);
            }
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.info("余额支付异常", e);
            throw new ServiceException(ResultCode.WALLET_INSUFFICIENT);
        }

    }

}
