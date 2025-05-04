package com.sky.service;

import com.sky.dto.*;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);

    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    void paySuccess(String outTradeNo);

    PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderVO getById(Long id);

    //void concel(Long id);

    void repetition(Long id);

    void reminder(Long id);

    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderStatisticsVO statistics();

    void cancel(OrdersCancelDTO ordersCancelDTO) throws Exception;

    void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception;

    void complete(Long id);

    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    void delivery(Long id);
}
