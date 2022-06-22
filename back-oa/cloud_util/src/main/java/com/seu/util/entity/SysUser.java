package com.seu.util.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_user")
public class SysUser implements Serializable {

	private static final long serialVersionUID = -6525908145032868837L;
	/**
	 * id
	 */
	@TableId(type = IdType.ASSIGN_UUID)
	private String id;
	/**
	 * 用户名
	 */
	@TableField(updateStrategy = FieldStrategy.NOT_NULL)
	private String username;
	/**
	 * 密码
	 */
	@TableField(updateStrategy = FieldStrategy.NOT_NULL)
	private String password;
	/***
	 * 姓名
	 */
	@TableField(updateStrategy = FieldStrategy.NOT_NULL)
	private String nickname;
	/***
	 * 电话
	 */
	@TableField(updateStrategy = FieldStrategy.NOT_NULL,insertStrategy = FieldStrategy.IGNORED)
	private String phone;
	/**
	 * 座机
	 */
	@TableField(updateStrategy = FieldStrategy.NOT_NULL,insertStrategy = FieldStrategy.IGNORED)
	private String telephone;
	/***
	 * 邮箱
	 */
	@TableField(updateStrategy = FieldStrategy.NOT_NULL,insertStrategy = FieldStrategy.IGNORED)
	private String email;
	/**
	 * 生日
	 */
	@TableField(updateStrategy = FieldStrategy.NOT_NULL)
	private String birthday;
	/**
	 * 性别
	 */
	@TableField(updateStrategy = FieldStrategy.NOT_NULL)
	private int sex;
	/**
	 * 状态
	 */
	@TableField(updateStrategy = FieldStrategy.NOT_NULL)
	private int status;

	// 类型
	@TableField("type")
	private String type;

	 //头像地址
	@TableField("img")
	private String img;
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
