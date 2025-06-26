package com.book.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.book.mapper.UserloginMapper;
import com.book.pojo.Userlogin;
import com.book.service.UserService;
import com.book.utils.Result;
import com.book.utils.ResultCode;
import com.book.vo.UserVo;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Comparator;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    RedisTemplate redisTemplate;

    @Override
    public int hasFinishedWx(int wxcode) {
        Object c = redisTemplate.opsForValue().get("wxcode");
        return c == null ? 0 : -1;
    }

    @Override
    public Result getSessionId(int wxcode, UserVo userVo) {
        if (this.hasFinishedWx(wxcode) == -1) {
            return new Result(ResultCode.CUSTOMER_EXISTD, null);
        }
        return null;
    }

    @Resource
    UserloginMapper userloginMapper;

    @Override
    public int getContinueDays(String userid) {
        List<Userlogin> list = userloginMapper.selectList(new QueryWrapper<Userlogin>().eq("userid", userid));
        list.sort((o1, o2) -> o2.getLogindate().compareTo(o1.getLogindate()));
        int res = 1;
        for (int i = 0; i < list.size() - 1; i++) {
            Instant instant1 = list.get(i).getLogindate().toInstant();
            Instant instant2 = list.get(i + 1).getLogindate().toInstant();
            ZoneId zoneId = ZoneId.systemDefault();
            LocalDate localDate1 = LocalDate.ofInstant(instant1, zoneId);
            LocalDate localDate2 = LocalDate.ofInstant(instant2, zoneId);
            long l = ChronoUnit.DAYS.between(localDate2, localDate1);
//            System.out.println("打印一下两个日期"+localDate1+"\n"+localDate2);
//            System.out.println(l);
            if (l == 1) {
                res++;
            } else {
                break;
            }
        }
        return res;
    }


}
