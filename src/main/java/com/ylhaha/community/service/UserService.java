package com.ylhaha.community.service;

import com.ylhaha.community.mapper.UserMapper;
import com.ylhaha.community.model.User;
import com.ylhaha.community.model.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yl
 */
@Service
public class UserService {
    @Autowired
    UserMapper userMapper;


    public List<User> getUser(String token){
        UserExample userExample = new UserExample();
        userExample.createCriteria().andTokenEqualTo(token);
        return userMapper.selectByExample(userExample);
    }

    public void createOrUpdate(User user) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andAccountIdEqualTo(user.getAccountId());
        List<User> users = userMapper.selectByExample(userExample);
        if (users.size() != 0) {
            //已有用户，更新信息
            User dbUser = users.get(0);
            User updateUser = new User();
            updateUser.setGmtModified(System.currentTimeMillis());
            updateUser.setAvatarUrl(user.getAvatarUrl());
            updateUser.setName(user.getName());
            updateUser.setToken(user.getToken());
            UserExample example = new UserExample();
            example.createCriteria()
                    .andIdEqualTo(dbUser.getId());
            userMapper.updateByExampleSelective(updateUser, example);
        }else {
            //没有，在数据库中创建
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
        }
    }
}
