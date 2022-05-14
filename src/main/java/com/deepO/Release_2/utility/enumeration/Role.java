package com.deepO.Release_2.utility.enumeration;
import static com.deepO.Release_2.utility.constant.Authority.*;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {
    ROLE_USER(USER_AUTHORITIES),
    ROLE_HR(HR_AUTHORITIES),
    ROLE_MANAGER(MANAGER_AUTHORITIES),
    ROLE_ADMIN(ADMIN_AUTHORITIES),
    ROLE_SUPER_ADMIN(SUPER_ADMIN_AUTHORITIES);
	

    private String[] authorities;

//    Role(String... authorities) {
//        this.authorities = authorities;
//    }
//
//    public String[] getAuthorities() {
//        return authorities;
//    }
}
