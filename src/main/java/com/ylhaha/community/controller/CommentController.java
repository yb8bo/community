package com.ylhaha.community.controller;

import com.ylhaha.community.dto.CommentCreateDTO;
import com.ylhaha.community.dto.CommentDTO;
import com.ylhaha.community.dto.ResultDTO;
import com.ylhaha.community.enums.CommentTypeEnum;
import com.ylhaha.community.exception.CustomizeErrorCode;
import com.ylhaha.community.mapper.CommentMapper;
import com.ylhaha.community.model.Comment;
import com.ylhaha.community.model.User;
import com.ylhaha.community.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 评论相关的处理
 * @author yl
 */
@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    //对某个问题或评论进行回复：从前端接收一个json并往数据库中插入一个comment对象
    @ResponseBody
    @PostMapping("/comment")
    public Object post(@RequestBody CommentCreateDTO commentCreateDTO,
                       HttpServletRequest request){
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }
        if(commentCreateDTO==null || StringUtils.isEmpty(commentCreateDTO.getContent())){
            return ResultDTO.errorOf(CustomizeErrorCode.CONTENT_IS_EMPTY);
        }
        Comment comment = new Comment();
        comment.setContent(commentCreateDTO.getContent());
        comment.setParentId(commentCreateDTO.getParentId());
        comment.setType(commentCreateDTO.getType());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(System.currentTimeMillis());
        comment.setCommentator(user.getId());
        comment.setLikeCount(0L);
        comment.setCommentCount(0);
        commentService.insert(comment);
        return ResultDTO.okOf();
    }


    //从数据库获得二级评论
    @ResponseBody
    @GetMapping("/comment/{id}")
    public ResultDTO<List<CommentDTO>> subComment(@PathVariable("id")Long id){
        List<CommentDTO> subCommentDTOS = commentService.listByTargetId(id, CommentTypeEnum.COMMENT);
        return  ResultDTO.okOf(subCommentDTOS);
    }
}
