package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;



import java.util.ArrayList;
import java.util.List;

@RestController
@Api(tags = "套餐相关接口")
@RequestMapping("/admin/setmeal")
@Slf4j
public class SetMealController {

    @Autowired
    private SetmealService setmealServic;

    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @PostMapping
    @Transactional
    @CacheEvict(cacheNames = "setmealCache",key = "#setmealDTO.categoryId")
    public Result insert(@RequestBody SetmealDTO setmealDTO) {
        log.info("新增套餐：{}",setmealDTO);
        //将套餐信息写入setmeal表
        setmealDTO.setStatus(0);//默认为禁售状态
        Long setmealId=setmealServic.insert(setmealDTO);
        log.info("setmealId:{}",setmealId);

        //将套餐菜品关联信息写入setmeal_dish表
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });
        setmealDishMapper.insert(setmealDishes);
        return Result.success();
    }


    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> getById(@PathVariable("id") Long dishId) {
        Setmeal setmeal = setmealServic.getById(dishId);
        SetmealVO setmealVO = new SetmealVO();
        //传入套餐数据
        BeanUtils.copyProperties(setmeal,setmealVO);
        //传入套餐菜品数据
        List<SetmealDish> setmealDishes =
                setmealDishMapper.getSetmealDishBySetmealId(dishId);
        setmealVO.setSetmealDishes(setmealDishes);
        return Result.success(setmealVO);
    }


    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result<PageResult> pageQuery (SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("套餐分页查询，请求参数：{}",setmealPageQueryDTO);
        PageResult pageResult = setmealServic.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }


    @PutMapping
    @ApiOperation("修改套餐")
    @Transactional
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result update(@RequestBody SetmealDTO setmealDTO) {
        log.info("套餐修改内容：{}", setmealDTO);
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //修改套餐表
        setmealServic.update(setmeal);
        log.info("修改套餐表");
        //修改套餐菜品关系表
        //1.删除原有菜品关系
        List<Long> ids = new ArrayList<>();
        ids.add(setmeal.getId());
        setmealDishMapper.deleteBySetmealIds(ids);
        //2.插入新菜品关系
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmeal.getId());
        });
        setmealDishMapper.insert(setmealDishes);
        return Result.success();
    }


    @DeleteMapping
    @ApiOperation("批量删除套餐")
    @Transactional
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result deleteBatch(@RequestParam List<Long> ids) {
        //删除套餐数据
        setmealServic.deleteBatch(ids);
        log.info("删除套餐数据ids：{}",ids);
        //删除口味关系数据
        setmealDishMapper.deleteBySetmealIds(ids);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("套餐起售停售切换")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result changeStatus(@PathVariable Integer status, Long id) {
        setmealServic.changeStatus(status,id);

        return Result.success();
    }
}
