package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
//@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/admin/employee/login")
    @ApiOperation(value="员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();


        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/admin/employeev/logout")
    @ApiOperation(value="员工退出")
    public Result<String> logout() {
        return Result.success();
    }


    /**
     * 员工注册
     * @param employeeDTO
     * @return
     */
    @PostMapping("/admin/employee")
    @ApiOperation(value="员工注册")
    public Result<String> register(@RequestBody EmployeeDTO employeeDTO) {
        log.info("员工注册：{}", employeeDTO);
        employeeService.register(employeeDTO);
        return Result.success();
    }

    @GetMapping("/admin/employee/page")
    @ApiOperation(value = "员工分页查询")
    public Result<PageResult> pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        //springMVC会自动将请求参数名与属性名一致的值传递进来
        log.info("员工分页查询，请求参数：{}",employeePageQueryDTO);
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);

        return Result.success(pageResult);
    }

    /**
     * 启用或禁用员工账号
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/admin/employee/status/{status}")
    @ApiOperation(value = "员工状态管理")
    public Result changeStatus(@PathVariable Integer status,Long id) {//此处status为路径参数，id为请求参数
        log.info("启用或禁用员工账号：{}，{}",status,id);
        employeeService.changeStatus(status, id);
        return Result.success();
    }

    /**
     * 个根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/admin/employee/{id}")
    @ApiOperation(value = "根据id查询员工信息")
    public Result<Employee> getById(@PathVariable Long id) {
        log.info("查询员工，id：{}",id);
        Employee employee=employeeService.getById(id);
        return Result.success(employee);
    }


    @PutMapping("/admin/employee")
    @ApiOperation("编辑员工信息")
    public Result update(@RequestBody EmployeeDTO employeeDTO) {
        log.info("编辑员工信息：{}",employeeDTO);
        //Employee employee = employeeService.getById(employeeDTO.getId());
        Employee employee=new Employee();
        BeanUtils.copyProperties(employeeDTO,employee);
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(BaseContext.getCurrentId());
        employeeService.update(employee);

        return Result.success();
    }
}
