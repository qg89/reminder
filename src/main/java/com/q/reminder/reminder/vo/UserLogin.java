package com.q.reminder.reminder.vo;

import com.q.reminder.reminder.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.UserLogin
 * @Description :
 * @date :  2022.11.17 14:15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLogin implements UserDetails {
    //传入用户对象
    private User user;

    /**
     * 判断权限信息
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return null;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * 判断是否未过期
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 判断账户是否未锁定
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 判断是否可以使用
     * @return
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
