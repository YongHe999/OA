package com.seu.main.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seu.util.entity.Role;
import com.seu.util.entity.vo.ResultVo;


import java.util.List;

public interface RoleService {

	ResultVo save(Role role);

	ResultVo update(Role role);

	ResultVo saveRolePermission(String roleId, List<String> permissionIds);

	ResultVo pageListRoles(JSONObject params, Page<Role> page);

	ResultVo listRoles();

	ResultVo delRolesByIds(List<String> roleIds);
}
