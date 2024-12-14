package com.sky.task;


import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理超时订单的方法
     * 每分钟执行一次
     */
    @Scheduled(cron = "0 * * * * ? ")
    public void processTimeoutOrder() {
        log.info("定时处理超时订单：{}", LocalDateTime.now());
        //查询是否存在超时订单
        LocalDateTime orderTime = LocalDateTime.now().minusMinutes(15);
        List<Orders> timeoutOrders = orderMapper.getByStatusAndOrderTime(Orders.PENDING_PAYMENT, orderTime);
        //更新返回的结果，使其状态改为已取消
        if (timeoutOrders != null && timeoutOrders.size() > 0) {
            for (Orders order : timeoutOrders) {
                order.setStatus(Orders.CANCELLED);
                //设置取消原因
                order.setCancelReason("订单超时，自动取消");
                //设置取消时间
                order.setCancelTime(LocalDateTime.now());
                //更新数据库
                orderMapper.update(order);

            }
        }
    }


    /**
     * 处理出于派送中的订单
     * 每天凌晨1点执行一次,修改订单状态
     */
    @Scheduled(cron = "0 0 1 * * ? ")
    public void processDeliveryOrder() {
        log.info("处理配送中订单：{}",LocalDateTime.now());
        //查询出于配送中的订单
        LocalDateTime time = LocalDateTime.now().minusHours(1);
        List<Orders> deliveryOrders = orderMapper.getByStatusAndOrderTime(Orders.DELIVERY_IN_PROGRESS, time);
        //更新返回结果
        if (deliveryOrders != null && deliveryOrders.size() > 0) {
            for (Orders order : deliveryOrders) {
                order.setStatus(Orders.COMPLETED);
                orderMapper.update(order);
            }
        }

    }

}
