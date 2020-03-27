package com.ylhaha.community.mapper;

import com.ylhaha.community.dto.GithubUser;
import com.ylhaha.community.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

/**
 * @author yl
 */
public interface UserMapper {
    @Insert("Insert into user(name,account_id,token,gmt_create,gmt_modified,bio) values" +
            "(#{name},#{accountId},#{token},#{gmtCreate},#{gmtModified},#{bio})")
    void insertUser(User user);

    @Select("select * from user where token = #{token}")
    User getUser(String token);
}
