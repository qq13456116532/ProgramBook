package com.book.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("bookofuser")
public class BookOfUser {
    String userid;
    String bookid;

    @TableField("hasRead")
    Integer hasRead;
}
