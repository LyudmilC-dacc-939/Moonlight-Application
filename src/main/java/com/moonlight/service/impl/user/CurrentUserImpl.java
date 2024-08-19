package com.moonlight.service.impl.user;

import com.moonlight.model.user.User;
import com.moonlight.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserImpl {
    private final UserService userService;

    @Autowired
    public CurrentUserImpl(UserService userService) {
        this.userService = userService;
    }

    public boolean isCurrentUserARole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (authority.getAuthority().equals("ROLE_ADMIN")) {
                    return true;
                }
            }
        }
        return false;
    }

    public Boolean isCurrentUserMatch(User sameUserCheck) {
        User currentUser = extractCurrentUser();
        if (currentUser != null && sameUserCheck != null) {
            return sameUserCheck.getEmailAddress().equals(extractCurrentUser().getEmailAddress());
        }
        return false;
    }

    public User extractCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            return (User) principal;
        } else {
            return userService.findByEmail(principal.toString());
        }
    }
}

