package com.seu.main.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.seu.main.mapper.PermissionMapper;
import com.seu.main.dto.LoginUser;
import com.seu.main.mapper.RoleMapper;
import com.seu.main.mapper.po.RolePermissionMapper;
import com.seu.main.mapper.po.UserRoleMapper;
import com.seu.main.service.UserService;
import com.seu.util.entity.Permission;
import com.seu.util.entity.Role;
import com.seu.util.entity.SysUser;
import com.seu.util.entity.po.RolePermissionPo;
import com.seu.util.entity.po.UserRolePo;
import com.seu.util.sysEnum.UserStatusEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * spring security登陆处理<br>
 * @author Ajie z-ajie@qq.com
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserService userService;
	@Autowired
	private PermissionMapper permissionMapper;
	@Autowired
	private UserRoleMapper userRoleMapper;
	@Autowired
	private RolePermissionMapper rolePermissionMapper;
	@Autowired
	private RoleMapper roleMapper;

	/**
	 * 用户登录逻辑
	 * @param username
	 * @return
	 * @throws UsernameNotFoundException
	 */
	@Cacheable(value = "UserDetails", key = "'loadUserByUsername'")
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		//查询用户
		SysUser sysUser = userService.getByUserName(username);
		if (sysUser == null) {
			throw new AuthenticationCredentialsNotFoundException("用户名不存在");
		} else if (sysUser.getStatus() == UserStatusEnum.LOCKED.getValue()) {
			throw new LockedException("用户被锁定,请联系管理员");
		} else if (sysUser.getStatus() == UserStatusEnum.DISABLED.getValue()) {
			throw new DisabledException("用户已被禁用");
		}
		//用户dto
		LoginUser loginUser = new LoginUser();
		BeanUtils.copyProperties(sysUser, loginUser);
		//获取用户权限
		System.out.println("用户登录逻辑");
		loginUser.setPermissions(this.permissionList(sysUser));
		return loginUser;
	}

	/**
	 * 获取用户相关权限
	 * @param sysUser
	 * @return
	 */

	public List<Permission> permissionList(SysUser sysUser){


		System.out.println("获取用户相关权限");
		//查询用户角色
		//获取po
		QueryWrapper<UserRolePo> userRolePoQueryWrapper=new QueryWrapper<>();
		userRolePoQueryWrapper.eq("userId",sysUser.getId());
		UserRolePo userRolePo=userRoleMapper.selectOne(userRolePoQueryWrapper);
		if (userRolePo==null){
			return null;
		}
		//获取角色
		QueryWrapper<Role> roleQueryWrapper=new QueryWrapper<>();
		roleQueryWrapper.eq("id",userRolePo.getRoleId());
		Role role=roleMapper.selectOne(roleQueryWrapper);
		//查询角色权限
		//po
		QueryWrapper<RolePermissionPo> rolePermissionPoQueryWrapper=new QueryWrapper<>();
		rolePermissionPoQueryWrapper.eq("roleId",role.getId());
		List<RolePermissionPo> rolePermissionPoList=rolePermissionMapper.selectList(rolePermissionPoQueryWrapper);
		if (rolePermissionPoList.isEmpty()){
			return null;
		}
		//获取权限
		return permissionMapper.selectBatchIds(rolePermissionPoList
				.stream().map(RolePermissionPo::getPermissionId).distinct().collect(Collectors.toList()));
	}
}
