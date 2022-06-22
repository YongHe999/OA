package com.seu.main.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seu.util.entity.SysUser;
import com.seu.util.entity.vo.UserVo;
import org.apache.ibatis.annotations.*;

import java.util.Map;

@Mapper
public interface UserMapper extends BaseMapper<SysUser> {

	/**
	 * 分页列表
	 * @param params
	 * @param page
	 * @return
	 */

	Page<UserVo> pageListUsers(@Param("params") Map<String, Object> params, Page<UserVo> page);

	/**
	 * 修改密码
	 * @param id
	 * @param password
	 * @return
	 */
	@Update("update sys_user t set t.password = #{password} where t.id = #{id}")
	Boolean changePassword(@Param("id") String id, @Param("password") String password);


}
