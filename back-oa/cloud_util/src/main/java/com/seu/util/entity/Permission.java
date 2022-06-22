package com.seu.util.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_permission")
public class Permission implements Serializable {

	private static final long serialVersionUID = 6180869216498363919L;

	/**
	 * id
	 */
	@TableId(type = IdType.ASSIGN_UUID)
	private String id;
	/**
	 * 父id
	 */
	@TableField(updateStrategy = FieldStrategy.NOT_NULL)
	private String parentId;
	/**
	 * 权限名称
	 */
	@TableField(updateStrategy = FieldStrategy.NOT_NULL)
	private String title;
	/**
	 * 权限路径
	 */
	@TableField(updateStrategy = FieldStrategy.NOT_NULL)
	private String path;

	/**
	 * 资源路径
	 */
	@TableField(updateStrategy = FieldStrategy.NOT_NULL)
	private String resourcepath;
	/**
	 * 菜单样式
	 */
	@TableField(updateStrategy = FieldStrategy.NOT_NULL,insertStrategy = FieldStrategy.IGNORED)
	private String icon;
	/**
	 * 类型 1菜单 2按钮
	 */
	@TableField(updateStrategy = FieldStrategy.NOT_NULL)
	private Integer type;
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
