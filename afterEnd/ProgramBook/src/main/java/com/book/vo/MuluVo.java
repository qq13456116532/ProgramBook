package com.book.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MuluVo {
    //章节名称
    String sectionId;
    //第几章
    Integer sectionNum;
    //章节题目
    String sectionTitle;
}
