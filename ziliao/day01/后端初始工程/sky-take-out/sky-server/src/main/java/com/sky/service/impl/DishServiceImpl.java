package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {


    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
//    @Autowired
//    private CategoryService categoryService;

    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Override
    public void saveWithFlavor(DishDTO dishDTO) {
        //向菜品表插入1条菜品数据
        Dish dish=new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.insert(dish);//插入完成后获取自增主键值
        Long dishId=dish.getId();

        //向口味表插入n条数据 参数：flavors集合
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size()>0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            //先判断是否为空，不为空再插入数据
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        //PageHelper创建分页数据
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        //将查询结果传递到分页对象，通过记录数获取总数
        Page<DishVO> page=dishMapper.pageQuery(dishPageQueryDTO);
        log.info("菜品分页查询结果：{}", page);
        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());
        return pageResult;
    }


    @Override
    @Transactional//事务注解，保证数据操作的一致性
    public void delete(List<Long> ids) {
        for (Long id : ids) {
            //先判断菜品是否能被删除----是否在售？
            Dish dish=dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) { //如果为1表示在售
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //先判断菜品是否能被删除----是否关联套餐？
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && setmealIds.size() > 0) {//不为空且大于0表示有套餐关联
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //删除菜品数据
//        for (Long id : ids) {
//            dishMapper.delete(id);
//            log.info("删除菜品，id：{}",id);
//            //删除菜品关联的口味数据
//            dishFlavorMapper.delete(id);
//            //每次循环调用一次sql，性能太低
              //希望能够一次性完成多个数据操作，避免循环
//        }


        //删除菜品数据优化版本
        dishMapper.delete(ids);//批量删除菜品数据

        dishFlavorMapper.delete(ids);//批量删除菜品口味数据
    }


    @Override
    public Dish getById(Long id) {
        return dishMapper.getById(id);
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {
        //修改菜品数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        List<DishFlavor> flavors = dishDTO.getFlavors();
        dishMapper.update(dish);
        log.info("修改菜品数据：{}",dish);

        //修改口味数据
        //1.删除原有口味数据
//        List<Long> dishId = new ArrayList<>();
//        Long dishId1 = flavors.get(1).getDishId();
//        log.info("dishId：{}",dishId1);
//        flavors.forEach(dishFlavor -> {
//            dishId.add(dishId1);
//            dishFlavor.setDishId();
//        });//获取原有口味数据id
//        dishFlavorMapper.delete(dishId);//根据id批量删除
        dishFlavorMapper.deleteByDishId(dish.getId());//删除原有数据
        log.info("删除原有数据：{}",flavors);

        //2.插入新口味数据
        if (flavors != null && flavors.size()>0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dish.getId());
            });
            //先判断是否为空，不为空再插入数据
            log.info("插入新口味数据：{}",flavors);
            dishFlavorMapper.insertBatch(flavors);
        }
    }


    public void changeStatus(Long id,Integer status) {
        Dish dish = new Dish();
        dish.setId(id);
        log.info("操作菜品id：{}",dish.getId());
        dish.setStatus(status);
        dishMapper.update(dish);
    }

    @Override
    public List<DishVO> getByCategoryId(Integer categoryId) {
        log.info("菜品id：{}",categoryId);
        return dishMapper.getByCategoryId(categoryId);
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getById(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}
