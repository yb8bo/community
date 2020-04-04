package com.ylhaha.community.controller;

import com.ylhaha.community.dto.PageInfoDTO;
import com.ylhaha.community.model.User;
import com.ylhaha.community.service.QuestionService;
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
    @Autowired
    QuestionService questionService;

    @GetMapping(value = {"/","/index"})
    public String hello(Model model,
                        @RequestParam(value = "currentPage",defaultValue = "1")Integer currentPage,
                        @RequestParam(value = "pageSize",defaultValue = "5")Integer pageSize){

        //获取Question中的数据展示到页面上
        PageInfoDTO pageInfoDTO = questionService.list(currentPage,pageSize, 0);
        model.addAttribute("pageInfoDTO",pageInfoDTO);
        return "index";
    }
}
