package com.cydeo.converter;

import com.cydeo.dto.RoleDto;
import com.cydeo.service.RoleService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class RoleDTOConverter implements Converter<String, RoleDto> {
    private final RoleService roleService;

    public RoleDTOConverter(RoleService roleService) {
        this.roleService = roleService;
    }

    @Override
    public RoleDto convert(String source) {

        if (source == null || source.isEmpty()) {
            return null;
        }

        return roleService.findById(Long.parseLong(source));
    }
}
