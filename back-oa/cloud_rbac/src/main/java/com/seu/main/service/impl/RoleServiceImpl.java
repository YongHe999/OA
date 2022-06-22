package com.seu.main.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.seu.main.mapper.RoleMapper;
import com.seu.main.mapper.po.RolePermissionMapper;
import com.seu.main.mapper.po.UserRoleMapper;
import com.seu.main.service.RoleService;
import com.seu.util.entity.Role;
import com.seu.util.entity.po.RolePermissionPo;
import com.seu.util.entity.vo.PageResultVo;
import com.seu.util.entity.vo.ResultVo;
import com.seu.util.sysEnum.ResultCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ajie
 */
@Service
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleMapper roleMapper;
	@Autowired
	private RolePermissionMapper rolePermissionMapper;
	@Autowired
	private UserRoleMapper userRoleMapper;

	/**
	 * 获取角色分页列表
	 * @param params
	 * @param page
	 * @return
	 */
	@Override
	public ResultVo pageListRoles(JSONObject params, Page<Role> page) {
		//获取查询条件
		String name=params.getString("name");
		//条件构造器
		QueryWrapper<Role> queryWrapper=new QueryWrapper<>();
		if (StringUtils.isNotBlank(name)){
			queryWrapper.like("name",name);
		}
		roleMapper.selectPage(page,queryWrapper);
		return new ResultVo(ResultCode.SUCCESS,new PageResultVo<>(page.getTotal(),page.getSize(),page.getPages(),page.getRecords()));
	}

	/**
	 * 新增角色
	 * @param role
	 * @return
	 */
	@Override
	@Transactional
	public ResultVo save(Role role) {
		//条件构造器
		QueryWrapper<Role> queryWrapper=new QueryWrapper<>();
		queryWrapper.eq("name",role.getName());
		//角色名是否被使用
		Role r = roleMapper.selectOne(queryWrapper);
		if (r != null) {
			return new ResultVo(ResultCode.EXIT);
		}
		role.setCreateTime(DateUtil.date());
		role.setUpdateTime(DateUtil.date());
		return roleMapper.insert(role)>0? new ResultVo(ResultCode.SUCCESS):new ResultVo(ResultCode.FAIL);
	}

	/**
	 * 修改角色
	 * @param role
	 * @return
	 */
	@Transactional
	@Override
	public ResultVo update(Role role) {
		//只改描述
//		role.setName(null);
		role.setUpdateTime(DateUtil.date());
		return roleMapper.updateById(role)>0?new ResultVo(ResultCode.SUCCESS):new ResultVo(ResultCode.FAIL);
	}

	/**
	 * 保存角色权限关系
	 * @param roleId
	 * @param permissionIds
	 * @return
	 */

	@Caching(evict={@CacheEvict(value = "Permission",allEntries=true),
			@CacheEvict(value = "UserDetails",allEntries=true)})
	@Transactional
	@Override
	public ResultVo saveRolePermission(String roleId, List<String> permissionIds) {

		//条件构造器
		QueryWrapper<RolePermissionPo> queryWrapper=new QueryWrapper<>();
		queryWrapper.eq("roleId",roleId);
		//先清空 角色权限 的关系
		rolePermissionMapper.delete(queryWrapper);
		//循环保存数据
		RolePermissionPo rolePermissionPo;
		for (String permissionId : permissionIds) {
			//创建对象
			rolePermissionPo = new RolePermissionPo();
			//po  填入角色id
			rolePermissionPo.setRoleId(roleId);
			//填入权限id
			rolePermissionPo.setPermissionId(permissionId);
			if (!(rolePermissionMapper.insert(rolePermissionPo)>0)){
				return new ResultVo(ResultCode.FAIL);
			}
		}
		return new ResultVo(ResultCode.SUCCESS);

	}

	/**
	 * 获取数组
	 * @return
	 */
	@Override
	public ResultVo listRoles() {
		return new ResultVo(ResultCode.SUCCESS, roleMapper.selectList(null));
	}

	/**
	 * 删除角色及相关联系
	 * @param roleIds
	 * @return
	 */
	@Override
	@Transactional
	public ResultVo delRolesByIds(List<String> roleIds) {
		//删除角色
		if (roleMapper.deleteBatchIds(roleIds)>0){
			Map<String,Object> params;
			for (String roleId : roleIds) {
				params=new HashMap<>();
				params.put("roleId",roleId);
				//删除用户角色关系
				userRoleMapper.deleteByMap(params);
				//删除角色权限关系
				rolePermissionMapper.deleteByMap(params);
			}
			return new ResultVo(ResultCode.SUCCESS);
		}
		return new ResultVo(ResultCode.FAIL);
	}
}
