package com.book.service;

import com.book.pojo.Comments;
import com.book.pojo.Email;

import java.util.List;

public interface EmailServie {
    public List<Email> getAllEmailById(String userid);


    public void insertReplyEmail(Comments comments, Comments comments1);

    Integer readEmail(String emailId);
}
