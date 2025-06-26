package com.book;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.book.mapper.UserloginMapper;
import com.book.mapper.mongo.BookMapper;
import com.book.pojo.Comments;
import com.book.pojo.Userlogin;
import com.book.pojo.mongo.Book;
import com.book.pojo.mongo.Chapter;
import com.book.service.CommentsService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import jakarta.annotation.Resource;
import lombok.val;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.Date;
import java.util.regex.Pattern;

@SpringBootTest
class ProgramBookApplicationTests {

    @Autowired
    BookMapper bookMapper;
    @Resource
    MongoTemplate mongoTemplate;
    @Test
    void contextLoads() {
        Criteria bookid = Criteria.where("bookid").is("63ad533d5346dbf6e8adca7a");
        Criteria num = Criteria.where("num").is(1);
        Query query = new Query();
        query.addCriteria(bookid);
        query.addCriteria(num);
        Chapter chapters = mongoTemplate.findOne(query, Chapter.class);
        System.out.println(chapters);
//        Query query1 = new Query(Criteria.where("_id").isNull());
//        List<Book> list = mongoTemplate.find(query1, Book.class);
//        List<Book> all = mongoTemplate.findAll(Book.class);
//        System.out.println(all);

    }
    @Autowired
    CommentsService commentsService;
    @Resource
    UserloginMapper userloginMapper;
    @Test
    void testMySQL()  {
        QueryWrapper<Userlogin> wrapper = new QueryWrapper<>();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String datestr = format.format(new Date());
        System.out.println(datestr);
        wrapper.apply("date_format(logindate,'%Y-%m-%d') = '"+ datestr + "'");
        wrapper.eq("userid","111");
        Userlogin userlogin = userloginMapper.selectOne(wrapper);
        System.out.println(userlogin);
    }

    @Test
    void testcontinuedays (){
        List<Userlogin> list = userloginMapper.selectList(new QueryWrapper<Userlogin>().eq("userid","ohaH053IbqZbgjlS3NAaulmoyUBs"));
        System.out.println("看一下list："+list);
        list.sort((o1, o2) -> o2.getLogindate().compareTo(o1.getLogindate()));
        System.out.println("看一下排序后的list："+list);
        int res=1;
        for(int i=0;i<list.size()-1;i++){
            Instant instant1 = list.get(i).getLogindate().toInstant();
            Instant instant2 = list.get(i + 1).getLogindate().toInstant();
            ZoneId zoneId = ZoneId.systemDefault();
            LocalDate localDate1= LocalDate.ofInstant(instant1, zoneId);
            LocalDate localDate2 = LocalDate.ofInstant(instant2, zoneId);
            long l = ChronoUnit.DAYS.between(localDate2,localDate1);
            System.out.println("打印一下两个日期"+localDate1+"\n"+localDate2);
            System.out.println(l);
            if(l==1){
                res++;
            }else{
                break;
            }
        }
        System.out.println("看一下res：" +res);

    }

    @Test
    void getmytest(){
        Pattern pattern = Pattern.compile("^.*" + "林" + ".*$", Pattern.CASE_INSENSITIVE);
        Query query1 = new Query();
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("author").regex(pattern),
                Criteria.where("factionName").regex(pattern));
        query1.addCriteria(criteria);
        List<Book> list = mongoTemplate.find(query1, Book.class);
        System.out.println(list);
    }


}
