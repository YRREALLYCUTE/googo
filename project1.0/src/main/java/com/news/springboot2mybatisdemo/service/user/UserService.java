package com.news.springboot2mybatisdemo.service.user;

import com.github.pagehelper.PageInfo;
import com.news.springboot2mybatisdemo.model.UserDomain;

public interface UserService {

    int addUser(UserDomain user);

    PageInfo<UserDomain> findAllUser(int pageNum, int pageSize);

}
