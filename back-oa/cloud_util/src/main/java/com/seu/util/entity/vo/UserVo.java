package com.seu.util.entity.vo;

import com.seu.util.entity.SysUser;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author Ajie
 * @version 1.0
 * @date 2020/6/16 09:15
 * @description
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserVo extends SysUser implements Serializable {
    private static final long serialVersionUID = 8853822248151903436L;
    /**
     * 角色id
     */
    private String roleId;
    private String roleName;
    private String rolecode;
    private String departmentId;
}
