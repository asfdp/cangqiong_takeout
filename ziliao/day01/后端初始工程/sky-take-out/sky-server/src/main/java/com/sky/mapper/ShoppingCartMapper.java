package com.sky.mapper;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {


    /**
     * 查询购物车已有信息
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 根据条件查询购物车数量
     * @param shoppingCart
     * @return
     */
    Integer getNumber(ShoppingCart shoppingCart);

    /**
     * 添加购物车
     * @param shoppingCart
     */
    void add(ShoppingCart shoppingCart);

    /**
     * 修改购物车商品数量
     * @param shoppingCart
     */

    @Update("update shopping_cart set number=#{number} where id=#{id}")
    void updateNumberById(ShoppingCart shoppingCart);


    /**
     * 根据条件删除购物车记录,数量-1
     * @param shoppingCart
     */
    void updateSub(ShoppingCart shoppingCart);

    /**
     * 根据条件删除购物车记录,删除该条记录
     * @param shoppingCart
     */
    void deleteSub(ShoppingCart shoppingCart);

    /**
     * 根据用户id删除购物车内容
     * @param userId
     */
    @Delete("delete  from shopping_cart where user_id=#{userId}")
    void deleteAll(Long userId);

    /**
     * 批量插入购物车数据
     * @param shoppingCartList
     */
    void insertBatch(List<ShoppingCart> shoppingCartList);
}
