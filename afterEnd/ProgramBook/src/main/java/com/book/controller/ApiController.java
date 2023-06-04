package com.book.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.book.conf.MyCustomInterceptor;
import com.book.mapper.*;
import com.book.mapper.mongo.BookMapper;
import com.book.pojo.*;
import com.book.pojo.mongo.Book;
import com.book.pojo.mongo.Chapter;
import com.book.service.BookService;
import com.book.service.CommentsService;
import com.book.service.EmailServie;
import com.book.service.UserService;
import com.book.utils.Result;
import com.book.utils.ResultCode;
import com.book.vo.AddCommentsVo;
import com.book.vo.CommentsVO;
import com.book.vo.MuluVo;
import com.book.vo.deleteData;
import io.netty.util.internal.StringUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@Slf4j
public class ApiController {

    @Autowired
    MyCustomInterceptor myCustomInterceptor;

    @Resource
    MongoTemplate mongoTemplate;

    @Autowired
    com.book.mapper.userMapper userMapper;

    @Resource
    BookOfUserMapper bookOfUserMapper;

    @Resource
    BookService bookService;

    @Resource
    CommentsMapper commentsMapper;

    @Autowired
    EmailServie emailServie;

    @GetMapping("/api/user/uploadAvatar")
    public Result uploadAvatar(@RequestParam("userid")String userid,@RequestParam("tempurl")String tempurl) throws IOException {
        user user = userMapper.selectById(userid);
        String endpoint = "https://oss-cn-beijing.aliyuncs.com";
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = "LTAI5tSduPoFKT4sAN6DzXdg";
        String accessKeySecret = "WLTigXI1o7f6fH9Njj3CmMukBnTzbJ";
        // 填写Bucket名称，例如examplebucket。
        String bucketName = "wk-gulimall";
        // 填写Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
        String imageName = "ProgramBook/"+UUID.randomUUID().toString();

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        URL url = new URL(tempurl); // HTTP URL
        try (InputStream inputStream = url.openStream()) {
            ossClient.putObject(bucketName, imageName, inputStream);
        }
        ossClient.shutdown();
        user.setAvatar(imageName);
        userMapper.insert(user);
        return new Result(ResultCode.SUCCESS, null);
    }

    @GetMapping("/api/user/getAllEmails")
    public Result getEmails() {
        log.info("当前正在执行getAllEmails");
        List<Email> emails = emailServie.getAllEmailById(myCustomInterceptor.getUserId());
        System.out.println("getEmails（）查出来了数据: " + emails);
        return new Result(ResultCode.SUCCESS, emails);
    }

    @PostMapping("/api/email/readEmail")
    public Result ReadEmail(@RequestParam("emailId") String emailId){
        Integer isSuccess = emailServie.readEmail(emailId);
        return new Result(ResultCode.SUCCESS, isSuccess);
    }

    @GetMapping("/api/user/getAllTasks")
    public Result getAllTasks() {
        System.out.println("当前正在执行getAllTasks");
        List<BookOfUser> bookOfUsers = bookOfUserMapper.BOOK_OF_USER_LIST(myCustomInterceptor.getUserId());
        ArrayList<String> bookids = new ArrayList<>();
        for (BookOfUser bookOfUser : bookOfUsers) {
            String bookid = bookOfUser.getBookid();
            bookids.add(bookid);
        }
        Query query = Query.query(Criteria.where("_id").in(bookids));
        List<Book> topBook = mongoTemplate.find(query, Book.class);
        for (int i = topBook.size() - 1; i >= 0; i--) {
            topBook.get(i).setHasRead(bookOfUsers.get(i).getHasRead());
            Double aDouble = redisTemplate.opsForZSet().score("BookReadCount", bookids.get(i));
            double score = aDouble == null ? 0.0 : aDouble;
            topBook.get(i).setHot(score);
            String curid = bookids.get(i);
            List<Chapter> chapters = mongoTemplate.find(new Query().addCriteria(Criteria.where("bookid").is(curid)), Chapter.class);
//            long count = mongoTemplate.count(new Query().addCriteria(Criteria.where("bookid").is(bookids.get(i))), Chapter.class);
            long count = chapters.size();
            topBook.get(i).setChapterSum(count);
        }
        if(topBook.size()==0){
            log.error("从BookofUser中查询不到");
            Set<String> topBooks = redisTemplate.opsForZSet().reverseRange("BookReadCount", 0, 1);
            Query query1 = Query.query(Criteria.where("_id").in(topBooks));
            List<Book> resBook = mongoTemplate.find(query1, Book.class);
            for (int i = resBook.size() - 1; i >= 0; i--) {
                resBook.get(i).setHasRead(1);
                Double aDouble = redisTemplate.opsForZSet().score("BookReadCount", resBook.get(i).getId());
                double score = aDouble == null ? 0.0 : aDouble;
                resBook.get(i).setHot(score);
                String curid = resBook.get(i).getId();
                List<Chapter> chapters = mongoTemplate.find(new Query().addCriteria(Criteria.where("bookid").is(curid)), Chapter.class);
//            long count = mongoTemplate.count(new Query().addCriteria(Criteria.where("bookid").is(bookids.get(i))), Chapter.class);
                long count = chapters.size();
                resBook.get(i).setChapterSum(count);
            }
            return new Result(ResultCode.SUCCESS, resBook);
        }
        return new Result(ResultCode.SUCCESS, topBook);
    }


    @GetMapping("/api/chapter/chapterByNumAndBookId")
    public Result chapterByNumAndBookId(@RequestParam("bookid") String bookid
            , @RequestParam("num") int num) {
        Query query1 = new Query();
        query1.addCriteria(Criteria.where("bookid").is(bookid));
        query1.addCriteria(Criteria.where("num").is(num));
        Chapter chapter = mongoTemplate.findOne(query1, Chapter.class);
        HashMap<String, String> map = new HashMap<>();
        map.put("content", chapter.getContent());

        return new Result(ResultCode.SUCCESS, map);
    }

    @GetMapping("/api/chapter/maxChapterNum")
    public Result getmaxChapterNum(@RequestParam("bookid") String bookid) {
        List<Chapter> chapters = mongoTemplate.find(new Query().addCriteria(Criteria.where("bookid").is(bookid)), Chapter.class);
        return new Result(ResultCode.SUCCESS, chapters.size());
    }

    @GetMapping("/api/user/recommendBooks")
    public Result recommendBooks() {
        log.info("当前正在执行recommendBooks，获得推荐书本");
        Set<String> topBooks = redisTemplate.opsForZSet().reverseRange("BookReadCount", 0, 5);
        Query query = Query.query(Criteria.where("_id").in(topBooks));
        List<Book> topBook = mongoTemplate.find(query, Book.class);
        return new Result(ResultCode.SUCCESS, topBook);
    }

    @PostMapping("/api/user/RemoveLikeNum")
    public Result RemoveLikeNum(@RequestBody AddCommentsVo addCommentsVo) {
        log.info("正在执行RemoveLikeNum去掉点赞,bookid是" + addCommentsVo.getBookid() + "，commentid是" + addCommentsVo.getCommentid() + "，userid是" + myCustomInterceptor.getUserId());
        redisTemplate.opsForSet().remove(addCommentsVo.getCommentid(), myCustomInterceptor.getUserId());
        return new Result(ResultCode.SUCCESS, null);
    }

    @Resource
    UserreadingMapper userreadingMapper;

    @GetMapping("/api/user/hasBookNum")
    public Result GethasBookNum() {
        log.info("当前正在获取已经阅读的书数量");
        int res = userreadingMapper.gethasBookNum(myCustomInterceptor.getUserId());
        return new Result(ResultCode.SUCCESS, res);
    }

    @GetMapping("/api/book/getOneBookReadingTime")
    public Result getOneBookReadingTime( @RequestParam("bookid") String bookid) {
        log.info("当前正在执行获取一本书的阅读时长，getOneBookReadingTime,bookid是" + bookid);
        String continueTime = userreadingMapper.getTimeByBookId(myCustomInterceptor.getUserId(), bookid);
        if (continueTime == null) {
            this.StartReading(bookid);
            HashMap<String, Integer> map = new HashMap<>();
            map.put("hours", 0);
            map.put("minutes", 0);
            return new Result(ResultCode.SUCCESS, map);
        }
        String[] strings = continueTime.split(":");
        int minutes = Integer.parseInt(strings[1]);
        int hours = Integer.parseInt(strings[0]);
        HashMap<String, Integer> map = new HashMap<>();
        map.put("hours", hours);
        map.put("minutes", minutes);
        return new Result(ResultCode.SUCCESS, map);
    }

    @GetMapping("/api/user/continueTime")
    public Result GetcontinueTime() {
        log.info("当前正在获取已经阅读时间");
        String continueTime = userreadingMapper.getContinueTime(myCustomInterceptor.getUserId());
        if (continueTime == null) {
            return new Result(ResultCode.SUCCESS, 0);
        }
        String[] strings = continueTime.split(":");
        int minutes = Integer.parseInt(strings[1]);
        int hours = Integer.parseInt(strings[0]);
        int res = hours * 60 + minutes;
        return new Result(ResultCode.SUCCESS, res);
    }

    @GetMapping("/api/user/endReading")
    public Result endReading(@RequestParam("token") String token) {
        log.info("当前正在结束阅读endReading，token是" + token);
        Userreading userreading = new Userreading();
        userreading.setId(token);
        userreading.setEnd(new Date());
        userreadingMapper.updateById(userreading);
        return new Result(ResultCode.SUCCESS, null);
    }

    @GetMapping("/api/user/StartReading")
    public Result StartReading( @RequestParam("bookid") String bookid) {
        log.info("当前正在开始阅读StartReading ,bookid是" + bookid);
        String uuid = UUID.randomUUID().toString();
        Userreading lastUnCompletedReading = userreadingMapper.getLastUnCompletedReading(myCustomInterceptor.getUserId());
        if (lastUnCompletedReading != null) {
            lastUnCompletedReading.setEnd(new Date());
            userreadingMapper.updateById(lastUnCompletedReading);
        }
        if (StringUtil.isNullOrEmpty(myCustomInterceptor.getUserId())) {
            System.out.println("StartReading获取的userid是空，出问题了");
            return new Result(ResultCode.ERROR, null);
        }
        Userreading userreading = new Userreading();
        userreading.setId(uuid);
        userreading.setUserid(myCustomInterceptor.getUserId());
        userreading.setBookid(bookid);
        userreading.setStart(new Date());
        userreadingMapper.insert(userreading);
        return new Result(ResultCode.SUCCESS, uuid);
    }

    @GetMapping("/api/book/getClassify")
    public Result getClassify(@RequestParam("index") String index, @RequestParam("page") String page) {
        log.info("当前正在获取分类信息getClassify，index是" + index);
        HashMap<String, Object> map = new HashMap<>();
        PageRequest request = PageRequest.of(Integer.parseInt(page) - 1, 6);
        Query query1 = new Query();
        List<Book> list = new ArrayList<>();
        query1.with(request);
        if (index.equals("0")) {
            list = mongoTemplate.find(query1, Book.class);
            map.put("list", list);
            map.put("total", mongoTemplate.findAll(Book.class).size());
        } else {
            query1.addCriteria(Criteria.where("index").is(index));
            list = mongoTemplate.find(query1, Book.class);
            map.put("list", list);
            Query query2 = new Query();
            query2.addCriteria(Criteria.where("index").is(index));
            map.put("total", mongoTemplate.find(query2, Book.class).size());
        }
        return new Result(ResultCode.SUCCESS, map);
    }


    @GetMapping("/api/xs_rank/getRank")
    public Result getRank(@RequestParam("rankType") String rankType) {
        log.info("当前正在获取排行榜信息getRank，rankType是" + rankType);
        Rank rank = new Rank();
        rank.setStandard("标准");
        List<Book> books = mongoTemplate.findAll(Book.class);
        rank.setQdRank(books);
//        rank.setZhRank(books);
        ArrayList<Rank> ranks = new ArrayList<Rank>();
        ranks.add(rank);
        return new Result(ResultCode.SUCCESS, ranks);
    }

    @GetMapping("/api/book")
    public Result getAllBooks() {
        log.info("当前正在获取所有书籍/api/book");
        List<Book> books = mongoTemplate.findAll(Book.class);
        return new Result(ResultCode.SUCCESS, books);
    }

    @GetMapping("/api/book/searchBook")
    public Result searchBook(@RequestParam("keyword") String keyword) {
        log.info("当前正在书城搜索searchBook，keyword是" + keyword);
        List<Book> books = bookService.searchBook(keyword);
        return new Result(ResultCode.SUCCESS, books);
    }

    @GetMapping("/api/chapter/getContentById")
    public Result getContentById(@RequestParam("sectionId") String sectionId, @RequestParam("bookid") String bookid) {
        log.info("当前正在getContentById，sectionId是" + sectionId);
        BookOfUser userid1 = bookOfUserMapper.selectOne(new QueryWrapper<BookOfUser>().eq("userid", myCustomInterceptor.getUserId()).eq("bookid", bookid));
//        Integer hasRead = userid1.getHasRead();
        Query query1 = new Query(Criteria.where("_id").is(sectionId));
        Chapter chapter = mongoTemplate.findOne(query1, Chapter.class);
        Integer num = chapter.getNum();
        userid1.setHasRead(num);
        bookOfUserMapper.update(userid1,new QueryWrapper<BookOfUser>().eq("userid",userid1.getUserid()).eq("bookid",userid1.getBookid()));
        HashMap<String, String> map = new HashMap<>();
        map.put("content", chapter.getContent());
        return new Result(ResultCode.SUCCESS, chapter);
    }

    @GetMapping("/api/book/getMulu")
    public Result getMulu(@RequestParam("bookId") String bookid, @RequestParam("sectionNum") Integer sectionNum) {
        log.info("当前正在获取目录getMulu，bookid是" + bookid + "，章节数是：" + sectionNum);
        Query query1 = new Query(Criteria.where("_id").is(bookid));
        Book book = mongoTemplate.findOne(query1, Book.class);
        Criteria c1 = Criteria.where("bookid").is(bookid);
        Query query2 = new Query(c1);
        List<Chapter> ts = mongoTemplate.find(query2, Chapter.class);
        HashMap<String, Object> map = new HashMap<>();
        map.put("author", book.getAuthor());
        map.put("headImg", book.getHeaderImage());
        map.put("des", book.getDes());
        List<MuluVo> muluVos = new ArrayList<>();
        for (Chapter t : ts) {
            MuluVo muluVo = new MuluVo();
            muluVo.setSectionId(t.getId());
            muluVo.setSectionNum(t.getNum());
            muluVo.setSectionTitle(t.getName());
            muluVos.add(muluVo);
        }
        map.put("sectionArray", muluVos);
        return new Result(ResultCode.SUCCESS, map);
    }


    @GetMapping("/api/chapter/getContentByHistory")
    public Result getContentByHistory( @RequestParam("bookid") String bookid) {
        log.info("正在获取书的内容getContentByHistory ，bookid是" + bookid);
        BookOfUser userid1 = bookOfUserMapper.selectOne(new QueryWrapper<BookOfUser>().eq("userid", myCustomInterceptor.getUserId()).eq("bookid", bookid));
        Integer hasRead = 1;
        if (userid1 != null) {
            hasRead = userid1.getHasRead();
        }
        Criteria c1 = Criteria.where("bookid").is(bookid);
        Criteria num = Criteria.where("num").is(hasRead);
        Query query = new Query();
        query.addCriteria(c1);
        query.addCriteria(num);
        Chapter chapters = mongoTemplate.findOne(query, Chapter.class);
        return new Result(ResultCode.SUCCESS, chapters);
    }

    @GetMapping("/api/user/JudgeIsInList")
    public Result GetJudgeIsInList( @RequestParam("bookid") String bookid) {
        log.info("当前判断是否书本在用户书架里JudgeIsInList,  bookid是" + bookid);
        BookOfUser bookOfUser = bookOfUserMapper.selectOne(new QueryWrapper<BookOfUser>().eq("userid", myCustomInterceptor.getUserId()).eq("bookid", bookid));
        if (bookOfUser == null) {
            return new Result(ResultCode.SUCCESS, 0);
        }
        return new Result(ResultCode.SUCCESS, 1);
    }

    @GetMapping("/api/user/getuserid")
    public Result GetUserId(){
        return  new Result(ResultCode.SUCCESS,myCustomInterceptor.getUserId());
    }
    @PostMapping("/api/user/addMyBooks")
    public Result addMyBooks(@RequestBody deleteData deleteData) {
        log.info("当前正在添加书到用户书架deleteMyBooks" + deleteData);
        BookOfUser bookOfUser1 = bookOfUserMapper.selectOne(new QueryWrapper<BookOfUser>().eq("userid", deleteData.getUserid()).eq("bookid", deleteData.getBookid()));
        if (bookOfUser1 != null) {
            return new Result(ResultCode.CUSTOMER_EXISTD, null);
        }
        BookOfUser bookOfUser = new BookOfUser(deleteData.getUserid(), deleteData.getBookid(), 1);
        bookOfUserMapper.insert(bookOfUser);
        return new Result(ResultCode.SUCCESS, null);

    }

    @PostMapping("/api/user/deleteMyBooks")
    public Result deleteMyBooks(@RequestBody deleteData deleteData) {
        log.info("当前正在删除用户书架deleteMyBooks" + deleteData);
        int delete = bookOfUserMapper.delete(new QueryWrapper<BookOfUser>().eq("userid", deleteData.getUserid()).eq("bookid", deleteData.getBookid()));
        return new Result(ResultCode.SUCCESS, null);
    }

    @Resource
    UserloginMapper userloginMapper;

    @GetMapping("/api/user/signIn")
    public Result EveryDaySignIn(@RequestParam("userid") String userid) {
        log.info("当前正在进行每日签到signIn，userid是" + userid);
        if (userid.equals("undefined")) {
            return new Result(ResultCode.ERROR, null);
        }
        QueryWrapper<Userlogin> wrapper = new QueryWrapper<>();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String datestr = format.format(new Date());
        wrapper.apply("date_format(logindate,'%Y-%m-%d') = '" + datestr + "'");
        wrapper.eq("userid", userid);
        List<Userlogin> userlogins = userloginMapper.selectList(wrapper);
        if (userlogins == null || userlogins.size() == 0) {
            Userlogin userlogin = new Userlogin();
            userlogin.setUserid(userid);
            userlogin.setLogindate(new Date());
            userlogin.setLogintime(new Date());
            userloginMapper.insert(userlogin);
        }
        return new Result(ResultCode.SUCCESS, null);

    }

    @GetMapping("/api/user/getMyBooks")
    public Result getMyBooks() {
        log.info("正在执行getMyBooks");
        String userid = myCustomInterceptor.getUserId();
        List<BookOfUser> bookOfUsers = bookOfUserMapper.selectList(new QueryWrapper<BookOfUser>().eq("userid", userid));
        ArrayList<Book> res = new ArrayList<>();
        bookOfUsers.forEach(e -> {
            Query query1 = new Query();
            Criteria criteria = new Criteria();
            query1.addCriteria(Criteria.where("_id").is(e.getBookid()));
            Book book = mongoTemplate.findOne(query1, Book.class);
            book.setDes(book.getDes().substring(0, book.getDes().length()>80?80:book.getDes().length()) + "...");
            res.add(book);
        });
        return new Result(ResultCode.SUCCESS, res);
    }

    @GetMapping("/api/user/getUserInfo")
    public Result getUserInfo() {
        log.info("正在执行getUserInfo");
        user user = userMapper.selectById(myCustomInterceptor.getUserId());
        if (user == null) {
            return new Result(ResultCode.USER_LONGIN_EXIST, null);
        }
        return new Result(ResultCode.SUCCESS, user);
    }

    @Resource
    UserService userService;

    @GetMapping("/api/user/getContinueDays")
    public Result getContinueDays() {
        log.info("当前正在获取用户连续登录天数" );
        int continueDays = userService.getContinueDays(myCustomInterceptor.getUserId());
        return new Result(ResultCode.SUCCESS, continueDays);
    }

    @GetMapping("/api/book/getBookDetail")
    public Result getBookDetail(@RequestParam("bookid") String bookid) {
        log.info("正在执行getBookDetail,bookid是" + bookid);
        redisTemplate.opsForZSet().incrementScore("BookReadCount", bookid, 1);
        Query query = new Query(Criteria.where("_id").is(bookid));
        Book book = mongoTemplate.findOne(query, Book.class);
        return new Result(ResultCode.SUCCESS, book);
    }

    @Autowired
    CommentsService commentsService;

    @GetMapping("/api/book/listComments")
    public Result listComments(@RequestParam("bookid") String bookid) {
        log.info("正在获取书本评论,bookid是" + bookid+",userid是"+myCustomInterceptor.getUserId());
        List<CommentsVO> comments = commentsService.listCommentsByBookId(bookid, myCustomInterceptor.getUserId());
        return new Result(ResultCode.SUCCESS, comments);
    }

    @PostMapping("/api/book/addComment")
    public Result addComment(@RequestBody AddCommentsVo addCommentsVo) {
        log.info("当前正在添加评论，addComment" + addCommentsVo);
        String commentid;
        try {
            commentid = commentsService.addComment(addCommentsVo);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(ResultCode.ERROR, null);
        }
        return new Result(ResultCode.SUCCESS, commentid);
    }


    @Resource
    RedisTemplate redisTemplate;
    @PostMapping("/api/book/addLikeNum")
    public Result addLikeNum(@RequestBody AddCommentsVo addCommentsVo) {
        log.info("正在执行addLikeNum添加点赞数,bookid是" + addCommentsVo.getBookid() + "，commentid是" + addCommentsVo.getCommentid() + "，userid是" + addCommentsVo.getUserid());
        Long add = redisTemplate.opsForSet().add(addCommentsVo.getCommentid(), addCommentsVo.getUserid());
        return new Result(ResultCode.SUCCESS, 1);
    }

}
