package com.ylhaha.community.controller;

import com.ylhaha.community.model.User;
import com.ylhaha.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author yl
 */
@Controller
public class IndexController {

    @Autowired
    UserService userService;

    @GetMapping(value = {"/","/index"})
    public String hello(HttpServletRequest request){
        //获取cookie中的token
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie != null && cookie.getName().equals("token")) {
                String token = cookie.getValue();
                //根据token得到数据库中的user
                User user = userService.getUser(token);
                if (user != null) {
                    request.getSession().setAttribute("user",user);
                    break;
                }
            }
        }
        return "index";
    }
}
