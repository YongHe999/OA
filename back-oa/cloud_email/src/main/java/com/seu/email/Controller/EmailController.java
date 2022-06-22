package com.seu.email.Controller;

import com.alibaba.fastjson.JSON;
import com.seu.email.service.MailService;
//import com.seu.util.entity.vo.ResultVo;
import com.seu.util.entity.vo.ResultVo;
import com.seu.util.sysEnum.ResultCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Map;

@Api(tags = "发送邮件")
@RestController
public class EmailController {
    @Resource
    private MailService mailService;

    @ApiOperation(value = "普通文本邮件")
    @PostMapping("/send")
    public ResultVo send(String data){
        if (data.isEmpty()){
            return new ResultVo(ResultCode.NULLFAIL);
        }
        Map<String, Object> emailContext= JSON.parseObject(data);
        String to = (String)emailContext.get("to");
        String title = (String)emailContext.get("title");
        String context= (String)emailContext.get("context");
        boolean result = mailService.send(to,title,context);
        return  result ? new ResultVo(ResultCode.SUCCESS) : new ResultVo(ResultCode.FAIL);
    }

    @ApiOperation(value = "发送html邮件")
    @PostMapping("/sendwithhtml")
    public ResultVo sendWithHtml(String data){
        if (data.isEmpty()){
            return new ResultVo(ResultCode.NULLFAIL);
        }
        Map<String, Object> emailContext= JSON.parseObject(data);
        String to = (String)emailContext.get("to");
        String title = (String)emailContext.get("title");
        String html= (String)emailContext.get("html");
        boolean result = mailService.sendWithHtml(to,title,html);
        return  result ? new ResultVo(ResultCode.SUCCESS) : new ResultVo(ResultCode.FAIL);
    }

    @ApiOperation(value = "发送带有图片的 html 邮件")
    @PostMapping("/sendwithimghtml")
    public ResultVo sendWithImgHtml(String data){
        Map<String, Object> emailContext= JSON.parseObject(data);
        String to = (String)emailContext.get("to");
        String title = (String)emailContext.get("title");
        String context= (String)emailContext.get("context");
        boolean result = mailService.sendWithImgHtml(to,title,context,null,null);
        return  result ? new ResultVo(ResultCode.SUCCESS) : new ResultVo(ResultCode.FAIL);
    }

    @ApiOperation(value = "发送带附件的邮件")
    @PostMapping("/sendwithenclosure")
    public ResultVo sendWithEnclosure(String data){
        if (data.isEmpty()){
            return new ResultVo(ResultCode.NULLFAIL);
        }
        Map<String, Object> emailContext= JSON.parseObject(data);
        String to = (String)emailContext.get("to");
        String title = (String)emailContext.get("title");
        String context= (String)emailContext.get("context");
        boolean result = mailService.sendWithEnclosure(to,title,context,null);
        return  result ? new ResultVo(ResultCode.SUCCESS) : new ResultVo(ResultCode.FAIL);
    }
}
