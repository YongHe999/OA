package com.seu.main.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.seu.main.mapper.PermissionMapper;
import com.seu.main.mapper.po.RolePermissionMapper;
import com.seu.main.service.PermissionService;
import com.seu.util.entity.Permission;
import com.seu.util.entity.po.RolePermissionPo;
import com.seu.util.entity.vo.ResultVo;
import com.seu.util.sysEnum.ResultCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author li.yinghao
 */
@Service
public class PermissionServiceImpl implements PermissionService {

	@Autowired
	private PermissionMapper permissionMapper;
	@Autowired
	private RolePermissionMapper rolePermissionMapper;

	/**
	 * 新增权限
	 * @param permission
	 * @return
	 */

	@Transactional
	@Override
	public ResultVo save(Permission permission) {
		permission.setCreateTime(DateUtil.date());
		permission.setUpdateTime(DateUtil.date());
		return permissionMapper.insert(permission)>0?new ResultVo(ResultCode.SUCCESS,permission): new ResultVo(ResultCode.FAIL);
	}

	/**
	 * 更新权限
	 * @param permission
	 * @return
	 */

	@Transactional
	@Override
	public ResultVo update(Permission permission) {
		permission.setUpdateTime(DateUtil.date());
		return permissionMapper.updateById(permission)>0?new ResultVo(ResultCode.SUCCESS,permission): new ResultVo(ResultCode.FAIL);
	}

	/**
	 * 删除权限
	 * @param permission
	 * @return
	 */

	@Override
	@Transactional
	public ResultVo delete(Permission permission) {
		//先查询是否有子节点
		QueryWrapper<Permission> queryWrapper=new QueryWrapper();
		queryWrapper.eq("parentId",permission.getId());
		//无子节点 可删除
		if (permissionMapper.selectList(queryWrapper).isEmpty()){
			//删除权限
			if (permissionMapper.deleteById(permission.getId())>0){
				//删除角色权限关系
				QueryWrapper<RolePermissionPo> poQueryWrapper=new QueryWrapper<>();
				poQueryWrapper.eq("permissionId",permission.getId());
				rolePermissionMapper.delete(poQueryWrapper);
				return new ResultVo(ResultCode.SUCCESS);
			}
			return new ResultVo(ResultCode.FAIL);
		}
		return new ResultVo(ResultCode.HAVINGCHILD);
	}

	/**
	 * 获取角色相关权限
	 * @param params
	 * @return
	 */

	@Cacheable(value = "Permission", key = "'listByRoleId'")
	@Override
	public List<Permission> listByRoleId(JSONObject params) {

		System.out.println("*****************************************listByRoleId");
		//条件构造器：获取角色权限关系po
		QueryWrapper<RolePermissionPo> rolePermissionPoQueryWrapper=new QueryWrapper<>();
		rolePermissionPoQueryWrapper.eq("roleId",params.getString("roleId"));
		List<RolePermissionPo> rolePermissionPoList=rolePermissionMapper.selectList(rolePermissionPoQueryWrapper);
		//为空时返回null
		if (rolePermissionPoList.isEmpty()){
			return null;
		}
		//根据权限id获取权限数组
		return permissionMapper.selectBatchIds(rolePermissionPoList
				//过滤角色权限po 只取权限id
				.stream().map(RolePermissionPo::getPermissionId).distinct().collect(Collectors.toList()));
	}

	/**
	 * 权限列表
	 * @param params
	 * @return
	 */

	@Override
	public List<Permission> listAll(JSONObject params) {
		//获取参数
		String title=params.getString("title");
		QueryWrapper<Permission> queryWrapper=new QueryWrapper<>();
		if (StringUtils.isNotBlank(title)){
			queryWrapper.like("title",params.get("title").toString());
		}else {
			queryWrapper=null;
		}
		return permissionMapper.selectList(queryWrapper);
	}

}
