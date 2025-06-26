package com.book.pojo.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("chapter")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chapter {
    @org.springframework.data.annotation.Id
    String id;
    String bookid;
    //书的第几章
    Integer num;
    //章节名称
    String name;
    //章节内容
    String content;
    Date create_time;

}
