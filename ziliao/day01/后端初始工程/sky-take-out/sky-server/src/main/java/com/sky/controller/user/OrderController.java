package com.sky.controller.user;



import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;

import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@RestController("userOrderController")
@RequestMapping("/user/order")
@Slf4j
@Api(tags = "订单相关接口")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    @ApiOperation("用户下单")
    public Result<OrderSubmitVO> oderSubmit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        OrderSubmitVO orderSubmitVO = orderService.orderSubmit(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }


    /**
     *历史订单分页查询
     * @param page
     * @param pageSize
     * @param status 订单状态
     * @return
     */
    @GetMapping("//historyOrders")
    @ApiOperation("历史订单分页查询")
    public Result<PageResult> orderPageQuery(int page,int pageSize,Integer status) {
        PageResult pageResult = orderService.pageQuery4user(page, pageSize, status);
        return Result.success(pageResult);
    }


    /**
     * 根据订单id取消订单
     * @param id
     * @return
     */
    @PutMapping("/cancel/{id}")
    @ApiOperation("取消订单")
    public Result orderCancel(@PathVariable Long id) {
        log.info("取消订单：{}",id);
        orderService.orderCancel(id);
        return Result.success();
    }


    /**
     * 根据订单id查看订单详情
     * @param id
     * @return
     */
    @GetMapping("/orderDetail/{id}")
    @ApiOperation("根据订单id查看订单明细")
    public Result<OrderVO> orderDetail(@PathVariable Long id) {
        log.info("查看订单详情：{}",id);
        OrderVO detail = orderService.getDetail(id);
        return Result.success(detail);
    }


    /**
     * 再来一单
     * @param id
     * @return
     */
    @PostMapping("/repetition/{id}")
    @ApiOperation("再来一单")
    public Result orderOnceMore(@PathVariable Long id) {
        orderService.repetition(id);
        return Result.success();
    }
}
