package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    /**
     * 新增菜品
     * @param dishDTO
     */
    public void saveWithFlavor(DishDTO dishDTO);

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);


    /**
     * 批量删除菜品
     * @param ids
     */
    public void delete(List<Long> ids);


    /**
     * 根据id查询菜品信息
     * @param id
     * @return
     */
    public Dish getById(Long id);


    /**
     * 修改菜品以及口味
     * @param dishDTO
     */
    public void updateWithFlavor(DishDTO dishDTO);

    /**
     * 菜品起售、停售操作
     * @param status
     */
    public void changeStatus(Long id,Integer status);


    /**
     * 根据分类查询菜品信息
     * @param categoryId
     * @return
     */
    public List<DishVO> getByCategoryId(Integer categoryId);

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);
}
