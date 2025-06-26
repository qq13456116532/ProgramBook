package com.book.service;

import com.book.utils.Result;
import com.book.vo.UserVo;

public interface UserService {


    public int hasFinishedWx(int wxcode);

    public Result getSessionId(int wxcode, UserVo userVo);

    int getContinueDays(String userid);
}
