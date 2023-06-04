package com.book.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("email")
public class Email {
    String id;
    String userid;
    String bookid;
    String commentid;
    @TableField("isRead")
    Boolean isRead;
    @TableField("bgImgUrl")
    String bgImgUrl; // 背景图片
    @TableField("DigestAuthorName")
    String DigestAuthorName; //消息的用户名
    @TableField("DigestAuthorImg")
    String DigestAuthorImg;//消息的用户头像
    @TableField("DigestDes")
    String DigestDes; //两种，宣传部 或 用户
    String title;
    @TableField("contentButtonText")
    String contentButtonText;//按钮文字
    @TableField("contentButtonUrl")
    String contentButtonUrl; //按钮的URL
    @TableField("contentButtonEventHandler")
    String contentButtonEventHandler; //基本是goToUrl；

}
