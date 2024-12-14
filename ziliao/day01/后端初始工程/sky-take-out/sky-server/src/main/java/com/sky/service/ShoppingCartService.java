package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {


    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    void addCart(ShoppingCartDTO shoppingCartDTO);


    /**
     * 根据用户id查询购物车
     * @param userId
     * @return
     */
    List<ShoppingCart> listByUserId();

    /**
     * 根据条件删除一条购物车记录
     * @param shoppingCartDTO
     */
    void deleteSub(ShoppingCartDTO shoppingCartDTO);

    /**
     * 清空购物车
     */
    void deleteAll();
}
