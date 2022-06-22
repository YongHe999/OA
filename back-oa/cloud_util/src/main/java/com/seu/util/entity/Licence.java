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
 * @date 2020/3/12 17:20
 * @description
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_licence")
public class Licence implements Serializable {

    private static final long serialVersionUID = -1249093027482981000L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    /**
     * 许可证编码
     */
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    String licence;
    /**
     * 创建时间
     */
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    private Date createTime;
    /**
     * 更新时间
     */
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    private Date updateTime;
}
