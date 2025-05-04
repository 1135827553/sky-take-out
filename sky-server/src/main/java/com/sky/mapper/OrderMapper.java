package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Mapper
public interface OrderMapper {
    void insert(Orders orders);


    @Select("select * from orders where number=#{outTradeNo}")
    Orders getByNumber(String outTradeNo);

    void update(Orders orders);

    @Update("update orders set status = #{orderStatus}, pay_status = #{orderPaidStatus}, check_out_time = #{check_out_time} where id = #{arg3}")
    void updateStatus(Integer orderStatus, Integer orderPaidStatus, LocalDateTime check_out_time, Long id);

    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

}
