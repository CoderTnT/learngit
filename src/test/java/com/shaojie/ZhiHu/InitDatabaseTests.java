package com.shaojie.ZhiHu;

import com.shaojie.ZhiHu.dao.QuestionDAO;
import com.shaojie.ZhiHu.dao.UserDAO;
import com.shaojie.ZhiHu.model.EntityType;
import com.shaojie.ZhiHu.model.Question;
import com.shaojie.ZhiHu.model.User;
import com.shaojie.ZhiHu.service.FollowService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InitDatabaseTests {


	@Autowired
	UserDAO userDAO;


    @Autowired
    QuestionDAO questionDAO;

    @Autowired
    FollowService followService;


	@Test
	public void contextLoads() {
        Random random = new Random() ;

        for(int i =0;i<11;i++){

//            User user = new User();
//            user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png",random.nextInt(1000)));
//            user.setName(String.format("USER%d",i));
//            user.setPassword("1");
//            user.setSalt("1");
//            userDAO.addUser(user);
//
//            user.setPassword("2");
//
//            userDAO.updatePassword(user);

            Question question = new Question();

            question.setCommentCount(i);
            question.setContent(String.format("lalalall %d",i));
            question.setTitle(String.format("Title[%d]",i));
            Date date = new Date();
            date.setTime(date.getTime()+1000*3600*i);
            question.setCreatedDate(date);
            question.setUserId(i);

            for(int j=i;j<i;j++){

                followService.follow(j, EntityType.ENTITY_USER,i);

            }


         questionDAO.addQuestion(question);

        }


      //  Assert.assertEquals("2", userDAO.selectById(1).getPassword());

       // userDAO.deleteById(1);

        //Assert.assertNull(userDAO.selectById(1));

        System.out.print(questionDAO.selectLatestQuestions(0,0,10));


	}

}
