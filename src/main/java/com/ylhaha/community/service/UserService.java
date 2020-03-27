package com.ylhaha.community.service;

import com.ylhaha.community.mapper.UserMapper;
import com.ylhaha.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yl
 */
@Service
public class UserService {
    @Autowired
    UserMapper userMapper;

    public void insertUser(User user){
        userMapper.insertUser(user);
    }

    public User getUser(String token){
        return userMapper.getUser(token);
    }
}
