package com.seu.chat.config;

import com.alibaba.fastjson.JSON;
import com.seu.chat.dto.ResponseInfo;
import com.seu.chat.feign.UserService;
import com.seu.util.entity.SysUser;
import com.seu.util.entity.vo.ResultVo;
import com.seu.util.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * TODO
 *
 * @author z-ajie@qq.com
 * @version 1.0
 * @date 2021/10/23 13:19
 * 使用原生WebSocket此HandshakeInterceptor类不生效，需改为Spring boot推荐写法后配置
 */
public class WebSocketInterceptor implements HandshakeInterceptor {
    @Autowired
    private UserService userService;

    /**
     * 连接前
     * @param request
     * @param response
     * @param webSocketHandler
     * @param map
     * @return
     * @throws Exception
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {
        //获取URI中的Token
        System.out.println("========================>拦截");
        HttpServletRequest req = (HttpServletRequest) request;
        String token = req.getParameter("token");
        ResultVo resultVo = userService.getUser(token);
        if (resultVo.getCode().equals(200)){
            SysUser sysUser= JSON.toJavaObject(JSON.parseObject(JSON.toJSONString(resultVo.getData())), SysUser.class);
            if (sysUser != null) {
                return true;
            }else {
                ResponseUtil.responseJson((HttpServletResponse) response, HttpStatus.UNAUTHORIZED.value(), new ResponseInfo(HttpStatus.UNAUTHORIZED.value(), "无效的用户！"));
                return false;
            }
        }else {
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler webSocketHandler, Exception e) {

    }
}
