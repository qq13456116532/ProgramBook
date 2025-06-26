package com.book.pojo.mongo;

import com.book.pojo.Comments;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Document("book")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book implements Serializable {
    @org.springframework.data.annotation.Id
    String id;
    String headerImage;
    Date updateTime;
    String author;
    //简介
    String des;
    //主角名称
    String factionName;
    int newest;
    Comments[] comments;
    Object[] source;
    //0不在，1在列表中
    Integer isInList;
    //类型，用英文字符隔开   C语言,JAVA,Python,前端,其他
    String category;


    Integer hasRead;
    Double hot;
    Long chapterSum;
    @JsonIgnore
    String shortDes;
}
