package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    @Transactional
    public void addCart(ShoppingCartDTO shoppingCartDTO) {
        //判断当前加入到购物车中的商品是否已存在于数据库中
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        //获取当前用户id
        Long userId= BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        //查询数据库
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        //如果已经存在，只需将数量+1,再存入数据库
        if (list != null && list.size() > 0) {//list不为空
            log.info("购物车内容已存在：{}", shoppingCart);
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartMapper.updateNumberById(cart);
        } else {//如果不存在，则插入一条数据到数据库
            //要判断是添加菜品还是套餐
            Long dishId = shoppingCart.getDishId();
            Long setmealId = shoppingCart.getSetmealId();
            if (dishId != null) {//非空证明是菜品
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            } else {//若不是菜品，则一定是套餐，不需要再做判断
                Setmeal setmeal = setmealMapper.getById(setmealId);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCart.setNumber(1);//新增默认为1
            shoppingCart.setCreateTime(LocalDateTime.now());

            log.info("购物车内容不存在，插入数据库：{}",shoppingCart);
            shoppingCartMapper.add(shoppingCart);
        }
    }

    @Override
    public List<ShoppingCart> listByUserId() {
        Long userId= BaseContext.getCurrentId();
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.list(ShoppingCart.builder().userId(userId).build());
        return shoppingCarts;
    }

    @Override
    public void deleteSub(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());

        //获取购物车中该数据的数量
        Integer number = shoppingCartMapper.getNumber(shoppingCart);
        shoppingCart.setNumber(number);
        //判断减少数量还是删除数据
        if (number > 1) {//数量不为1，则使数量-1
            log.info("商品数量为：{},数量-1",number);
            shoppingCart.setNumber(--number);
            shoppingCartMapper.updateSub(shoppingCart);
        } else {//否则数量为1，删除该条数据
            log.info("商品数量为1，删除该行数据");
            shoppingCartMapper.deleteSub(shoppingCart);
        }
    }

    @Override
    public void deleteAll() {
        Long userId=BaseContext.getCurrentId();
        log.info("清空购物车，用户id：{}",userId);
        shoppingCartMapper.deleteAll(userId);
    }


}
