package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Api
@RestController("userDishController")
@RequestMapping("/user/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    @Cacheable(cacheNames = "dishCache",key = "#categoryId")
    public Result<List<DishVO>> list(Long categoryId) {
        log.info("根据分类id查询菜品:{}",categoryId);

        //构建redis的key
        String key="dish_"+categoryId;
        //查询redis中是否有值
        List<DishVO> redisDishList = (List<DishVO>) redisTemplate.opsForValue().get(key);
        if (redisDishList != null && redisDishList.size() > 0) {
            return Result.success(redisDishList);
        }

        Dish dish = new Dish();
        dish.setId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);
        List<DishVO> list=dishService.listWithFlavor(categoryId);

        //不存在就加入
        redisTemplate.opsForValue().set(key,list);

        return Result.success(list);
    }
}
