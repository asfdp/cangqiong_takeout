package com.sky.service;


import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.vo.UserLoginVO;

/**
 * 用户相关服务
 */
public interface UserService {
    public User wxLogin(UserLoginDTO userLoginDTO);

}
