package com.ylhaha.community.controller;

import com.ylhaha.community.dto.PageInfoDTO;
import com.ylhaha.community.model.User;
import com.ylhaha.community.service.QuestionService;
import com.ylhaha.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author yl
 */
@Controller
public class ProfileController {
    @Autowired
    UserService userService;
    @Autowired
    QuestionService questionService;

    @GetMapping("/profile/{action}")
    public String toProfile(@PathVariable("action")String action,
                            Model model,
                            HttpServletRequest request,
                            @RequestParam(value = "currentPage",defaultValue = "1")Integer currentPage,
                            @RequestParam(value = "pageSize",defaultValue = "5")Integer pageSize){


        User user = (User) request.getSession().getAttribute("user");
        if(user==null){
            return "redirect:/";
        }

        if("questions".equals(action)){
            model.addAttribute("section", "questions");
            model.addAttribute("sectionName", "我的提问");
        }else if("replies".equals(action)) {
            model.addAttribute("section", "replies");
            model.addAttribute("sectionName", "最新回复");
        }

        PageInfoDTO pageInfoDTO = questionService.list(currentPage, pageSize, user.getId());
        model.addAttribute("pageInfoDTO",pageInfoDTO);

        return "profile";
    }
}
