package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminCategoryController")
@RequestMapping("/admin/category")
@Slf4j
@Api(tags = "分类相关接口")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    @ApiOperation("新增")
    public Result save(@RequestBody CategoryDTO categoryDTO) {
        log.info("新增分类:{}",categoryDTO);
        categoryService.save(categoryDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("分类分页")
    public Result<PageResult> categoryPage(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("分页:{}",categoryPageQueryDTO);
        PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    @PutMapping
    @ApiOperation("编辑分类")
    public Result updateInfo(@RequestBody CategoryDTO categoryDTO){
        log.info("编辑分类:{}", categoryDTO);
        categoryService.update(categoryDTO);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("根据类型查询分类")
        public Result<List<Category>> list(Integer type){
        log.info("根据类型查询分类:{}", type);
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }

    @PostMapping("/status/{status}")
    @ApiOperation("修改员工状态")
    public Result startOrStop(@PathVariable Integer status,long id) {
        log.info("启用禁用员工账号:{},{}",status,id);
        categoryService.startOrStop(status,id);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation("删除")
    public Result deleteCatrgoryById(long id) {
        log.info("根据id删除分类:{}",id);
        categoryService.deleteById(id);
        return Result.success();
    }

}
