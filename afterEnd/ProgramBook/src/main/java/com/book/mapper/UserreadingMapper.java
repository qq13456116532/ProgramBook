package com.book.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.book.pojo.Userreading;
import com.book.pojo.user;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Date;

@Mapper
public interface UserreadingMapper extends BaseMapper<Userreading> {


    @Select("SELECT * FROM userreading WHERE userid = #{userid} AND END IS NULL ORDER BY START DESC LIMIT 1")
    public Userreading getLastUnCompletedReading(String userid);

    @Select("SELECT SEC_TO_TIME(SUM(TIME_TO_SEC(TIMEDIFF(END,START))))  FROM userreading WHERE userid = #{userid}")
    public String getContinueTime(String userid);

    @Select("SELECT SEC_TO_TIME(SUM(TIME_TO_SEC(TIMEDIFF(END,START))))  FROM userreading WHERE userid = #{userid}  AND bookid = #{bookid}")
    public String getTimeByBookId(String userid, String bookid);

    @Select("SELECT COUNT(*) FROM (SELECT  DISTINCT bookid AS book FROM userreading WHERE userid = #{userid}) AS b")
    public int gethasBookNum(String userid);
}
