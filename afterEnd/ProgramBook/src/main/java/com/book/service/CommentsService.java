package com.book.service;

import com.book.vo.AddCommentsVo;
import com.book.vo.CommentsVO;

import java.util.List;

public interface CommentsService {

    public List<CommentsVO> listCommentsByBookId(String bookid, String userid);

    public String addComment(AddCommentsVo addCommentsVo);

    void addLikeNum(String bookid, String commentid);
}
