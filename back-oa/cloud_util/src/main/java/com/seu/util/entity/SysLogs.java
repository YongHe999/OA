package com.seu.util.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Ajie
 * @version 1.0
 * @date 2020/2/28 15:21
 * @description
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_logs")
public class SysLogs implements Serializable {

    private static final long serialVersionUID = 6412180164812494598L;
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    /**
     * 用户名称
     */
    @TableField(insertStrategy = FieldStrategy.IGNORED)
    private String nickName;
    /**
     * 模块/操作
     */
    private String module;
    /**
     * 成功失败标志
     */
    private int flag;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建时间
     */
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 更新时间
     */
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
