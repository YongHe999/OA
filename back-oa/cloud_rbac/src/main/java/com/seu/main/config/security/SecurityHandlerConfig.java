package com.seu.main.config.security;

import com.seu.main.dto.LoginUser;
import com.seu.main.dto.ResponseInfo;
import com.seu.main.dto.TokenDto;
import com.seu.main.service.TokenService;
import com.seu.util.sysEnum.ResultCode;
import com.seu.util.utils.CookieUtils;
import com.seu.util.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * spring security处理器
 * @author Ajie z-ajie@qq.com
 *
 */
@Configuration
public class SecurityHandlerConfig {
	@Autowired
	private TokenService tokenService;
	/**
	 * 登陆成功，返回用户信息
	 * @return
	 */
	@Bean
	public AuthenticationSuccessHandler loginSuccessHandler() {
		return (request, response, authentication) -> {
			LoginUser loginUser = (LoginUser) authentication.getPrincipal();


			//生成token
			TokenDto tokenDto = tokenService.save(loginUser);
			//返回给前端
			Map<String,Object> resMap=new HashMap<>();
			resMap.put("code",HttpStatus.OK.value());
			resMap.put("message","登录成功！");
			resMap.put("uuid",loginUser.getId());
			resMap.put("token",tokenDto.getToken());
			ResponseUtil.responseJson(response, HttpStatus.OK.value(), resMap);
		};
	}
	/**
	 * 登陆失败
	 * 
	 * @return
	 */
	@Bean
	public AuthenticationFailureHandler loginFailureHandler() {
		return (request, response, exception) -> {
			String msg = null;

			ResponseInfo info=null;

			if (exception instanceof BadCredentialsException) {
				msg = "密码错误";
				info = new ResponseInfo(ResultCode.PASSWORD_ERROR.getCode(),msg);
			} else{
				msg = exception.getMessage();
				info=new ResponseInfo(ResultCode.USERNAME_ERROR.getCode(),msg);
			}
			ResponseUtil.responseJson(response, ResultCode.PASSWORD_ERROR.getCode(), info);
		};

	}

	/**
	 * 未登录，返回402
	 * 
	 * @return
	 */
	@Bean
	public AuthenticationEntryPoint authenticationEntryPoint() {
		return (request, response, authException) -> {
			ResponseInfo info = new ResponseInfo(ResultCode.UNAUTHENTICATED.getCode() , "请重新登录！");
			ResponseUtil.responseJson(response, ResultCode.UNAUTHENTICATED.getCode(), info);
		};
	}
	/**
	 * 权限不足，返回401
	 *
	 * @return
	 */
	@Bean
	public AccessDeniedHandler accessDeniedHandler() {
		return (request, response, AccessDeniedException) -> {

			ResponseInfo info = new ResponseInfo(ResultCode.UNAUTHORISE.getCode(), "权限不足！");

			ResponseUtil.responseJson(response, ResultCode.UNAUTHORISE.getCode(), info);
		};
	}

	/**
	 * 退出处理
	 * 
	 * @return
	 */

	@Bean
	public LogoutSuccessHandler logoutSussHandler() {

		return (request, response, authentication) -> {
			CookieUtils.deleteCookie(request,response,"token");

			CookieUtils.deleteCookie(request,response,"uuid");
			ResponseInfo info;
			String jwtToken=request.getHeader("token");
			if(StringUtils.isEmpty(jwtToken)){
				info = new ResponseInfo(HttpStatus.UNAUTHORIZED.value() , "当前没有用户登录！");
			}else {
				if (tokenService.delete(jwtToken)){
					info = new ResponseInfo(HttpStatus.OK.value(), "退出成功！");
				}
				else {
					info = new ResponseInfo(HttpStatus.UNAUTHORIZED.value(), "无效的用户！");
				}
			}
			ResponseUtil.responseJson(response, HttpStatus.OK.value(), info);
		};
	}
}
