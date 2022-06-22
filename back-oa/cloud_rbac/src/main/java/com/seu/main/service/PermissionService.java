package com.seu.main.service;


import com.alibaba.fastjson.JSONObject;
import com.seu.util.entity.Permission;
import com.seu.util.entity.vo.ResultVo;


import java.util.List;

public interface PermissionService {

	ResultVo save(Permission permission);

	ResultVo update(Permission permission);

	ResultVo delete(Permission permission);

	List<Permission> listByRoleId(JSONObject params);

	List<Permission> listAll(JSONObject params);
}
