package com.seu.util.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author 阿杰
 * @since 2021-08-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("process_form")
@ApiModel(value="ProcessForm对象", description="")
public class ProcessForm implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "表单id")
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @ApiModelProperty(value = "表单名称")
    @TableField("formname")
    private String formname;

    @ApiModelProperty(value = "表单内容")
    @TableField("form")
    private String form;

    @TableField("version")
    private String version;

    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;


}
