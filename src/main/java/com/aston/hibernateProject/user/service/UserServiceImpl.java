package com.aston.hibernateProject.user.service;

import com.aston.hibernateProject.exception.NotFoundException;
import com.aston.hibernateProject.user.dto.UserDto;
import com.aston.hibernateProject.user.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.aston.hibernateProject.user.UserMapper;
import com.aston.hibernateProject.user.UserRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        User user = UserMapper.returnUser(userDto);
        userRepository.save(user);
        return UserMapper.returnUserDto(user);
    }


    @Override
    @Transactional
    public UserDto update(UserDto userDto, long userId) {
        User exisitingUser = UserMapper.returnUser(userDto);
        exisitingUser.setId(userId);
        checkUser(userId);
        User user = userRepository.findById(userId).get();
        if (exisitingUser.getName() != null) {
            user.setName(exisitingUser.getName());
        }
        if (exisitingUser.getEmail() != null) {
            List<User> findEmail = userRepository.findByEmail(exisitingUser.getEmail());
            if (!findEmail.isEmpty() && findEmail.get(0).getId() != userId) {
                throw new NotFoundException("Пользователь с email: " + exisitingUser.getEmail() + " уже существует");
            }
            user.setEmail(exisitingUser.getEmail());
        }
        userRepository.save(user);
        return UserMapper.returnUserDto(user);
    }


    @Override
    @Transactional
    public void deleteById(long userId) {
        checkUser(userId);
        userRepository.deleteById(userId);
    }


    @Override
    @Transactional(readOnly = true)
    public UserDto findById(long userId) {
        checkUser(userId);
        return UserMapper.returnUserDto(userRepository.findById(userId).get());
    }

    @Override
    public List<UserDto> findAll() {
        return UserMapper.returnUserDtoList(userRepository.findAll());
    }

    private void checkUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь с ID %d не найден", userId));
        }
    }
}