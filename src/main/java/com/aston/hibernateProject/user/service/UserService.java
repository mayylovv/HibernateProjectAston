package com.aston.hibernateProject.user.service;

import com.aston.hibernateProject.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto, long userId);

    void deleteById(long userId);

    UserDto findById(long userId);

    Collection<UserDto> findAll();

}