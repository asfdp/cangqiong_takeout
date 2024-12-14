package com.sky.controller.user;


import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "C端购物车相关接口")
@Slf4j
@RestController
@RequestMapping("/user/shoppingCart")
public class shoppingCartController {
//    @Autowired
//    private RedisTemplate redisTemplate;
    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    @ApiOperation("添加购物车")
    public Result addCart(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        shoppingCartService.addCart(shoppingCartDTO);
        //redisTemplate.opsForValue().set("shoppingCart",shoppingCartDTO);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("查看购物车内容")
    public Result<List<ShoppingCart>> listByUserId() {
        List<ShoppingCart> shoppingCarts = shoppingCartService.listByUserId();
        return Result.success(shoppingCarts);
    }

    @PostMapping("/sub")
    @ApiOperation("根据条件删除一条购物车记录")
    public void deleteSub(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        shoppingCartService.deleteSub(shoppingCartDTO);
    }

    @DeleteMapping("/clean")
    public void deleteAll() {
        shoppingCartService.deleteAll();
    }
}
