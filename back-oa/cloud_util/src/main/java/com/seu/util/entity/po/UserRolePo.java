package com.seu.util.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Ajie
 * @version 1.0
 * @date 2020/6/12 10:59
 * @description 用户角色关系po
 */
@Data
@TableName("sys_user_role")
public class UserRolePo implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 角色id
     */
    private String roleId;
}
