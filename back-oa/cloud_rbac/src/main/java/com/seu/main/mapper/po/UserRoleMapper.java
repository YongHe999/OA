package com.seu.main.mapper.po;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seu.util.entity.po.UserRolePo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Ajie
 * @version 1.0
 * @date 2020/6/12 11:08
 * @description 用户角色关系mapper
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRolePo> {
}
