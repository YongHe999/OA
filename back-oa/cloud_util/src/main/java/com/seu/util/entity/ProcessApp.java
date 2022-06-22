package com.seu.util.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
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
 * @since 2021-08-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("process_app")
@ApiModel(value="ProcessApp对象", description="")
public class ProcessApp implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ID_WORKER)
    private String id;

    @TableField("definition_id")
    private String definitionId;

    @TableField("form_id")
    private String formId;

//    @TableField("color")
//    private String color;

    @TableField("definition_name")
    private String definitionName;

//    @TableField("icon")
//    private String icon;

    @TableField("form_name")
    private String formName;

    @TableField("definition_key")
    private String definitionKey;

    @TableField("name")
    private String name;

    @TableField("suspendState")
    private String suspendState;

    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField("version")
    private String version;


}
