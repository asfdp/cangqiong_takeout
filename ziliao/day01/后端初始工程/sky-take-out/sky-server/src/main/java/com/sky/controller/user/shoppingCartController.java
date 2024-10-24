package com.sky.controller.user;


import com.sky.dto.ShoppingCartDTO;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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



}
