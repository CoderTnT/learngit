package com.shaojie.ZhiHu.controller;


import com.shaojie.ZhiHu.asycn.EventModel;
import com.shaojie.ZhiHu.asycn.EventProducer;
import com.shaojie.ZhiHu.asycn.EventType;
import com.shaojie.ZhiHu.model.*;
import com.shaojie.ZhiHu.service.*;
import com.shaojie.ZhiHu.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Controller
public class SearchController {

    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);


    @Autowired
    SearchService searchService;

    @Autowired
    FollowService followService;

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @RequestMapping(value = "/search",method = RequestMethod.GET)
    public String addComment(Model model, @RequestParam("q") String keyword,
                             @RequestParam(value="offset" ,defaultValue = "0") int offset,
                             @RequestParam(value="limit" ,defaultValue = "10") int limit){

        try{

            List<Question> questionList = searchService.searchQuestion(keyword,offset,limit,"<font size=\"4\" face=\"arial\" color=\"red\">",
                                            "</font>");


            List<ViewObject> viewObjects = new ArrayList<>();

            for (Question question : questionList) {

                ViewObject viewObject = new ViewObject();

                Question q = questionService.selectQuestionById(question.getId());

                if(question.getContent()!=null){

                    q.setContent(question.getContent());

                }

                if(question.getTitle()!=null){

                    q.setTitle(question.getTitle());

                }

                viewObject.set("question", q);

                viewObject.set("user", userService.getUser(q.getUserId()));
                viewObject.set("followerCount", followService.getFollowersCount(EntityType.ENTITY_QUESTION, q.getId()));

                viewObjects.add(viewObject);

            }
            model.addAttribute("viewObjects",viewObjects);



        }catch (Exception e){

            logger.error(e.getMessage());
        }
        return "result";

    }
}
