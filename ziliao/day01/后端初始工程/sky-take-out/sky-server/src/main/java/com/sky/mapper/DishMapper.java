package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotations.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);


    /**
     * 新增菜品
     * @param dish
     */
    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);


    /**
     * 查询菜品和分类信息
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);


    /**
     * 批量删除菜品
     * @param ids
     */
    void delete(List<Long> ids);


    /**
     * 根据id查询菜品信息
     * @param id
     * @return
     */
    Dish getById(Long id);


    /**
     * 修改菜品数据
     * @param dish
     */
    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);


    /**
     * 根据分类id查询菜品数据
     * @param categoryId
     * @return
     */
    List<DishVO> getByCategoryId(Integer categoryId);


    /**
     * 条件查询菜品信息
     * @param dish
     * @return
     */
    List<Dish> list(Dish dish);
}
