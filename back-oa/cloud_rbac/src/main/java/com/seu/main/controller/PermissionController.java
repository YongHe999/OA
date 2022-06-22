package com.seu.main.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.seu.main.dto.LoginUser;

import com.seu.main.service.PermissionService;
import com.seu.main.service.TokenService;
import com.seu.util.entity.Permission;
import com.seu.util.entity.vo.ResultVo;
import com.seu.util.sysEnum.PermissionTypeEnum;
import com.seu.util.sysEnum.ResultCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 权限相关接口
 * 
 * @author Ajie z-ajie@qq.com
 *
 */
@Api(tags = "权限相关接口")
@RestController
@RequestMapping("/permission")
public class PermissionController {
	@Autowired
	private PermissionService permissionService;
	@Autowired
	private TokenService tokenService;

	@GetMapping
	@ApiOperation("权限列表")
	public ResultVo list(){
		JSONObject params=new JSONObject();
		params.put("title","");
		return new ResultVo(ResultCode.SUCCESS,permissionService.listAll(params)
				.stream().filter(p -> p.getType().equals(PermissionTypeEnum.MENU.getValue())).collect(Collectors.toList()));
	}

	@PostMapping
	@ApiOperation("新增权限")
	@PreAuthorize("hasAuthority('sys_permission_add')")
	public ResultVo save(String data) {
		Permission permission= JSON.toJavaObject(JSON.parseObject(data),Permission.class);
		return permissionService.save(permission);
	}

	@PutMapping
	@ApiOperation("修改权限")
	@PreAuthorize("hasAuthority('sys_permission_update')")
	public ResultVo update(String data) {
		Permission permission= JSON.toJavaObject(JSON.parseObject(data),Permission.class);
		return permissionService.update(permission);
	}

	@DeleteMapping
	@ApiOperation("删除权限")
	@PreAuthorize("hasAuthority('sys_permission_delete')")
	public ResultVo delete(String data) {
		Permission permission=JSON.toJavaObject(JSON.parseObject(data),Permission.class);
		return permissionService.delete(permission);
	}

	@GetMapping("tree")
	@ApiOperation("权限树")
	public ResultVo listPermissions(String data) {
		JSONObject params=JSON.parseObject(data);
		if (!params.containsKey("title")){
			params.put("title","");
		}
		List<Permission> permissionsAll = permissionService.listAll(params);
		return new ResultVo(ResultCode.SUCCESS,permissionTree(permissionsAll));
	}

	@GetMapping("/userTree")
	@ApiOperation("登录用户菜单树")
	public List<Tree<String>> treePermissions(HttpServletRequest request) {
		//获取登录用户的所有菜单
		LoginUser loginUser=tokenService.getLoginUser(request.getHeader("token"));
		//过滤数组只要菜单
		List<Permission> permissionsAll = loginUser.getPermissions()
				.stream().filter(p -> p.getType().equals(PermissionTypeEnum.MENU.getValue())).collect(Collectors.toList());
		//构造菜单树
		return permissionTree(permissionsAll);
	}

	@PostMapping("/rolePermissions")
	@ApiOperation("根据角色id获取权限")
	public ResultVo listByRoleId(String data) {
		JSONObject params=JSON.parseObject(data);
		return new ResultVo(ResultCode.SUCCESS,permissionTree(permissionService.listByRoleId(params)));
	}

	/**
	 * 菜单树构造
	 * @param permissionList
	 * @return
	 */
	private List<Tree<String>> permissionTree(List<Permission> permissionList){
		if (permissionList==null||permissionList.isEmpty()){
			return null;
		}
		// 构建node列表
		List<TreeNode<String>> nodeList = CollUtil.newArrayList();
		//扩展字段
		Map<String,Object> nodeExtra;
		for (Permission permission : permissionList) {
			nodeExtra=new HashMap<>();
			//数据库扩展字段path
			nodeExtra.put("path",permission.getPath());
			nodeExtra.put("resourcepath",permission.getResourcepath());
			//数据库扩展字段icon
			nodeExtra.put("icon",permission.getIcon());
			nodeExtra.put("title",permission.getTitle());
			nodeExtra.put("type",permission.getType());
			nodeList.add(new TreeNode<String>(permission.getId(),permission.getParentId(),permission.getTitle(),1)
					//添加扩展字段
					.setExtra(nodeExtra));
		}
		//构造器
		List<Tree<String>> treeNodes = TreeUtil.build(nodeList, "0");
		return treeNodes;
	}

}
