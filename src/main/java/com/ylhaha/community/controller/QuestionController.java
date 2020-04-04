package com.ylhaha.community.controller;

import com.ylhaha.community.dto.CommentDTO;
import com.ylhaha.community.dto.QuestionDTO;
import com.ylhaha.community.enums.CommentTypeEnum;
import com.ylhaha.community.model.Question;
import com.ylhaha.community.service.CommentService;
import com.ylhaha.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 处理问题详情页面
 * @author yl
 */
@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    //得到Question相关数据并填充到问题详情页面
    @GetMapping("/question/{id}")
    public String question(@PathVariable("id")Long id,
                           Model model){
        //问题相关数据（先增加问题的浏览数）
        questionService.incView(id);
        //根据前端传来的参数id获取
        QuestionDTO questionDTO = questionService.getQuestionDTOById(id);

        //问题的回复相关数据
        List<CommentDTO> commentDTOS = commentService.listByTargetId(id, CommentTypeEnum.QUESTION);

        //相关问题
        List<QuestionDTO> relatedQuestions = questionService.getRelated(questionDTO);

        model.addAttribute("commentDTOS",commentDTOS);
        model.addAttribute("questionDTO",questionDTO);
        model.addAttribute("relatedQuestions",relatedQuestions);
        return "question";
    }
}
