package com.book.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.book.pojo.Comments;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentsMapper extends BaseMapper<Comments> {
}
