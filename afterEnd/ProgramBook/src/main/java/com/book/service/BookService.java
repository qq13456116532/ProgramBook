package com.book.service;

import com.book.pojo.mongo.Book;

import java.util.List;

public interface BookService {
    List<Book> searchBook(String keyword);
}
