package com.sky.service.impl;

import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public Long insert(SetmealDTO setmealDTO) {
        //将套餐信息写入setmeal表
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmeal.setStatus(0);
        log.info("status：{}",setmeal.getStatus());
        setmealMapper.insert(setmeal);
        return setmeal.getId();
    }

    @Override
    public Setmeal getById(Long id) {
        return setmealMapper.getById(id);
    }

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        PageResult pageResult = new PageResult();
        List<Setmeal> list = setmealMapper.pageQuery(setmealPageQueryDTO);
        pageResult.setRecords(list);
        pageResult.setTotal(list.size());
        return pageResult;
    }

    @Override
    public void update(Setmeal setmeal) {
        setmealMapper.update(setmeal);


    }

    @Override
    public void deleteBatch(List<Long> ids) {
        setmealMapper.deleteBatch(ids);
    }


    @Override
    public void changeStatus(Integer status,Long id) {
        Setmeal setmeal = new Setmeal();
        setmeal.setId(id);
        setmeal.setStatus(status);
        log.info("操作套餐起、停售，id：{}，状态：{}",id,status);
        setmealMapper.update(setmeal);
    }

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}
