package anb52.jobhunter.config;


import anb52.jobhunter.domain.User;
import anb52.jobhunter.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component("userDetailsService")
public class UserDetailsCustom implements UserDetailsService {
    private UserService userService;
    public UserDetailsCustom(UserService userService){
        this.userService = userService;
    }
    @Override
    //Tìm kiếm người dùng dựa vào username
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userService.handleGetUserByUsername(username);
        if(user == null){
            throw new UsernameNotFoundException("Username/password không hợp lệ");
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
