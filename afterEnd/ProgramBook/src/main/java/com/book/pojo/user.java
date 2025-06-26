package com.book.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class user {
    @TableId("openid")
    String openid;
    String avatar;
    @TableField("nickname")
    String nickName;
    //有效阅读时长
    @TableField("hasReadTime")
    Integer hasReadTime;
    @TableField("continueReadDay")
    //坚持阅读天数
    Integer continueReadDay;
    //已阅读的书籍
    @TableField("hasBookNum")
    Integer hasBookNum;
    //所有邮件
    @TableField("totalEmailsNum")
    Integer totalEmailsNum;

}
