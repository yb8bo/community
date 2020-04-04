package com.ylhaha.community.controller;

import com.ylhaha.community.cache.TagCache;
import com.ylhaha.community.dto.TagDTO;
import com.ylhaha.community.exception.CustomizeErrorCode;
import com.ylhaha.community.exception.CustomizeException;
import com.ylhaha.community.model.Question;
import com.ylhaha.community.model.User;
import com.ylhaha.community.service.QuestionService;
import com.ylhaha.community.service.UserService;
import okhttp3.internal.publicsuffix.PublicSuffixDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.PushBuilder;
import java.util.List;

/**
 * 处理问题的发布与储存
 *
 * @author yl
 */
@Controller
public class PublishController {

    @Autowired
    private QuestionService questionService;

    //到问题编辑页面
    @GetMapping("/publish/{id}")
    public String edit(@PathVariable("id")Integer id,
                       Model model){
        //得到Question数据并回显
        Question question = questionService.getQuestionById(id);
        if (question==null){
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        model.addAttribute("question",question);
        //得到标签数据
        List<TagDTO> tagDTOS = TagCache.get();
        model.addAttribute("tagDTOS",tagDTOS);
        return "publish";
    }


    //到问题发布页面
    @GetMapping("/publish")
    public String publish(Model model) {
        //得到标签数据
        List<TagDTO> tagDTOS = TagCache.get();
        model.addAttribute("tagDTOS",tagDTOS);
        return "publish";
    }

    //处理Question数据并储存到数据库
    @PostMapping("/question")
    public String postQuestion(@RequestParam("title") String title,
                               @RequestParam("description") String description,
                               @RequestParam("tag") String tag,
                               @RequestParam(name = "id",required = false)Integer id,
                               HttpServletRequest request,
                               Model model) {

        //先判断用户的登录状态
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            model.addAttribute("error", "用户未登录");
            //得到标签数据
            List<TagDTO> tagDTOS = TagCache.get();
            model.addAttribute("tagDTOS",tagDTOS);
            return "publish";
        }

        //校验表单
        if(title==null||"".equals(title)||"".equals(description)||"".equals(tag)||description==null||tag==null){
            model.addAttribute("error","问题不完整，请先填写完整");
            //得到标签数据
            List<TagDTO> tagDTOS = TagCache.get();
            model.addAttribute("tagDTOS",tagDTOS);
            return "publish";
        }

        //校验标签是否合法
        String invalid = TagCache.filterInvalid(tag);
        if(!StringUtils.isEmpty(invalid)){
            model.addAttribute("error","输入非法标签:"+invalid);
            //得到标签数据
            List<TagDTO> tagDTOS = TagCache.get();
            model.addAttribute("tagDTOS",tagDTOS);
            return "publish";
        }

        Question question = new Question();
        question.setCreator(user.getId());
        question.setDescription(description);
        question.setTag(tag);
        question.setTitle(title);
        if(id != null){
            question.setId((long)id);
        }
        //根据编辑/发布请求对数据库进行更新/插入操作
        questionService.updateOrInsert(question);

        return "redirect:/index";
    }
}
