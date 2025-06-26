package com.book.pojo;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Userreading {
    String id;
    String userid;
    String bookid;
    Date start;
    Date end;
}
