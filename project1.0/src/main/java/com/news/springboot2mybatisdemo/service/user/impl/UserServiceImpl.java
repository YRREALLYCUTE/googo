package com.news.springboot2mybatisdemo.service.user.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.news.springboot2mybatisdemo.dao.UserDao;
import com.news.springboot2mybatisdemo.model.UserDomain;
import com.news.springboot2mybatisdemo.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "UserService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public int addUser(UserDomain user) {
        return userDao.insert(user);
    }

    @Override
    public PageInfo<UserDomain> findAllUser(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<UserDomain> userDomains = userDao.selectUsers();
        return new PageInfo(userDomains);
    }
}
