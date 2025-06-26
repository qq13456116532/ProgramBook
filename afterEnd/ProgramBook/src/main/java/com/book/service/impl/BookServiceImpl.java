package com.book.service.impl;

import com.book.mapper.mongo.BookMapper;
import com.book.pojo.mongo.Book;
import com.book.service.BookService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
@Slf4j
public class BookServiceImpl implements BookService {

    @Resource
    MongoTemplate mongoTemplate;

    @Override
    public List<Book> searchBook(String keyword) {
        Pattern pattern = Pattern.compile("^.*" + keyword + ".*$", Pattern.CASE_INSENSITIVE);
//        Pattern pattern = Pattern.compile("(?i)\\b" + keyword + "\\b");
        Query query1 = new Query();
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("author").regex(pattern),
                Criteria.where("factionName").regex(pattern));
        query1.addCriteria(criteria);
        List<Book> list = mongoTemplate.find(query1, Book.class);
        return list;
    }
}
