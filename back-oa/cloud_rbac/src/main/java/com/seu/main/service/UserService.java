package com.seu.main.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seu.main.dto.LoginUser;
import com.seu.util.entity.SysUser;
import com.seu.util.entity.vo.ResultVo;
import com.seu.util.entity.vo.UserVo;

import java.util.List;
import java.util.Map;

/**
 * @author Ajie
 * @version 1.0
 * @date 2020/1/15 10:22
 * @description
 */

public interface UserService extends IService<SysUser> {
    ResultVo saveU(SysUser sysUser);

    ResultVo update(SysUser sysUser);

    SysUser getByUserName(String username);

    ResultVo pageListUsers(Map<String, Object> params, Page<UserVo> page);

    ResultVo changePassword(LoginUser loginUser, String oldPassword, String newPassword);

    ResultVo delUsersByIds(List<String> ids);

    ResultVo saveRoleUser(Map<String, Object> params);
}
