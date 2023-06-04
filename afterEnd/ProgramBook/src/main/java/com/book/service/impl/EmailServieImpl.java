package com.book.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.book.mapper.CommentsMapper;
import com.book.mapper.EmailMapper;
import com.book.mapper.userMapper;
import com.book.pojo.Comments;
import com.book.pojo.Email;
import com.book.service.EmailServie;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
public class EmailServieImpl implements EmailServie {

    @Resource
    EmailMapper emailMapper;
    @Resource
    CommentsMapper commentsMapper;

    @Resource
    userMapper userMapper;

    @Override
    public List<Email> getAllEmailById(String userid) {
        List<Email> emails = emailMapper.selectList(new QueryWrapper<Email>().eq("userid", userid).eq("isRead",0));
        for (Email email : emails) {
            //todo 感觉这里可以在插入的时候解决了
//            if(email.getDigestDes().equals("宣传部")){
//            }else {
//                Comments comments = commentsMapper.selectOne(new QueryWrapper<Comments>().eq("commentid", email.getCommentid()));
//                user user = userMapper.selectOne(new QueryWrapper<user>().eq("openid", comments.getUserid()));
//                String avatar = user.getAvatar();
//                String nickName = user.getNickName();
//                email.setBgImgUrl(avatar);
//            }

        }
        return emails;
    }

    @Override
    public void insertReplyEmail(Comments commentsfather, Comments comments) {
        //commentsfather是被回复者，comments是回复者
        Email email = new Email();
        email.setDigestAuthorImg(comments.getAvatar());
        email.setCommentid(comments.getCommentid());
        email.setUserid(commentsfather.getUserid());
        email.setBookid(comments.getBookid());
        email.setBgImgUrl(comments.getAvatar());
        email.setContentButtonText("查看详情");
        email.setId(UUID.randomUUID().toString());
        email.setDigestDes("用户");
        email.setContentButtonUrl("../book_detail/book_detail?bookid="+comments.getBookid());
        email.setDigestAuthorName(comments.getNickname());
        email.setTitle(comments.getContent().length()>8?comments.getContent().substring(0, 7):comments.getContent() + "...");
        emailMapper.insert(email);
    }

    @Override
    public Integer readEmail(String emailId) {
        Email email = emailMapper.selectById(emailId);
        if(!StringUtils.isEmpty(email.getBookid())){
            email.setIsRead(true);
            return emailMapper.updateById(email);
        }else{
            return -1;
        }

    }


}
