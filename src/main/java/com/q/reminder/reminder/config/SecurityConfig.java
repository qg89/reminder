package com.q.reminder.reminder.config;

import com.q.reminder.reminder.service.impl.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.config.SecurityConfig
 * @Description :
 * @date :  2022.11.17 12:04
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig  {
    private final UserDetailsServiceImpl userDetailsService;

    //获取AuthenticationManager（认证管理器），登录时认证使用
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    //配置过滤
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf()
                .disable()//关闭csrf
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)//关闭session
                .and()
                .authorizeRequests(auth ->
                        auth.requestMatchers(
                                        "/user/**",
                                        "/weekly/**",
                                        "/p/**",
                                        "/table/**",
                                        "/workload/**"
                                )
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                ).httpBasic(Customizer.withDefaults())
                .userDetailsService(userDetailsService).build();
    }

    //配置加密方式
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
