package cn.lili.modules.order.order.serviceimpl;

import cn.lili.common.exception.ServiceException;
import cn.lili.modules.order.order.entity.dos.OrderItem;
import cn.lili.modules.order.order.entity.enums.CommentStatusEnum;
import cn.lili.modules.order.order.entity.enums.OrderComplaintStatusEnum;
import cn.lili.modules.order.order.entity.enums.OrderItemAfterSaleStatusEnum;
import cn.lili.modules.order.order.mapper.OrderItemMapper;
import cn.lili.modules.order.order.service.OrderItemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * 子订单业务层实现
 *
 * @author Chopper
 * @date 2020/11/17 7:38 下午
 */
@Service
@Transactional
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public void updateCommentStatus(String orderItemSn, CommentStatusEnum commentStatusEnum) {
        LambdaUpdateWrapper<OrderItem> lambdaUpdateWrapper = Wrappers.lambdaUpdate();
        lambdaUpdateWrapper.set(OrderItem::getCommentStatus, commentStatusEnum.name());
        lambdaUpdateWrapper.eq(OrderItem::getSn, orderItemSn);
        this.update(lambdaUpdateWrapper);
    }

    @Override
    public void updateAfterSaleStatus(String orderItemSn, OrderItemAfterSaleStatusEnum orderItemAfterSaleStatusEnum) {
        LambdaUpdateWrapper<OrderItem> lambdaUpdateWrapper = Wrappers.lambdaUpdate();
        lambdaUpdateWrapper.set(OrderItem::getAfterSaleStatus, orderItemAfterSaleStatusEnum.name());
        lambdaUpdateWrapper.eq(OrderItem::getSn, orderItemSn);
        this.update(lambdaUpdateWrapper);
    }

    /**
     * 更新订单可投诉状态
     *
     * @param orderSn            订单sn
     * @param skuId              商品skuId
     * @param complainId         订单交易投诉ID
     * @param complainStatusEnum 修改状态
     */
    @Override
    public void updateOrderItemsComplainStatus(String orderSn, String skuId, String complainId, OrderComplaintStatusEnum complainStatusEnum) {
        LambdaQueryWrapper<OrderItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderItem::getOrderSn, orderSn).eq(OrderItem::getSkuId, skuId);
        OrderItem orderItem = getOne(queryWrapper);
        if (orderItem == null) {
            throw new ServiceException("当前订单项不存在！");
        }
        orderItem.setComplainId(complainId);
        orderItem.setComplainStatus(complainStatusEnum.name());
        updateById(orderItem);
    }

    @Override
    public OrderItem getBySn(String sn) {
        LambdaQueryWrapper<OrderItem> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(OrderItem::getSn, sn);
        return this.getOne(lambdaQueryWrapper);
    }

    @Override
    public List<OrderItem> getByOrderSn(String orderSn) {
        LambdaQueryWrapper<OrderItem> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(OrderItem::getOrderSn, orderSn);
        return this.list(lambdaQueryWrapper);
    }

    @Override
    public List<OrderItem> waitEvaluate(Date date) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.ge("o.complete_time", date);
        queryWrapper.eq("oi.comment_status", CommentStatusEnum.UNFINISHED.name());
        return orderItemMapper.waitEvaluate(queryWrapper);
    }
}