package com.shaojie.ZhiHu.dao;


import com.shaojie.ZhiHu.model.Comment;
import com.shaojie.ZhiHu.model.Feed;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Mapper
public interface   FeedDAO {


    String TABLE_NAME = " feed ";
    String INSERT_FIELD = " user_id,data,created_date,type ";
    String SELECT_FIELD = " id, "+INSERT_FIELD;


    @Insert({"insert into ",TABLE_NAME," (",INSERT_FIELD,
            ") values (#{userId},#{data},#{createdDate},#{type})"})
    int addFeed(Feed feed);



    @Select({"select ",SELECT_FIELD," from ",TABLE_NAME,"where id=#{id}"})
    Feed getFeedById(int id);



    List<Feed> selectUserFeeds(@Param("maxId") int maxId,
                               @Param("userIds") List<Integer> userIds,
                               @Param(("count")) int count);


}
