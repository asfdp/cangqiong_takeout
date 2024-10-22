package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


public interface SetmealService {


    /**
     * 新增套餐
     * @param setmealDTO
     */
    public Long insert(SetmealDTO setmealDTO);

    /**
     * 根据套餐id查询套餐
     * @param id
     * @return
     */
    Setmeal getById(Long id);


    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);


    /**
     * 套餐修改
     * @param setmeal
     */
    void update(Setmeal setmeal);


    /**
     * 批量删除套餐
     */
    void deleteBatch(List<Long> ids);


    /**
     * 修改套餐状态
     * @param status
     * @param id
     */
    void changeStatus(Integer status,Long id);

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemById(Long id);

}
