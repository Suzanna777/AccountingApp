package com.cydeo.aspect;

import com.cydeo.dto.UserDto;
import com.cydeo.entity.Company;
import com.cydeo.service.CompanyService;
import com.cydeo.service.SecurityService;
import com.cydeo.util.MapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    private final SecurityService securityService;
    private final CompanyService companyService;
    private final MapperUtil mapperUtil;

    public LoggingAspect(SecurityService securityService, CompanyService companyService, MapperUtil mapperUtil) {
        this.securityService = securityService;
        this.companyService = companyService;
        this.mapperUtil = mapperUtil;
    }

    private String getUserInfo(){
        UserDto userDto = securityService.getLoggedInUser();
        return userDto.getFirstname() +" "+ userDto.getLastname() +" "+ userDto.getUsername();
    }

    @Pointcut("execution (* com.cydeo.controller.CompanyController.activateCompany(*)) || execution(* com.cydeo.controller.CompanyController.deactivateCompany(*))")
    public void activateOrDeactivateCompany() {}

    @AfterReturning(pointcut = "activateOrDeactivateCompany()")
    public void afterReturningActivateOrDeactivateCompany(JoinPoint joinPoint) {

        Long companyId = (Long) joinPoint.getArgs()[0];
        Company company = mapperUtil.convert(companyService.findById(companyId), new Company());
        String companyName = company.getTitle();

        String method = joinPoint.getSignature().getName().contains("deactivate") ? "Deactivate" : "Activate";

        log.info("Method: {}, Company: {}, User: {}"
                , method
                , companyName
                , getUserInfo());
    }

}
