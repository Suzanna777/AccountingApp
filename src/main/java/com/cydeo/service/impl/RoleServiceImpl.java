package com.cydeo.service.impl;

import com.cydeo.dto.RoleDto;
import com.cydeo.dto.UserDto;
import com.cydeo.entity.Role;
import com.cydeo.exception.RoleNotFoundException;
import com.cydeo.repository.RoleRepository;
import com.cydeo.service.RoleService;
import com.cydeo.service.SecurityService;
import com.cydeo.util.MapperUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final MapperUtil mapperUtil;
    private final SecurityService securityService;

    public RoleServiceImpl(MapperUtil mapperUtil, RoleRepository roleRepository, SecurityService securityService) {
        this.mapperUtil = mapperUtil;
        this.roleRepository = roleRepository;
        this.securityService = securityService;
    }

    @Override
    public RoleDto findById(Long id) {
        Optional<Role> roleOptional = roleRepository.findById(id);
        if (roleOptional.isPresent()) {
            Role role = roleOptional.get();
            return mapperUtil.convert(role, new RoleDto());
        } else {
            // Handle the case when the Role is not found
            // You can throw an exception or return a default value
            throw new RoleNotFoundException("Role not found with id: " + id);
        }
    }

    @Override
    public List<RoleDto> listAllRoles() {
        List<Role> roleList = roleRepository.findAll();
        return roleList.stream()
                .map(role -> mapperUtil.convert(role,new RoleDto()))
                .collect(Collectors.toList());
    }

    @Override
    public List<RoleDto> listAdminRoles() {
        UserDto currentUser = securityService.getLoggedInUser();
        List<Role> roleList = roleRepository.findAll();

        if(currentUser.getRole().getDescription().equalsIgnoreCase("root user")){
            return roleList.stream()
                    .filter(role -> role.getDescription().equalsIgnoreCase("admin"))
                    .map(role -> mapperUtil.convert(role,new RoleDto()))
                    .collect(Collectors.toList());
        }else{
            return roleList.stream()
                    .filter(role -> !role.getDescription().equalsIgnoreCase("root user"))
                    .map(role -> mapperUtil.convert(role,new RoleDto()))
                    .collect(Collectors.toList());
        }

    }
}
