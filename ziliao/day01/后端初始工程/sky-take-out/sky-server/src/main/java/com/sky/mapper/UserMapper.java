package com.sky.mapper;

import com.sky.annotations.AutoFill;
import com.sky.entity.User;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /**
     * 根据openid查询用户
     *
     * @param openid
     * @return
     */
    @Select("select * from user where openid=#{openid}")
    User getById(String openid);

    /**
     * 表中插入新用户
     * @param user
     */
    void insert(User user);

    /**
     * 根据userId获取用户
     * @param userId
     * @return
     */
    @Select("select * from user where id=#{userId}")
    User getByUserId(Long userId);

}
