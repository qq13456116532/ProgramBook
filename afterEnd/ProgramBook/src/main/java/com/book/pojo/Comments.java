package com.book.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comments {
    @TableId
    String commentid;
    String bookid;
    //发评论的人的nickname
    String nickname;
    String avatar;
    Date time;
    //父评论的id，如果是顶层评论就是 ”root“
    String father;
    Integer likenum;
    String content;
    String userid;
    Integer length;
    @TableField("isOpenMoreComment")
    boolean isOpenMoreComment;
    //被回复的评论的nickname
    String reply;
    //0表示未点赞，1表示点赞
    @TableField("isLike")
    Integer isLike;

}
