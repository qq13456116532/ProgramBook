package com.book.mapper.mongo;

import com.book.pojo.mongo.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookMapper extends MongoRepository<Book, String> {
}
