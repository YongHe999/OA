package com.seu.main.config.security;

import com.seu.main.filter.TokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsUtils;

/**
 * spring security配置
 *
 * @author Ajie z-ajie@qq.com
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) //开启方法级别的权限控制
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;
    @Autowired
    private LogoutSuccessHandler logoutSuccessHandler;
    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private AccessDeniedHandler accessDeniedHandler;
    @Autowired
    @Qualifier("userDetailsServiceImpl")
    private UserDetailsService userDetailsService;
    @Autowired
    private TokenFilter tokenFilter;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //放行
        web.ignoring().antMatchers("/css/**","classpath:/templates/**","classpath:/static/**","/jquery-3.4.1/**","/js/**",
                "/json/**","/layui/**","/druid/**","/user/getCaptcha");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //关闭CSRF安全策略
        http.cors().and().csrf().disable().authorizeRequests()
                //处理跨域请求中的Preflight请求
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll();
        //token校验器
        http.addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);
        // 基于token，所以不需要session,设置只允许一处登陆开启session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        //权限控制规则，设置frameOptions为disable，前端iframe才可访问
        http.headers().frameOptions().disable();

        http.authorizeRequests()
                .antMatchers("/v2/**","/user/verify","/swagger-resources/**","/webjars/**","/swagger-ui.html").permitAll();

        //配置拦截所有请求
		http.authorizeRequests().anyRequest().authenticated();

        //设置用户只允许在一处登录，在其他地方登录则挤掉已登录用户，被挤掉的已登录用户则需要返回/login重新登录
//        http.sessionManagement().maximumSessions(1).expiredUrl("/login");
		
        //登录校验配置
        http.formLogin().usernameParameter("username").passwordParameter("password")
                .loginProcessingUrl("/login")

                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler).permitAll()
                .and()
                //异常
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint).accessDeniedHandler(accessDeniedHandler);
        //退出invalidateHttpSession关闭保存的的session
        http.logout().logoutUrl("/logout").logoutSuccessHandler(logoutSuccessHandler).invalidateHttpSession(true).permitAll();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }

}
