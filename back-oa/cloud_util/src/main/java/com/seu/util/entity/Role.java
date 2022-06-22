package com.seu.util.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_role")
public class Role implements Serializable {

	private static final long serialVersionUID = -3802292814767103648L;
	/**
	 * id
	 */
	@TableId(type = IdType.ASSIGN_UUID)
	private String id;
	/**
	 * 角色名
	 */
	@TableField(updateStrategy = FieldStrategy.NOT_NULL)
	private String name;
	/**
	 * 描述
	 */
	@TableField(updateStrategy = FieldStrategy.NOT_NULL,insertStrategy = FieldStrategy.IGNORED)
	private String description;

	@TableField(updateStrategy = FieldStrategy.NOT_NULL)
	private String rolecode;
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
