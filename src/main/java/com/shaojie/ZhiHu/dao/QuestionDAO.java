package com.shaojie.ZhiHu.dao;


import com.shaojie.ZhiHu.model.Question;
import com.shaojie.ZhiHu.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface QuestionDAO {

    //这样定义表以后更新更加方便,
    //注意前后空格
    String TABLE_NAME = " question ";
    String INSERT_FIELD = " title,content,created_date,user_id,comment_count ";
    String SELECT_FIELD = " id, "+INSERT_FIELD;

    @Insert({"insert into ",TABLE_NAME,"(",INSERT_FIELD,
            ") value (#{title},#{content},#{createdDate},#{userId},#{commentCount})"})
    int addQuestion(Question question);


    @Select({"select ",SELECT_FIELD," from ",TABLE_NAME," where id = #{id}"})
    Question selectQuestionById(int id);


    List<Question> selectLatestQuestions(@Param("userId") int userId,
                               @Param("offset") int offset,
                               @Param("limit") int limit);

    List<Question> selectLatestQuestionsByType(@Param("userId") int userId,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit,
                                         @Param("classification") String classification);

    @Update({"update ", TABLE_NAME,"set comment_count=#{commentCount}  where id =#{id}"})
    int updateQuestionCommentCount(@Param("id") int id,@Param("commentCount") int commentCount);


}
