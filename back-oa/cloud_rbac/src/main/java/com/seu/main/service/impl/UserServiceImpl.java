package com.seu.main.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seu.main.mapper.UserMapper;
import com.seu.main.dto.LoginUser;
import com.seu.main.mapper.po.UserRoleMapper;
import com.seu.main.service.UserService;
import com.seu.util.entity.SysUser;
import com.seu.util.entity.po.UserRolePo;
import com.seu.util.entity.vo.PageResultVo;
import com.seu.util.entity.vo.ResultVo;
import com.seu.util.entity.vo.UserVo;
import com.seu.util.sysEnum.ResultCode;
import com.seu.util.sysEnum.UserStatusEnum;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, SysUser> implements UserService {

	@Resource
	private UserMapper userMapper;
	@Resource
	UserRoleMapper userRoleMapper;
	@Resource
	private BCryptPasswordEncoder passwordEncoder;

	@Override
	public ResultVo pageListUsers(Map<String, Object> params, Page<UserVo> page){
			page=userMapper.pageListUsers(params,page);
			return new ResultVo(ResultCode.SUCCESS,new PageResultVo<UserVo>(page.getTotal(),page.getSize(),page.getPages(),page.getRecords()));
	}

	@Override
	public ResultVo saveU(SysUser sysUser) {
		//passwordEncoder密码加密
		sysUser.setPassword(passwordEncoder.encode("123456"));
		//状态可用
		sysUser.setStatus(UserStatusEnum.VALID.getValue());
		sysUser.setCreateTime(DateUtil.date());
		sysUser.setUpdateTime(DateUtil.date());
		return userMapper.insert(sysUser)>0?new ResultVo(ResultCode.SUCCESS):new ResultVo(ResultCode.FAIL);
	}
	@Override
	public ResultVo update(SysUser sysUser) {
		//更新时间
		sysUser.setUpdateTime(DateUtil.date());
		//设为null  不更新
		sysUser.setUsername(null);
		sysUser.setPassword(null);
		return userMapper.updateById(sysUser)>0?new ResultVo(ResultCode.SUCCESS):new ResultVo(ResultCode.FAIL);
	}
	@Override
	public SysUser getByUserName(String username) {
		//条件构造器
		QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>() ;
		queryWrapper.eq("username",username);
		return userMapper.selectOne(queryWrapper);
	}
	@Override
	public ResultVo changePassword(LoginUser u, String oldPassword, String newPassword) {
		//旧密码匹配
		if (!passwordEncoder.matches(oldPassword, u.getPassword())) {
			return new ResultVo(ResultCode.PASSWORD_ERROR);
		}
		//生成新密码
		return userMapper.changePassword(u.getId(), passwordEncoder.encode(newPassword))
				? new ResultVo(ResultCode.SUCCESS) : new ResultVo(ResultCode.FAIL);
	}
	@Override
	public ResultVo delUsersByIds(List<String> ids) {
		if (userMapper.deleteBatchIds(ids)>0){
			//条件构造map
			Map<String,Object> params=new HashMap<>();
			for (String id : ids) {
				params.put("userId",id);
				//删除用户角色关系
				userRoleMapper.deleteByMap(params);
			}
			return new ResultVo(ResultCode.SUCCESS);
		}
		return new ResultVo(ResultCode.FAIL);
	}
	@Override
	public ResultVo saveRoleUser(Map<String, Object> params) {
		//po
		UserRolePo userRolePo=new UserRolePo();
		userRolePo.setUserId(params.get("userId").toString());
		userRolePo.setRoleId(params.get("roleId").toString());
		params.remove("roleId");
		//删除用户角色关系
		userRoleMapper.deleteByMap(params);
		//新增用户角色关系
		if (userRoleMapper.insert(userRolePo)>0){
			return new ResultVo(ResultCode.SUCCESS);
		}
		return new ResultVo(ResultCode.FAIL);
	}
}
