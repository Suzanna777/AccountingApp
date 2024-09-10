package com.cydeo.entity.common;


import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@Component
public class BaseEntityListener extends AuditingEntityListener {
   // @PrePersist//save

    private void onPrePersist(BaseEntity baseEntity){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        baseEntity.setInsertDateTime(LocalDateTime.now());
        baseEntity.setLastUpdateDateTime(LocalDateTime.now());
        if(authentication != null && !authentication .getName().equals("anonymousUser")){
            Object principal = authentication.getPrincipal();
            baseEntity.setInsertUserId(((UserPrincipal)principal).getId());
            //to get and map the id from Spring user to DB user id I added id to the UserPrincipal
            baseEntity.setLastUpdateUserId(((UserPrincipal)principal).getId());
        }
        // dynamically check whoever is login into the system
    }
   // @PreUpdate
    //update

    private void onPreUpdate(BaseEntity baseEntity){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        baseEntity.setLastUpdateDateTime(LocalDateTime.now());
        if(authentication != null && !authentication .getName().equals("anonymousUser")){
            Object principal = authentication.getPrincipal();
            //to get and map the id from Spring user to DB user id I added id to the UserPrincipal
            baseEntity.setLastUpdateUserId(((UserPrincipal)principal).getId());
        }
        // dynamically check whoever is login into the system
    }
}
