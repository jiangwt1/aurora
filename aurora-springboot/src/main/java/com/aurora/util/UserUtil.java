package com.aurora.util;

import com.aurora.model.dto.UserDetailsDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Component
public class UserUtil {

    public static UserDetailsDTO getUserDetailsDTO() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsDTO) {
            return (UserDetailsDTO) principal;
        }
        throw new IllegalStateException("User not authenticated or invalid authentication type");
    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

}
