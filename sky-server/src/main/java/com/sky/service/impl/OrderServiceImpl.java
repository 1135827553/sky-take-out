package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.gson.JsonObject;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;

    private Orders orders;

    @Override
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        //处理各种业务异常(地址簿为空,购物车数据为空)
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook==null ) {
            throw new RuntimeException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        ShoppingCart shoppingCart = new ShoppingCart();
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if (list == null || list.size() == 0) {
            throw new RuntimeException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //向订单表插入1条数据
        Orders order = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,order);
        order.setNumber(String.valueOf(System.currentTimeMillis()));//订单号
        order.setOrderTime(LocalDateTime.now());//下单时间
        order.setAddress(addressBook.getDetail());//地址
        order.setStatus(Orders.PENDING_PAYMENT);//订单状态
        order.setPayStatus(Orders.UN_PAID);//支付状态
        order.setConsignee(addressBook.getConsignee());//收货人
        order.setPhone(addressBook.getPhone());
        order.setUserId(BaseContext.getCurrentId());//用户id
        this.orders=order;
        orderMapper.insert(order);


        //向订单明细表插入n条数据
        ArrayList<OrderDetail> orderDetailsList = new ArrayList<>();
        for (ShoppingCart cart : list) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(order.getId());
            orderDetailsList.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetailsList);
        //清空当前用户的购物车数据
        shoppingCartMapper.deleteByUserId(userId);
        //封装Vo返回结果
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder().id(order.getId())
                .orderTime(order.getOrderTime())
                .orderNumber(order.getNumber())
                .orderAmount(order.getAmount())
                .build();

        return orderSubmitVO;
    }

    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);
/*        //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(ordersPaymentDTO.getOrderNumber(),//商户订单号
                new BigDecimal(0.01),//支付金额，单位 元
                "苍穹外卖",//商品描述
                user.getOpenid()//微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }*/
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", "ORDERPAID");
        OrderPaymentVO javaObject = jsonObject.toJavaObject(OrderPaymentVO.class);
        javaObject.setPackageStr(jsonObject.getString("package"));
       /* Integer OrderStatus = Orders.TO_BE_CONFIRMED;
        Integer OrderpaidStatus = Orders.PAID;
        orderMapper.updateStatus(OrderStatus,OrderpaidStatus,LocalDateTime.now(),this.orders.getId());*/
        Orders order = new Orders();
        order.setStatus(Orders.TO_BE_CONFIRMED);
        order.setPayStatus(Orders.PAID);
        order.setCheckoutTime(LocalDateTime.now());
        order.setId(this.orders.getId());
        orderMapper.update(orders);

        return javaObject;
    }

    //支付成功，修改订单状态
    public void paySuccess(String outTradeNo) {
        Orders order=orderMapper.getByNumber(outTradeNo);
        Orders orders = Orders.builder().id(order.getId()).status(Orders.TO_BE_CONFIRMED).payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now()).build();

        orderMapper.update(orders);
    }

    @Override
    public PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<Orders> query= orderMapper.pageQuery(ordersPageQueryDTO);
        ArrayList<Object> list = new ArrayList<>();
        if (query != null && query.getTotal() >0) {
            query.getResult().forEach(order -> {
                List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(order.getId());
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(order,orderVO);
                orderVO.setOrderDetailList(orderDetailList);
                list.add(orderVO);
            });
        }
        return new PageResult(query.getTotal(),list);
    }

    @Override
    public OrderVO getById(Long id) {
        Orders Orders = orderMapper.getById(id);
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(Orders,orderVO);
        orderVO.setOrderDetailList(orderDetailList);
        return orderVO;
    }



    @Override
    public void repetition(Long id) {
        Orders order = orderMapper.getById(id);
        OrdersSubmitDTO ordersSubmitDTO = new OrdersSubmitDTO();
        BeanUtils.copyProperties(order,ordersSubmitDTO);

        List<OrderDetail> detailList = orderDetailMapper.getByOrderId(id);
        for (OrderDetail detail : detailList) {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(detail,shoppingCart);
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    @Override
    public void reminder(Long id) {
        //回头来看  现在没思路
    }

    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<Orders> query= orderMapper.pageQuery(ordersPageQueryDTO);
        ArrayList<Object> list = new ArrayList<>();
        if (query != null && query.getTotal() >0) {
            query.getResult().forEach(order -> {

                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(order,orderVO);

                List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(order.getId());
                orderDetailList.stream().map(orderDetail -> {
                    String s = orderDetail.getName() + "*" + orderDetail.getNumber();
                    return s;
                }).collect(Collectors.toList());
                String.join(orderDetailList.toString());

                orderVO.setOrderDetailList(orderDetailList);
                list.add(orderVO);
            });
        }
        return new PageResult(query.getTotal(),list);
    }

    @Override
    public OrderStatisticsVO statistics() {
        Page<Orders> list = orderMapper.pageQuery(new OrdersPageQueryDTO());
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        list.forEach(orders -> {
            if (orders.getStatus() == Orders.TO_BE_CONFIRMED) {
                int i = (orderStatisticsVO.getToBeConfirmed() != null) ? orderStatisticsVO.getToBeConfirmed().intValue() + 1 : 1;
                orderStatisticsVO.setToBeConfirmed(i);
            } else if (orders.getStatus() == Orders.CONFIRMED) {
                int i = (orderStatisticsVO.getConfirmed() != null) ? orderStatisticsVO.getConfirmed().intValue() + 1 : 1;
                orderStatisticsVO.setConfirmed(i);
            } else if (orders.getStatus() == Orders.DELIVERY_IN_PROGRESS) {
                int i = (orderStatisticsVO.getDeliveryInProgress() != null) ? orderStatisticsVO.getDeliveryInProgress().intValue() + 1 : 1;
                orderStatisticsVO.setDeliveryInProgress(i);
            }
        });
        return orderStatisticsVO;
    }

    @Override
    public void cancel(OrdersCancelDTO ordersCancelDTO) throws Exception {
        //将取消原因放入
        //将状态改为已取消
        Orders orderdb = orderMapper.getById(ordersCancelDTO.getId());
        if (orderdb == null || orderdb.getStatus() != Orders.TO_BE_CONFIRMED) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        if (orderdb.getPayStatus() == Orders.PAID) {
            String refund = weChatPayUtil.refund(orderdb.getNumber(), orderdb.getNumber(), orderdb.getAmount(), orderdb.getAmount());
            log.info("申请退款：{}", refund);
        }

        Orders order = new Orders();
        BeanUtils.copyProperties(ordersCancelDTO,order);
        order.setStatus(Orders.CANCELLED);
        order.setPayStatus(Orders.UN_PAID);
        order.setCheckoutTime(LocalDateTime.now());
        orderMapper.update(order);


    }

    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception {
        //将拒绝原因放入
        //将状态改为已取消
        //支付状态改为退款
        Orders orderdb = orderMapper.getById(ordersRejectionDTO.getId());

        if (orderdb.getPayStatus() == Orders.PAID) {
            String refund = weChatPayUtil.refund(orderdb.getNumber(), orderdb.getNumber(), orderdb.getAmount(), orderdb.getAmount());
            log.info("申请退款：{}", refund);
        }


        Orders order = new Orders();
        BeanUtils.copyProperties(ordersRejectionDTO,order);
        order.setStatus(Orders.CANCELLED);
        order.setPayStatus(Orders.REFUND);
        order.setCheckoutTime(LocalDateTime.now());
        orderMapper.update(order);
    }

    @Override
    public void complete(Long id) {
        Orders orders = orderMapper.getById(id);
        if (orders == null || orders.getStatus() != Orders.DELIVERY_IN_PROGRESS) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        orders.setStatus(Orders.COMPLETED);
        orders.setDeliveryTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersConfirmDTO,orders);
        orders.setStatus(Orders.CONFIRMED);
        orderMapper.update(orders);
    }

    @Override
    public void delivery(Long id) {
        Orders order = orderMapper.getById(id);
        if (order == null || order.getStatus() != Orders.CONFIRMED) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        order.setStatus(Orders.DELIVERY_IN_PROGRESS);
        orderMapper.update(order);
    }
}
