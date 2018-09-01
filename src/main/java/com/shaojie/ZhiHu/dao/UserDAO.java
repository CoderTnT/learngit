package com.shaojie.ZhiHu.dao;


import com.shaojie.ZhiHu.model.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserDAO {

    //这样定义表以后更新更加方便,
    //注意前后空格
    String TABLE_NAME = " user ";
    String INSERT_FIELD = " name,password,salt,head_url ";
    String SELECT_FIELD = " id, "+INSERT_FIELD;

    @Insert({"insert into ",TABLE_NAME,"(",INSERT_FIELD,
            ") value (#{name},#{password},#{salt},#{headUrl})"})
     int addUser(User user);


    @Select({"select ",SELECT_FIELD,"from ",TABLE_NAME," where id = #{id}"})
    User selectById(int id);

    @Select({"select ",SELECT_FIELD,"from ",TABLE_NAME," where name = #{name}"})
    User selectByUsername(String name);

    @Update({"update ",TABLE_NAME," set password = #{password} where id = #{id}"})
    void updatePassword(User user);

    @Delete({"delete from ",TABLE_NAME," where id = #{id}"})
    void deleteById(int id);
}
