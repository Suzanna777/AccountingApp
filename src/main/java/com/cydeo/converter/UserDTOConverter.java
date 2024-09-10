package com.cydeo.converter;


import com.cydeo.dto.UserDto;
import com.cydeo.service.UserService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserDTOConverter implements Converter<String, UserDto> {

    private final UserService userService;

    public UserDTOConverter(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDto convert(String source) {
        return userService.findByUsername(source);
    }
}
