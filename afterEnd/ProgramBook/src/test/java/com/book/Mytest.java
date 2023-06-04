package com.book;

import com.book.pojo.mongo.Book;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@SpringBootTest
public class Mytest {
    @Resource
    MongoTemplate mongoTemplate;
    @Test
    public  void testGetBookDeatil(){
        String bookid = "测试ID";
        Query query = new Query(Criteria.where("_id").is(bookid));
        Book book = mongoTemplate.findOne(query, Book.class);
        System.out.println("测试的详情信息是： "+book);
    }


}
