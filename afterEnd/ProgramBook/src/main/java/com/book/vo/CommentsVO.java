package com.book.vo;

import com.book.pojo.Comments;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentsVO {

    //某一个评论的最顶层评论
    Comments rootComment;
    //评论的回复
    List<Comments> child;

}
