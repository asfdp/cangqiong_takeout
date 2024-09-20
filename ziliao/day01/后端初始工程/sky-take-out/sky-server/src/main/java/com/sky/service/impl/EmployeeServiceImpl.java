package com.sky.service.impl;

import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

//    @Autowired
//    private  PageResult pageResult;


    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        //对前端传过来的密码进行md5加密，再和数据库中的密码进行比对
         password = DigestUtils.md5DigestAsHex(password.getBytes()); //md5加密

        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }


    /**
     * 员工注册
     * @param employeeDTO
     * @return
     */
    @Override
    public Result register(EmployeeDTO employeeDTO) {
        Employee employee=new Employee();
        //使用spring框架中的bean属性拷贝，将DTO中的属性放到新生成的实体类中,补充其他属性
        BeanUtils.copyProperties(employeeDTO,employee);
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
        employee.setStatus(StatusConstant.ENABLE);
        //将默认密码常量通过MD5加密，设置默认密码
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
//        Long empId = BaseContext.getCurrentId();//获取操作人员id
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);
        employeeMapper.register(employee);
        log.info("新增员工：{}", employee);
        return Result.success();
    }

    /**
     * 员工分页查询
     * @param employeePageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        PageResult pageResult = new PageResult();
        pageResult.setRecords(employeeMapper.pageQuery(employeePageQueryDTO));
        pageResult.setTotal(pageResult.getRecords().size());
        log.info("查询结果：{}",pageResult);
        return pageResult;
    }


    /**
     * 员工状态修改
     * @param status
     * @param id
     * @return
     */
    @Override
    public Result changeStatus(Integer status, Long id) {
        //status = (status == StatusConstant.ENABLE ? StatusConstant.DISABLE :StatusConstant.ENABLE );
        //用实体类来传递参数，复用数据更新方法
        Employee employee=Employee.builder()
                .status(status)
                .id(id)
                .build();
        employeeMapper.update(employee);
        return Result.success();
    }

    /**
     * 根据id查询员工数据
     * @param id
     * @return employee
     */
    @Override
    public Employee getById(Long id) {
        Employee employee=employeeMapper.getById(id);
        return employee;
    }

    @Override
    public Result update(Employee employee) {
        employeeMapper.update(employee);
        return Result.success();
    }


}
