package com.ylhaha.community.service;

import com.ylhaha.community.dto.CommentDTO;
import com.ylhaha.community.enums.CommentTypeEnum;
import com.ylhaha.community.exception.CustomizeErrorCode;
import com.ylhaha.community.exception.CustomizeException;
import com.ylhaha.community.mapper.*;
import com.ylhaha.community.model.*;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author yl
 */
@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private CommentExtMapper commentExtMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionExtMapper questionExtMapper;
    @Autowired
    private UserMapper userMapper;

    //在数据库中保存一条评论
    @Transactional
    public void insert(Comment comment) {
        //捕获无问题直接评论异常
        if(comment.getParentId()==null || comment.getParentId()==0){
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }
        //捕获无Type异常
        if(comment.getType()==null || !CommentTypeEnum.isExit(comment.getType())){
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }
        //判断评论回复对象：问题/评论
        if(comment.getType().equals(CommentTypeEnum.COMMENT.getType())){
            //回复评论
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            //捕获回复空评论异常
            if (dbComment == null) {
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            commentMapper.insert(comment);
            //增加评论的评论数
            dbComment.setCommentCount(1);
            commentExtMapper.incComment(dbComment);
        }else {
            //回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            //捕获回复空问题异常
            if (question == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            //增加问题的评论数
            question.setCommentCount(1);
            questionExtMapper.incComment(question);
        }
    }

    //根据问题/评论id从数据库获取回复相关信息
    public List<CommentDTO> listByTargetId(Long id, CommentTypeEnum type) {
        //获取问题/评论下的所有回复
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria().andParentIdEqualTo(id).andTypeEqualTo(type.getType());
        //根据创建时间倒叙查询
        commentExample.setOrderByClause("gmt_create desc");
        List<Comment> comments = commentMapper.selectByExample(commentExample);
        if(comments.size()==0){
            return new ArrayList<>();
        }
        //得到这些问题的评论者的id，不重复记录（出现就记一次）
        Set<Long> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());

        //根据commentators得到对应的用户
        UserExample userExample = new UserExample();
        List<Long> userIds = new ArrayList<>();
        userIds.addAll(commentators);
        userExample.createCriteria().andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);
        //对CommentDTO对象的数据填充
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));
        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment,commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());
        return commentDTOS;
    }
}
