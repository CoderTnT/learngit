package com.shaojie.ZhiHu.service;


import com.shaojie.ZhiHu.dao.QuestionDAO;
import com.shaojie.ZhiHu.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class QuestionService {


    @Autowired
    QuestionDAO questionDAO;

    @Autowired
    SensitiveService sensitiveService;


    public List<Question> selectLatestQuestions(int userId,int offset,int limit){


        return questionDAO.selectLatestQuestions(userId, offset, limit);
    }


    public List<Question> selectLatestQuestionsByType(int userId,int offset,int limit,String classification){

        return questionDAO.selectLatestQuestionsByType(userId, offset, limit, classification);
    }


    public int addQuestion(Question question){

        //防止脚本注入

        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));
        //过滤敏感词

        question.setTitle(sensitiveService.textFilter(question.getTitle()));
        question.setContent(sensitiveService.textFilter(question.getContent()));


       return questionDAO.addQuestion(question) >0?question.getId():0;

    }

    public Question selectQuestionById(int id){

        return questionDAO.selectQuestionById(id);

    }

    public int updateQuestionCommentCount(int id,int commentCount){

        return questionDAO.updateQuestionCommentCount(id,commentCount);
    }


}
