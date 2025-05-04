package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    List<ShoppingCart> list(ShoppingCart shoppingCart);

    @Update("UPDATE shopping_cart set number=#{number} where id=#{id}")
    void updateNumberById(ShoppingCart shoppingCart2);

    @Insert("INSERT INTO shopping_cart(name, image, user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time) VALUES " +
            "(#{name},#{image},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{number},#{amount},#{createTime})")
    void insert(ShoppingCart shoppingCart);

    @Delete("delete from shopping_cart where user_id=#{userId}")
    void cleanShoppingCartByUserId(Long userId);


    @Delete("delete from shopping_cart where id=#{id}")
    void deleteShoppingCartById(ShoppingCart shoppingCart);

    @Delete("delete from shopping_cart where user_id=#{userId}")
    void deleteByUserId(Long userId);
}
