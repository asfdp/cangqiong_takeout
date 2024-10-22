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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "C端-菜品浏览接口")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> list(Long categoryId) {
        //构造Redis中的key，规则：dish_分类id
        String key="dish_"+categoryId;
        //查询Redis中是否存在数据
        List<DishVO> list = (List<DishVO>) redisTemplate.opsForValue().get(key);
        // 如果存在，直接返回
        if (list != null && list.size()>0) {//集合非空且有元素
            return Result.success(list);
        }
        //如果不存在，则进入数据库查找返回结果，并将数据存入Redis缓存
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品
        //获得数据库中的信息
        list = dishService.listWithFlavor(dish);
        //将结果存入Redis缓存
        redisTemplate.opsForValue().set(key,list);
        return Result.success(list);
    }

}
