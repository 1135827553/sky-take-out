package com.sky.mapper;

import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {


    //批量插入口味数据
    void insertBatch(List<DishFlavor> flavor);

    void deleteByIds(List<Long> ids);

    List<DishFlavor> getByDishId(Long id);

    @Delete("delete from dish_flavor where id=#{id}")
    void deleteByDishIds(Long id);
}
