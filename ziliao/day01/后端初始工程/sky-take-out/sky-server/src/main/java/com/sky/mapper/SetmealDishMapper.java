package com.sky.mapper;

import com.sky.annotations.AutoFill;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id查询套餐id
     * @param dishIds
     * @return
     */
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);


    /**
     * 新增套餐菜品关系
     * @param setmealDish
     */
    void insert(List<SetmealDish> setmealDish);

    /**
     * 修改套餐菜品关系
     * @param setmealDishes
     */
    //void update(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id查询套餐菜品关系
     * @param setmealId
     * @return
     */

    List<SetmealDish> getSetmealDishBySetmealId(Long setmealId);


    /**
     * 根据套餐id批量删除菜品套餐关系
     * @param ids
     */
    void deleteBySetmealIds(List<Long> ids);
}
