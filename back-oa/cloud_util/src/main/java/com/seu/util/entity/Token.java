package com.seu.util.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("sys_token")
public class Token implements Serializable {

	private static final long serialVersionUID = 4566334160572911795L;
	/**
	 * id
	 */
	@TableId
	private String id;
	/**
	 * 过期时间
	 */
	@TableField(updateStrategy = FieldStrategy.NOT_NULL)
	private Date expireTime;
	/**
	 * LoginUser的json串
	 */
	@TableField(updateStrategy = FieldStrategy.NOT_NULL)
	private String val;
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
