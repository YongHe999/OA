package com.seu.main.controller;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seu.main.service.SysDepartmentService;
import com.seu.main.service.SysUserDepartmentService;
import com.seu.main.service.UserService;
import com.seu.util.entity.SysDepartment;
import com.seu.util.entity.SysUser;
import com.seu.util.entity.SysUserDepartment;
import com.seu.util.entity.vo.ResultVo;
import com.seu.util.sysEnum.ResultCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 阿杰
 * @since 2021-09-13
 */
@RestController
@Api(tags = "部门相关")
@RequestMapping("/department")
public class SysDepartmentController {

    @Resource
    private SysDepartmentService sysDepartmentService;

    @Resource
    private SysUserDepartmentService sysUserDepartmentService;
    @Resource
    private UserService userService;

    @ApiOperation("部门列表")
    @GetMapping("/departmentList")
    public List<SysDepartment> departmentList(){
        return sysDepartmentService.list();
    }

    @ApiOperation("部门树")
    @GetMapping("/departmentTree")
    public List<Tree<String>> departmentTreeList(){
        return departmentTree(sysDepartmentService.list());
    }

    @ApiOperation("用户部门关系列表")
    @GetMapping("/userDepartment")
    public List<SysUserDepartment> userDepartments(){
        return sysUserDepartmentService.list();
    }

    @ApiOperation("根据部门id得到用户")
    @GetMapping("/getUserByDepartmentId")
    public List<SysUser> getUserByDepartmentId(String data){
        Map<String, Object> DepartmentIdMap= JSON.parseObject(data);
        List<SysUserDepartment> sysUserDepartmentList = sysUserDepartmentService.listByMap(DepartmentIdMap);
        List<String> sysUserList = new ArrayList<>();
        for (SysUserDepartment sysUserDepartment : sysUserDepartmentList) {
            sysUserList.add(sysUserDepartment.getUserid());
        }
        if (sysUserList.size()<1) {
            return new ArrayList<>();
        }
        return userService.listByIds(sysUserList);
    }
    @ApiOperation("根据用户得到部门")
    @GetMapping("/getDepartmentByUserId")
    public List<SysDepartment> getDepartmentByUserId(String data){
        Map<String, Object> userId= JSON.parseObject(data);
        SysUserDepartment userDepartmentList = sysUserDepartmentService.listByMap(userId).get(0);
        QueryWrapper<SysDepartment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",userDepartmentList.getDepartmentid());
        return sysDepartmentService.list(queryWrapper);
    }

    @PostMapping
    @ApiOperation("添加部门")
    public ResultVo addDepartments(String data){
        SysDepartment sysDepartment = JSON.toJavaObject(JSON.parseObject(data),SysDepartment.class);
        return sysDepartmentService.save(sysDepartment)? new ResultVo(ResultCode.SUCCESS):new ResultVo(ResultCode.FAIL);
    }
    @PostMapping("/update")
    @ApiOperation("修改部门")
    public ResultVo updateDepartments(String data){
        SysDepartment sysDepartment = JSON.toJavaObject(JSON.parseObject(data),SysDepartment.class);
        return sysDepartmentService.updateById(sysDepartment)? new ResultVo(ResultCode.SUCCESS):new ResultVo(ResultCode.FAIL);
    }
    @PutMapping
    @ApiOperation("更新用户部门关系")
    public ResultVo updateUserDepartments(String data){
        Map<String, Object> jo= JSON.parseObject(data);
        if (jo.get("departmentId").toString().equals("")){
            return new ResultVo(ResultCode.NULLFAIL);
        }
        SysUserDepartment sysUserDepartment = new SysUserDepartment();
        sysUserDepartment.setDepartmentid(jo.get("departmentId").toString());
        sysUserDepartment.setUserid(jo.get("userid").toString());
        sysUserDepartment.setNickname(jo.get("Nickname").toString());

        jo.remove("departmentId");
        jo.remove("Nickname");
        sysUserDepartmentService.removeByMap(jo);
        return sysUserDepartmentService.save(sysUserDepartment)? new ResultVo(ResultCode.SUCCESS):new ResultVo(ResultCode.FAIL);
    }


    public List<Tree<String>> departmentTree(List<SysDepartment> sysDepartmentList){
        if (sysDepartmentList==null||sysDepartmentList.isEmpty()){
            return null;
        }
        // 构建node列表
        List<TreeNode<String>> nodeList = CollUtil.newArrayList();
        //扩展字段
//        Map<String,Object> nodeExtra;
        for (SysDepartment sysdepartment : sysDepartmentList) {
//            nodeExtra=new HashMap<>();
//            //数据库扩展字段
            nodeList.add(new TreeNode<String>(sysdepartment.getId(),sysdepartment.getParentid(),sysdepartment.getDepartmentname(),1));
                    //添加扩展字段
//                    .setExtra(nodeExtra));
        }
        //构造器
        return TreeUtil.build(nodeList, "0");
    }
}

