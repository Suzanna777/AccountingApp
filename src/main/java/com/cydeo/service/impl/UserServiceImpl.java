package com.cydeo.service.impl;

import com.cydeo.dto.UserDto;
import com.cydeo.entity.User;
import com.cydeo.exception.UserNotFoundException;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.SecurityService;
import com.cydeo.service.UserService;
import com.cydeo.util.MapperUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MapperUtil mapperUtil;
    private final SecurityService securityService;
    private final PasswordEncoder passwordEncoder;


    public UserServiceImpl(MapperUtil mapperUtil, UserRepository userRepository, @Lazy SecurityService securityService, @Lazy PasswordEncoder passwordEncoder) {
        this.mapperUtil = mapperUtil;
        this.userRepository = userRepository;
        this.securityService = securityService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto findByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User not found with username: " + username);
        }
        UserDto userDto = new UserDto();
        return mapperUtil.convert(user, userDto);
    }

    @Override
    public List<UserDto> listAllUsers() {
        UserDto currentUser = securityService.getLoggedInUser();
        List<User> userList = userRepository.findAll();
        Stream<User> userStream;


        if (currentUser.getRole().getDescription().equals("Root User")) {

            userStream = userList.stream()
                    .filter(user -> user.getRole().getDescription().equals("Admin") && user.getIsDeleted().equals(false))
                    .sorted(Comparator.comparing(user -> user.getCompany().getTitle()));

        } else if (currentUser.getRole().getDescription().equals("Admin")) {
            userStream = userList.stream()
                    .filter(user -> user.getCompany().getTitle().equals(currentUser.getCompany().getTitle()) && user.getIsDeleted().equals(false))
                    .sorted(Comparator.comparing(user -> user.getRole().getDescription()));
        } else {
            userStream = userList.stream()
                    .filter(user -> user.getIsDeleted().equals(false))
                    .sorted(Comparator.comparing(user -> user.getCompany().getTitle()));
        }
        return userStream
                .map(user -> {
                    UserDto userDto = mapperUtil.convert(user, new UserDto());
                    userDto.setOnlyAdmin(isOnlyAdmin(userDto));
                    return userDto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserDto save(UserDto user) {
        User existUser = userRepository.findByUsername(user.getUsername());
        if (existUser != null) {
            throw new IllegalArgumentException("A user with this email already exists. Please try with a different email.");
        }
        User userEntity = mapperUtil.convert(user, new User());
        userEntity.setPassword(passwordEncoder.encode(user.getPassword()));
        userEntity.setEnabled(true); // this will be dynamic
        userRepository.save(userEntity);

        return user;
    }

    @Override
    public UserDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + id));

        UserDto userDto = mapperUtil.convert(user, new UserDto());
        if (userDto.getRole() == null) {
            userDto.setOnlyAdmin(false);
        } else {
            userDto.setOnlyAdmin(isOnlyAdmin(userDto));
        }
        return userDto;
    }

    @Transactional
    @Override
    public void updateUser(UserDto user) {

        User userEntity = userRepository.findById(user.getId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id" + user.getId()));
        User convertedUser = mapperUtil.convert(user, new User());
        convertedUser.setId(userEntity.getId());
        userRepository.save(convertedUser);

    }

    @Transactional
    @Override
    public void deleteUser(Long id) {

        User userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found" + id));

        String tempEmail = "temp_" + id + "@example.com";
        userEntity.setUsername(tempEmail);
        userEntity.setIsDeleted(true);
        userRepository.save(userEntity);
    }


    public boolean isOnlyAdmin(UserDto userDto) {

        if (!userDto.getRole().getDescription().equalsIgnoreCase("admin")) {
            return false;
        }
        return userRepository.countAllByCompany_IdAndRole_Description(userDto.getCompany().getId(), "Admin") == 1;
    }

}





