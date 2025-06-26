package com.book.pojo;

import com.book.pojo.mongo.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rank {
    String standard;
    String engName;
    String qidian;//起点
    String zongheng;//纵横
    String qd_url;
    Date updateTime;
    List<Book> qdRank;
    List<Book> zhRank;
}
