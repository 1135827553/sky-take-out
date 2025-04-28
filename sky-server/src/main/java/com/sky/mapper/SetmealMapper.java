package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    List<Long> getSetmealIdsByDishIds(List<Long> ids);

    List<Setmeal> list(Setmeal setmeal);

    //根据套餐id查询菜品选项
    @Select("select sd.copies,sd.description,d.image,d.name from setmeal_dish sd left join dish d on sd.dish_id=d.id where sd.setmeal_id=#{setmealId}")
    List<DishItemVO> getDishItemBySetmealId(Long id);

    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    @AutoFill(value = OperationType.INSERT)
    void insert(Setmeal setmeal);

    @Select("select * from setmeal where id=#{id}")
    SetmealVO getByIdWithDish(Long id);

    @AutoFill(value = OperationType.UPDATE)
    void update(Setmeal emp);

    @Select("select * from setmeal where id=#{id}")
    Setmeal getById(Long id);


    @Delete("delete from setmeal where id=#{id}")
    void deleteById(Long id);

    List<SetmealDish> getBySetmealId(Long id);
}
