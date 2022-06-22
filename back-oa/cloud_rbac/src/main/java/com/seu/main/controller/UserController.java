package com.seu.main.controller;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ramostear.captcha.HappyCaptcha;
import com.ramostear.captcha.support.CaptchaType;
import com.seu.main.dto.LoginUser;

import com.seu.main.service.TokenService;
import com.seu.main.service.UserService;
import com.seu.util.entity.SysUser;
import com.seu.util.entity.vo.ResultVo;
import com.seu.util.entity.vo.UserVo;
import com.seu.util.sysEnum.ResultCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 用户相关接口
 * @author Ajie
 */

@RestController
@Api(tags = "用户相关接口")
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;
	@Autowired
	TokenService tokenService;

	private static String vcode = "";

	@ApiOperation("获取验证码")
	@GetMapping("/getCaptcha")
	public void getCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HappyCaptcha.require(request, response).type(CaptchaType.NUMBER).build().finish();

		vcode = (String) request.getSession().getAttribute("happy-captcha");
	}


	@ApiOperation("验证验证码是否正确")
	@PostMapping("/verify")
	public ResultVo verify(String data, HttpServletRequest request) {
		//Verification Captcha
		System.out.println(data);
//		boolean flag = HappyCaptcha.verification(request, code.replace("\"",""), true);
		if (data.replace("\"","").equals(vcode))
			return new ResultVo(ResultCode.SUCCESS);
		vcode = "";
		return new ResultVo(502,"验证码有误！");
	}
	@GetMapping
	@ApiOperation("获取用户分页列表")
	public ResultVo pageListUsers(String data){

        //封装查询参数
        Map<String, Object> params=JSON.parseObject(data);

        return userService.pageListUsers(params,new Page<UserVo>((int) params.get("page"), (int) params.get("limit"),true));
	}

	@GetMapping("/userList")
	@ApiOperation("其他用户列表")
	public ResultVo ListOrderUsers(HttpServletRequest request){
		QueryWrapper<SysUser> sysUserQueryWrapper = new QueryWrapper<>();
		LoginUser loginUser= tokenService.getLoginUser(request.getHeader("token"));
		sysUserQueryWrapper.ne("username", loginUser.getUsername());
		return new ResultVo(ResultCode.SUCCESS,userService.list(sysUserQueryWrapper));
	}

	@PostMapping
	@ApiOperation("新增用户")
	@PreAuthorize("hasAuthority('sys_user_add')")
	public ResultVo save(String data) {
		SysUser sysUser= JSON.toJavaObject(JSON.parseObject(data), SysUser.class);
		SysUser u = userService.getByUserName(sysUser.getUsername());
		if (u != null) {
			return new ResultVo(ResultCode.EXIT);
		}
		return userService.saveU(sysUser);
	}

	@PostMapping("/userRole")
	@ApiOperation("新增用户角色关系")
	@PreAuthorize("hasAuthority('sys_user_role')")
	public ResultVo saveUserRole(String data){
		JSONObject params=JSON.parseObject(data);
		return userService.saveRoleUser(params);
	}

	@PutMapping
	@ApiOperation("更新用户")
	@PreAuthorize("hasAuthority('sys_user_update')")
	public ResultVo update(String data) {
		SysUser sysUser= JSON.toJavaObject(JSON.parseObject(data),SysUser.class);
		return userService.update(sysUser);
	}

	@ApiOperation("修改用户密码")
	@PostMapping("/changePwd")
	public ResultVo changePassword(String data, HttpServletRequest request) {
		JSONObject params=JSON.parseObject(data);
		LoginUser loginUser= tokenService.getLoginUser(request.getHeader("token"));
		return userService.changePassword(loginUser, params.getString("oldPwd"), params.getString("newPwd"));
	}
	@ApiOperation("获取用户信息")
	@GetMapping("/info")
	public ResultVo info(HttpServletRequest request) {
		return new ResultVo(ResultCode.SUCCESS,tokenService.getLoginUser(request.getHeader("token")));
	}

	@ApiOperation("批量删除用户")
	@DeleteMapping
	@PreAuthorize("hasAuthority('sys_user_delete')")
	public ResultVo delUsersByIds(String data){
		JSONObject jo=JSONObject.parseObject(data);
		List<String> userIds= JSON.parseArray(jo.getString("ids"),String.class);
		return userService.delUsersByIds(userIds);

	}

	@ApiOperation("批量导入用户")
	@PostMapping("/batch")
	@PreAuthorize("hasAuthority('sys_user_add')")
	public ResultVo batchUsers(MultipartFile file){
		try {
			// 获取上传的文件
			if (file.isEmpty()) {
				try {
					throw new Exception("文件不存在！");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			InputStream in = null;
			try {
				in = file.getInputStream();
			} catch (IOException e) {
				return new ResultVo(ResultCode.FAIL);
			}
			// 数据导入
			ExcelReader reader = ExcelUtil.getReader(in);
			List<List<Object>> listOb = reader.read();
			//删除表头
			listOb.remove(0);
			SysUser sysUser;
			for (List<Object> objects : listOb) {
				sysUser=new SysUser();
				sysUser.setUsername(String.valueOf(objects.get(0)));
				sysUser.setNickname(String.valueOf(objects.get(1)));
				sysUser.setEmail(String.valueOf(objects.get(2)));
				sysUser.setBirthday(String.valueOf(objects.get(3)));
				sysUser.setPhone(String.valueOf(objects.get(4)));
				sysUser.setTelephone(String.valueOf(objects.get(5)));
				sysUser.setSex(String.valueOf(objects.get(6)).equals("男")?0:1);
				userService.save(sysUser);
			}
			in.close();
			return new ResultVo(ResultCode.SUCCESS);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new ResultVo(ResultCode.FAIL);
	}
}
