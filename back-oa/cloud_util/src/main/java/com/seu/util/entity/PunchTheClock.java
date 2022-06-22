package com.seu.util.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author 阿杰
 * @since 2021-09-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("punch_the_clock")
@ApiModel(value="PunchTheClock对象", description="")
public class PunchTheClock implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @TableField("userid")
    private String userid;

    @TableField("ontime")
    private Date ontime;

    @TableField("offtime")
    private Date offtime;

    @TableField("onpunch")
    private String onpunch;

    @TableField("offpunch")
    private String offpunch;
}
