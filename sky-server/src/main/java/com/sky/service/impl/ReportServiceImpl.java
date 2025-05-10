package com.sky.service.impl;
import com.sky.mapper.OrderDetailMapper;
import com.sky.service.ReportService;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        ArrayList<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //存放每天的营业额
        //select sum(amount) from orders where >order_time and <order_time and status=Orders.CANCELLED
        ArrayList<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover=orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }

        //封装返回结果
        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        ArrayList<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //存放每天的新增用户数量
        ArrayList<Integer> newUserList = new ArrayList<>();
        //存放每天的总用户数量
        ArrayList<Integer> totalUserList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("end", endTime);

            Integer total=userMapper.countByMap(map);
            map.put("begin", beginTime);
            Integer newUsers=userMapper.countByMap(map);

            newUserList.add(newUsers);
            totalUserList.add(total);
        }

        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ",")).build();
    }

    @Override
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        ArrayList<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //存放每天的新增订单总数
        ArrayList<Integer> orderCountList = new ArrayList<>();
        //存放每天的有效订单数
        ArrayList<Integer> validOrderCountList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);

            orderCountList.add(orderMapper.countByMap(map));
            map.put("status", Orders.COMPLETED);
            validOrderCountList.add(orderMapper.countByMap(map));

        }
        //计算时间区间内的订单总数量
        Integer totalOrderCount = orderCountList.stream().mapToInt(Integer::intValue).sum();

        //有效订单数
        Integer validOrderCount = validOrderCountList.stream().mapToInt(Integer::intValue).sum();
        //订单完成率
        Double orderCompletionRate = (validOrderCount.doubleValue() / totalOrderCount);
        //计算时间区间内的有效订单数量
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .orderCountList(StringUtils.join(orderCountList,","))
                .validOrderCount(validOrderCount)
                .validOrderCountList(StringUtils.join(validOrderCountList,","))
                .orderCompletionRate(orderCompletionRate)
                .totalOrderCount(totalOrderCount).build();
    }

    @Override
    public SalesTop10ReportVO getTop(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> goodsSalesDTOList=orderMapper.getSalesTop(beginTime,endTime);

        //商品名称列表
        ArrayList<String> goodsNameList = new ArrayList<>();
        //销量列表
        ArrayList<Integer> salesList = new ArrayList<>();//nteger validOrderCount = validOrderCountList.stream().mapToInt(Integer::intValue).sum();
        //goodsSalesDTOList.stream().map(GoodsSalesDTO::getName).collect(GoodsSalesDTO.)
        goodsSalesDTOList.forEach(goodsSalesDTO -> {
            goodsNameList.add(goodsSalesDTO.getName());
            salesList.add(goodsSalesDTO.getNumber());
        });
        return SalesTop10ReportVO
                .builder()
                .nameList(StringUtils.join(goodsNameList,","))
                .numberList(StringUtils.join(salesList,","))
                .build();
    }
}
