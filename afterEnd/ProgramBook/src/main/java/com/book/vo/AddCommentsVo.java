package com.book.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AddCommentsVo {
    String bookid;
    String userid;
    String content;
    String father;
    String commentid;
}
