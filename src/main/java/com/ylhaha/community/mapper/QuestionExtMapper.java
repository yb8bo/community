package com.ylhaha.community.mapper;



import com.ylhaha.community.dto.QuestionDTO;
import com.ylhaha.community.model.Question;

import java.util.List;

public interface QuestionExtMapper {
    int incView(Question record);


    int incComment(Question question);

    List<Question> selectRelated(Question question);
}