package com.book.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.book.pojo.BookOfUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BookOfUserMapper extends BaseMapper<BookOfUser> {

    @Select("SELECT * FROM bookofuser where userid = #{userid} ORDER BY hasRead DESC LIMIT 2 ; ")
    List<BookOfUser> BOOK_OF_USER_LIST(String userid);

}
