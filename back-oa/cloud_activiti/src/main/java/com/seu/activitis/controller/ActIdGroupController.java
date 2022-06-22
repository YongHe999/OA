package com.seu.activitis.controller;

import com.alibaba.fastjson.JSON;
import com.seu.util.entity.vo.PageResultVo;
import com.seu.util.entity.vo.ResultVo;
import com.seu.util.sysEnum.ResultCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.GroupQuery;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "流程用户")
@RestController
public class ActIdGroupController {

    @Resource
    private IdentityService identityService;

    /**
     * 查询流程用户组列表
     */
    @ApiOperation(value = "用户组列表")
    @GetMapping("/group")
    public ResultVo groupList(String data) {
        //封装查询参数
        Map<String, Object> group= JSON.parseObject(data);
        if (group == null){
            group = new HashMap<>();
        }
        if (group.isEmpty()){
            group.put("name","");
            group.put("limit",10);
            group.put("page",1);
        }
        long size = (int)group.get("limit");
        long pages = (int)group.get("page");
        GroupQuery groupQuery = identityService.createGroupQuery();
        groupQuery.groupNameLike("%" + group.get("name") + "%");
        List<Group> groupList = groupQuery.listPage(((int)group.get("page")-1) * (int)group.get("limit"), (int)group.get("limit"));

        // ============= 对上面查询结果进行封装 ==================
        List<Map<String, Object>> pdResult = new ArrayList<>();
        for (Group grog : groupList) {
            Map<String, Object> pdMap = new HashMap<>();
            pdMap.put("id", grog.getId());
            pdMap.put("name", grog.getName());
            pdMap.put("type", grog.getType());
            pdResult.add(pdMap);
        }
        //反回分页数据
        return new ResultVo(ResultCode.SUCCESS,new PageResultVo<>(groupQuery.count(),size,pages,pdResult));
    }
    /**
     * 查询流程用户列表
     */
    @GetMapping("/actUserList")
    @ApiOperation(value = "用户列表")
    public ResultVo actUserList(String data) {
        //封装查询参数
        Map<String, Object> actUserMap = JSON.parseObject(data);
        if (actUserMap == null){
            actUserMap = new HashMap<>();
        }
        if (actUserMap.isEmpty()){
            actUserMap.put("name","");
            actUserMap.put("email","");
            actUserMap.put("limit",10);
            actUserMap.put("page",1);
        }
        long size = (int)actUserMap.get("limit");
        long pages = (int)actUserMap.get("page");
        UserQuery userQuery = identityService.createUserQuery();

        userQuery.userFirstNameLike("%" + actUserMap.get("name") + "%");
        userQuery.userEmailLike("%" + actUserMap.get("email") + "%");

        List<User> userList = userQuery.listPage(((int)actUserMap.get("page")-1) * (int)actUserMap.get("limit"), (int)actUserMap.get("limit"));
        // ============= 对上面查询结果进行封装 ==================
        List<Map<String, Object>> Result = new ArrayList<>();
        for (User grog : userList) {
            Map<String, Object> pdMap = new HashMap<>();
            pdMap.put("id", grog.getId());
            pdMap.put("email", grog.getEmail());
            pdMap.put("FirstName", grog.getFirstName());
            Result.add(pdMap);
        }
        //反回分页数据
        return new ResultVo(ResultCode.SUCCESS,new PageResultVo<>(userQuery.count(),size,pages,Result));
    }

}
