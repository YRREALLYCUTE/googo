package com.news.springboot2mybatisdemo.dao;

import com.news.springboot2mybatisdemo.model.UserDomain;

import java.util.List;

public interface UserDao {

    int insert(UserDomain record);

    List<UserDomain> selectUsers();
}
