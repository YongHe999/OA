package com.seu.main.controller;



import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonView;
import com.seu.main.ueditor.ActionEnter;
import org.apache.xmlbeans.impl.xb.xmlconfig.ConfigDocument;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 用于处理关于ueditor插件相关的请求
 * @author lhh
 * @date 2019-06-19
 *
 */
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/ueditor")
public class UeditorController {

//    @Autowired
//    private AppendixController appendixController;

    //配置ueditor后端上传接口的验证
    @GetMapping("/config")
    public String config(HttpServletRequest request, HttpServletResponse response, String action, MultipartFile[] upfile) throws Exception {
        System.out.println("1231213");

        if (action.equals("config")) {
            request.setCharacterEncoding("utf-8");
            response.setHeader("Content-Type", "text/html");
            String path = ClassUtils.getDefaultClassLoader().getResource("").getPath() + "config";

            PrintWriter printWriter = response.getWriter();
            printWriter.write(new ActionEnter(request, path).exec());
            printWriter.flush();
            printWriter.close();
        } else if (action.equals("uploadimage")) {
//            JsonView jsonView = appendixController.upload(upfile);
//            Map<String, Object> map = ((List<Map<String, Object>>) jsonView.get("datas")).get(0);
//            Map<String, Object> result = new HashMap<String, Object>();
//
//            result.put("title", map.get("fileName"));
//            result.put("original", map.get("srcName"));
//            result.put("state", "SUCCESS");
//            result.put("url", imgUrl + map.get("fileName"));
//            String jStr = JSON.toJSONString(result);
//            return jStr;
        }
        return null;
    }
}

