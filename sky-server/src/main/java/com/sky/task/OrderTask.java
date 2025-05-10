package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;

    //处理超时订单
    //@Scheduled(cron = "0 * * * * *")
    public void processTimeoutOrder(){
        log.info("处理超时订单");
        //查询15分钟之前未支付订单
        List<Orders> list = orderMapper.getByStatusAndTime(Orders.PENDING_PAYMENT, LocalDateTime.now().plusMinutes(15));
        //更新订单状态，完成订单
        list.forEach(order->{
            order.setStatus(Orders.CANCELLED);
            order.setCancelReason("支付超时,订单取消");
            order.setCancelTime(LocalDateTime.now());
            orderMapper.update(order);
        });
    }

    //处理当天一直在派送的订单
    //@Scheduled(cron = "* * 1 * * *")
    public void processDeliverOrder(){
        log.info("处理处理当天一直在派送的订单");
        //查询12点钟之前未完成的订单
        List<Orders> list = orderMapper.getByStatusAndTime(Orders.PENDING_PAYMENT, LocalDateTime.now().plusMinutes(-60));
        //更新订单状态，完成订单
        list.forEach(order->{
            order.setStatus(Orders.COMPLETED);
            orderMapper.update(order);
        });
    }
}
