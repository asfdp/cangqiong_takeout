package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品：{}",dishDTO);
        dishService.saveWithFlavor(dishDTO);
        //新增完成后，清理缓存数据
        String key="dish_"+dishDTO.getCategoryId();
        cleanCache(key);

        return Result.success();
    }


    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询：{}",dishPageQueryDTO);
        PageResult pageResult=dishService.pageQuery(dishPageQueryDTO);
        //log.info("菜品分页查询结果：{}",pageResult);
        return Result.success(pageResult);
    }


    @DeleteMapping
    @ApiOperation("菜品批量删除")
    public Result delete(@RequestParam List<Long> ids) {
        log.info("批量删除菜品：{}",ids);
        dishService.delete(ids);
        cleanCache("dish_*");
        return Result.success();
    }


    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品数据")
    public Result<DishVO> getById(@PathVariable() Long id) {
        log.info("根据id查询菜品数据，id：{}",id);
        Dish dishById = dishService.getById(id);//查询菜品数据
        //根据菜品id查询口味数据
        List<DishFlavor> list = dishFlavorMapper.getById(id);
        DishVO dishvo = new DishVO();//新建菜品VO对象
        BeanUtils.copyProperties(dishById,dishvo);//传入菜品数据
        dishvo.setFlavors(list);//传入菜品口味数据
        return Result.success(dishvo);
    }

    @PutMapping
    @ApiOperation("修改菜品")
    public Result updateDish(@RequestBody DishDTO dishDTO) {
        dishService.updateWithFlavor(dishDTO);
        cleanCache("*dish_*");
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售、停售操作")
    public Result changeStatus(@PathVariable("status") Integer status, Long id) {
        log.info("status:{},id:{}",status,id);
        dishService.changeStatus(id,status);
        cleanCache("dish_*");
        return Result.success();
    }


    @GetMapping("/list")
    @ApiOperation("根据分类查询套餐数据")
    public Result<List<DishVO>> listByCategory(Integer categoryId) {
        log.info("根据分类查询套餐，分类id：{}",categoryId);
        List<DishVO> list=dishService.getByCategoryId(categoryId);
        return Result.success(list);
    }

    /**
     * 清除Redis缓存数据
     * @param pattern
     */
    private void cleanCache(String pattern) {
        Set keys=redisTemplate.keys(pattern);
        redisTemplate.delete(keys);

    }
}
