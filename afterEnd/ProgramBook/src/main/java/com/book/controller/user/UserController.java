package com.book.controller.user;


import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.book.mapper.EmailMapper;
import com.book.mapper.UserloginMapper;

import com.book.pojo.Email;
import com.book.pojo.Userlogin;
import com.book.pojo.user;
import com.book.utils.Result;
import com.book.utils.ResultCode;
import com.book.vo.LoginDataVo;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


//@RestController("/user/")
@RestController
@Slf4j
public class UserController {

    @Autowired
    com.book.mapper.userMapper userMapper;
    @Resource
    RedisTemplate redisTemplate;

    @Value("${aliyun.oss.endpoint}")
    private String ossEndpoint;

    @Value("${aliyun.oss.access-key-id}")
    private String ossAccessKeyId;

    @Value("${aliyun.oss.access-key-secret}")
    private String ossAccessKeySecret;

    @Value("${aliyun.oss.bucket-name}")
    private String ossBucketName;

    @Value("${wechat.appid}")
    private String wechatAppid;

    @Value("${wechat.secret}")
    private String wechatSecret;
    

    @GetMapping("/user/judgeExpire")
    @ResponseBody
    public Result judgeExpire(@RequestParam(value = "token") String token) {
        log.info("当前正在执行judgeExpire，token是" + token);
        Object o = redisTemplate.opsForValue().get(token);
        if (o == null) {
            //已经过期了
            return new Result(ResultCode.ERROR, null);
        } else {
            //没过期
            return new Result(ResultCode.SUCCESS, null);
        }
    }

    @Resource
    UserloginMapper userloginMapper;

    @Resource
    EmailMapper emailMapper;
    public String uploadAvatar(String userid,String tempurl) throws IOException {
        String endpoint = ossEndpoint;
        String accessKeyId = ossAccessKeyId;
        String accessKeySecret = ossAccessKeySecret;
        String bucketName = ossBucketName;

        // 填写Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
        String imageName = "ProgramBook/"+UUID.randomUUID().toString()+".jpg";
        tempurl = tempurl.replaceFirst("^data:[^;]+;base64,", "");

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        // 将 base64 编码的图片转为字节数组
        byte[] imageData = Base64.getDecoder().decode(tempurl);
        // 上传图片到 OSS
        ossClient.putObject(bucketName, imageName, new ByteArrayInputStream(imageData));
        ossClient.shutdown();
        return "https://wk-gulimall.oss-cn-beijing.aliyuncs.com/"+imageName;

    }
    @PostMapping("/user/onLogin")
    public Result onLogin(@RequestBody LoginDataVo loginDataVo) throws IOException {
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        String appid = wechatAppid;
        String secret = wechatSecret;
        HttpGet httpGet = new HttpGet("https://api.weixin.qq.com/sns/jscode2session?appid=" + appid +
                "&secret=" + secret + "&js_code=" + loginDataVo.getCode());

        // 响应模型
        CloseableHttpResponse response = null;
        String openidout = "";
        String session_keyout = "";
        String token = "";
        try {
            // 由客户端执行(发送)Get请求
            response = httpClient.execute(httpGet);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            System.out.println("响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
//                System.out.println("响应内容为:" + EntityUtils.toString(responseEntity));
                String s = EntityUtils.toString(responseEntity);
                System.out.println("字符串s" + s);
                JSONObject parseObject = JSONObject.parseObject(s);
                String openid = (String) parseObject.get("openid");
                if(openid==null)
                    return new Result(ResultCode.ERROR,null);
                String session_key = (String) parseObject.get("session_key");
                openidout = openid;
                session_keyout = session_key;
                user id = userMapper.selectById(openid);
                if (id == null) {
                    user user = new user();
                    user.setOpenid(openid);
                    user.setAvatar(this.uploadAvatar(openid,loginDataVo.getAvatar()));
                    user.setNickName(loginDataVo.getNickName());
                    user.setContinueReadDay(1);
                    userMapper.insert(user);
                    Userlogin userlogin = new Userlogin();
                    userlogin.setUserid(openid);
                    userlogin.setLogindate(new Date());
                    userlogin.setLogintime(new Date());
                    userloginMapper.insert(userlogin);
                    Email email = new Email();
                    email.setId(UUID.randomUUID().toString());
                    email.setUserid(openid);
                    email.setIsRead(false);
                    email.setBgImgUrl("https://img0.baidu.com/it/u=1397706616,1605176518&fm=253&fmt=auto&app=138&f=JPEG?w=755&h=500");
                    email.setDigestAuthorName("熊猫老师");
                    email.setDigestAuthorImg("https://img0.baidu.com/it/u=1397706616,1605176518&fm=253&fmt=auto&app=138&f=JPEG?w=755&h=500");
                    email.setDigestDes("宣传部");
                    email.setTitle("“阅读推行赠书计划”已经开始");
                    email.setContentButtonText("加入计划");
                    email.setContentButtonUrl("../shop/shop");
                    email.setContentButtonEventHandler("goToUrl");
                    emailMapper.insert(email);
                }
                token = UUID.randomUUID().toString();
                redisTemplate.opsForValue().set(token, openid, 12, TimeUnit.HOURS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("token", token);
//        map.put("session_key", openidout);
        return new Result(ResultCode.SUCCESS, map);
    }
}
