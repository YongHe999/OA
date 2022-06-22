package com.seu.main.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.seu.main.service.RoleService;
import com.seu.util.entity.Role;
import com.seu.util.entity.vo.ResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色相关接口
 * 
 * @author
 *
 */
@RestController
@Api(tags = "角色相关接口")
@RequestMapping("/role")
public class RoleController {

	@Autowired
	private RoleService roleService;

	@GetMapping
	@ApiOperation("获取角色分页列表")
	public ResultVo pageListRoles(String data){
		JSONObject params=JSON.parseObject(data);
		return roleService.pageListRoles(params,new Page<Role>((int) params.get("page"), (int) params.get("limit"),true));
	}

	@ApiOperation("新增角色")
	@PostMapping
	@PreAuthorize("hasAuthority('sys_role_add')")
	public ResultVo save(String data) {
		Role role= JSON.toJavaObject(JSON.parseObject(data),Role.class);
		return roleService.save(role);
	}

	@ApiOperation("更新角色")
	@PutMapping
	@PreAuthorize("hasAuthority('sys_role_update')")
	public ResultVo update(String data) {
		Role role= JSON.toJavaObject(JSON.parseObject(data),Role.class);
		return roleService.update(role);
	}

	@PostMapping("/permission")
	@ApiOperation("保存角色权限关系")
	@PreAuthorize("hasAuthority('sys_role_permission')")
	public ResultVo saveRolePermission(String data) {
		JSONObject params=JSON.parseObject(data);
		List<String> permissionIds= JSON.parseArray(params.getString("permissionIds"),String.class);
		return roleService.saveRolePermission(params.getString("roleId"),permissionIds);
	}

	@ApiOperation("批量删除角色")
	@DeleteMapping
	@PreAuthorize("hasAuthority('sys_role_delete')")
	public ResultVo delRolesByIds(String data){
		JSONObject jo=JSONObject.parseObject(data);
		List<String> roleIds= JSON.parseArray(jo.getString("ids"),String.class);
		return roleService.delRolesByIds(roleIds);
	}


	@ApiOperation("获取所有角色信息（下拉框）")
	@GetMapping("/list")
	public ResultVo listRoles() {
		return roleService.listRoles();
	}
}
