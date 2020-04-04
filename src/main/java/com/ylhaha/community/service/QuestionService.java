package com.ylhaha.community.service;

import com.ylhaha.community.dto.PageInfoDTO;
import com.ylhaha.community.dto.QuestionDTO;
import com.ylhaha.community.exception.CustomizeErrorCode;
import com.ylhaha.community.exception.CustomizeException;
import com.ylhaha.community.mapper.QuestionExtMapper;
import com.ylhaha.community.mapper.QuestionMapper;
import com.ylhaha.community.mapper.UserMapper;
import com.ylhaha.community.model.Question;
import com.ylhaha.community.model.QuestionExample;
import com.ylhaha.community.model.User;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yl
 */
@Service
public class QuestionService {
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private QuestionExtMapper questionExtMapper;




    //分页获取当前页的PageInfoDTO
    public PageInfoDTO list(Integer currentPage, Integer pageSize, long id) {
        //数据库limit查询的offset
        Integer offset = pageSize * (currentPage - 1);

        //获取QuestionDTO
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        List<Question> questions = null;
        QuestionExample questionExample = new QuestionExample();
        questionExample.setOrderByClause("gmt_create desc");
        if (id == 0) {
            questions = questionMapper.selectByExampleWithBLOBsWithRowbounds(questionExample,new RowBounds(offset, pageSize));
        } else {
            questionExample.createCriteria().andCreatorEqualTo(id);
            questions = questionMapper.selectByExampleWithBLOBsWithRowbounds(questionExample,new RowBounds(offset, pageSize));
        }
        User user = userMapper.selectByPrimaryKey((long)id);
        for (Question question : questions) {
            if (question != null) {
                //把数据传给QuestionDTO
                QuestionDTO questionDTO = new QuestionDTO();
                BeanUtils.copyProperties(question, questionDTO);
                if (id == 0) {
                    //根据creator（创建者id）得到用户创建者
                    User user1 = userMapper.selectByPrimaryKey(question.getCreator());
                    questionDTO.setUser(user1);
                } else {
                    questionDTO.setUser(user);
                }

                //封装好数据的questionDTO传给容器
                questionDTOList.add(questionDTO);
            }
        }

        PageInfoDTO pageInfoDTO = new PageInfoDTO();

        Integer totalCount;
        if (id == 0) {
            totalCount = (int)questionMapper.countByExample(questionExample);
        }else {
            questionExample.createCriteria().andCreatorEqualTo(id);
            totalCount = (int)questionMapper.countByExample(questionExample);
        }

        pageInfoDTO.setQuestionDTOS(questionDTOList);
        pageInfoDTO.setPageInfo(totalCount, currentPage, pageSize);
        return pageInfoDTO;
    }


    //根据问题ID获得一个QuestionDTO对象
    public QuestionDTO getQuestionDTOById(long id) {
        //获得Question
        Question question = questionMapper.selectByPrimaryKey(id);
        //捕获异常
        if(question == null){
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        //获得User
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        //填充数据
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);
        questionDTO.setUser(user);
        return questionDTO;
    }

    public Question getQuestionById(long id) {
        return questionMapper.selectByPrimaryKey(id);
    }

    //更新或插入数据
    public void updateOrInsert(Question question) {
        if(question.getId()==null){
            //id不存在，插入新问题
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question.setCommentCount(0);
            question.setViewCount(0);
            question.setLikeCount(0);
            questionMapper.insert(question);
        }else {
            //id存在，更新问题
            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTag(question.getTag());
            QuestionExample example = new QuestionExample();
            example.createCriteria()
                    .andIdEqualTo(question.getId());
            int updated = questionMapper.updateByExampleSelective(updateQuestion, example);
            if (updated != 1){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }

    //增加问题的浏览数
    public void incView(Long id) {
        Question updateQuestion = new Question();
        updateQuestion.setId(id);
        updateQuestion.setViewCount(1);
        questionExtMapper.incView(updateQuestion);
    }

    //根据tag获取相关问题
    public List<QuestionDTO> getRelated(QuestionDTO questionDTO) {

        if(StringUtils.isEmpty(questionDTO.getTag())){
            return new ArrayList<>();
        }

        //将tag中的,替换为|
        String tagRegex = StringUtils.replace(questionDTO.getTag(), ",", "|");

        Question question = new Question();
        question.setId(questionDTO.getId());
        question.setTag(tagRegex);

        List<Question> relatedQuestions = questionExtMapper.selectRelated(question);
        List<QuestionDTO> relatedQuestionDTOS = relatedQuestions.stream().map(q -> {
            QuestionDTO rQuestionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q, rQuestionDTO);
            return rQuestionDTO;
        }).collect(Collectors.toList());
        return relatedQuestionDTOS;
    }
}
