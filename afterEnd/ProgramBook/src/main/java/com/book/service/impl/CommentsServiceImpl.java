package com.book.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.book.mapper.CommentsMapper;
import com.book.mapper.userMapper;
import com.book.pojo.Comments;
import com.book.pojo.user;
import com.book.service.CommentsService;
import com.book.vo.AddCommentsVo;
import com.book.vo.CommentsVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class CommentsServiceImpl implements CommentsService {

    @Resource
    CommentsMapper commentsMapper;

    @Resource
    userMapper userMapper;

    @Resource
    RedisTemplate redisTemplate;

    @Override
    public List<CommentsVO> listCommentsByBookId(String bookid, String userid) {
        log.info("根据bookid为" + bookid + "查询评论");
        List<Comments> comments = commentsMapper.selectList(new QueryWrapper<Comments>().eq("bookid", bookid).eq("father", "root"));
        List<CommentsVO> res = new ArrayList<>();
        for (Comments comment : comments) {
            String commentid = comment.getCommentid();
            List<Comments> child = commentsMapper.selectList(new QueryWrapper<Comments>().eq("father", commentid));
            CommentsVO commentsVO = new CommentsVO();
            comment.setLikenum(redisTemplate.opsForSet().size(commentid).intValue());
            comment.setIsLike(redisTemplate.opsForSet().isMember(commentid, userid) ? 1 : 0);
//            Userlike userlike = userlikeMapper.selectOne(new QueryWrapper<Userlike>().eq("commentid", commentid).eq("userid", userid));
//            if (userlike != null) {
//                comment.setIsLike(1);
//            } else
//                comment.setIsLike(0);
            commentsVO.setChild(child);
            commentsVO.setRootComment(comment);
            res.add(commentsVO);
        }
        return res;
    }

    @Resource
    userMapper usermapper;

    @Resource
    EmailServieImpl emailServie;
    @Override
    public String addComment(AddCommentsVo addCommentsVo) {
        Comments comments = new Comments();
        user user1 = usermapper.selectById(addCommentsVo.getUserid());
        comments.setNickname(user1.getNickName());
        comments.setAvatar(user1.getAvatar());
        comments.setBookid(addCommentsVo.getBookid());
        comments.setUserid(addCommentsVo.getUserid());
        comments.setContent(addCommentsVo.getContent());
        comments.setFather(addCommentsVo.getFather());
        comments.setLength(addCommentsVo.getContent().length());
        this.setDefaultAttribute(comments);
        user user = userMapper.selectById(addCommentsVo.getUserid());
        comments.setNickname(user.getNickName());
        comments.setTime(new Date());
        Comments commentFather = commentsMapper.selectById(comments.getFather());
        if(!comments.getFather().equals("root")){
            //这是二级评论
            user user2 = userMapper.selectById(commentFather.getUserid());
            comments.setReply(user2.getNickName());
        }
        commentsMapper.insert(comments);
        if (!comments.getFather().equals("root") && !commentFather.getUserid().equals(comments.getUserid())) {
            //父节点不为root,且不是自己评论自己的时候才发送邮件
            emailServie.insertReplyEmail(commentFather, comments);
        }
        return comments.getCommentid();
    }

    @Override
    public void addLikeNum(String bookid, String commentid) {
        Comments comments = commentsMapper.selectById(commentid);
        comments.setLikenum(comments.getLikenum() + 1);
        commentsMapper.updateById(comments);
    }

    public void setDefaultAttribute(Comments comments) {
        comments.setCommentid(UUID.randomUUID().toString());
        comments.setIsLike(0);
        comments.setLikenum(0);
        comments.setOpenMoreComment(false);
    }


}
