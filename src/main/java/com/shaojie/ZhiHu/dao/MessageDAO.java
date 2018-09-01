package com.shaojie.ZhiHu.dao;


import com.shaojie.ZhiHu.model.Message;
import javafx.scene.control.Tab;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessageDAO {


    String TABLE_NAME = " message ";
    String INSERT_FIELD = " from_id,to_id,conversation_id,content,created_date,has_read ";
    String SELECT_FIELD = " id, "+INSERT_FIELD;


    @Insert({"insert into ",TABLE_NAME,"(",INSERT_FIELD,
            ")values(#{fromId},#{toId},#{conversationId},#{content},#{createdDate},#{hasRead})"})
    int addMessage(Message message);


    @Select({"select ",SELECT_FIELD," from ",TABLE_NAME,"where conversation_id=#{conversationId} order by created_date desc limit #{offset},#{limit}"})
    List<Message> selectMessageDetailList(@Param("conversationId") String conversationId,
                                          @Param("offset") int offset,
                                          @Param("limit") int limit);


    @Select({"select ",INSERT_FIELD,", count(id) as id  from( select *"+
            " from ",TABLE_NAME,"where from_id=#{userId} or to_id = #{userId} order by created_date desc limit 10000000000  ) tt group by conversation_id order by created_date" +
            " desc limit #{offset},#{limit}  "})
    List<Message> selectMessageList(@Param("userId") int userId,
                                          @Param("offset") int offset,
                                          @Param("limit") int limit);



    @Select({"select count(id) from ",TABLE_NAME," where has_read = 0 and to_id =#{userId} and conversation_id = #{conversationId}"})
    int getConversationUnReadCount(@Param("userId") int userId,
                                   @Param("conversationId") String conversationId);


    @Update({"update ",TABLE_NAME,"set has_read=#{hasRead} where to_id=#{userId} and conversation_id = #{conversationId}"})
    void updateConversationHasRead(@Param("userId") int userId,
                                   @Param("conversationId") String conversationId,
                                   @Param("hasRead") int hasRead);

}



