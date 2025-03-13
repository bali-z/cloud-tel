package com.ruoyi.system.api;

import com.ruoyi.system.api.domain.SysDept;
import com.ruoyi.system.api.domain.SysRole;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.constant.ServiceNameConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.api.domain.SysUser;
import com.ruoyi.system.api.factory.RemoteUserFallbackFactory;
import com.ruoyi.system.api.model.LoginUser;

import java.util.List;

/**
 * 用户服务
 * 
 * @author ruoyi
 */
@FeignClient(contextId = "remoteUserService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteUserFallbackFactory.class)
public interface RemoteUserService
{
    /**
     * 通过角色id获取用户信息列表
     */
    @GetMapping("/user/listByRoleIds/{roleIds}")
    public R<List<SysUser>> listByRoleIds(@PathVariable("roleIds") List<Long> roleIds,@RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     *  通过部门id获取用户信息列表
     */
    @GetMapping("/user/listByDeptIds/{deptIds}")
    public R<List<SysUser>> listByDeptIds(@PathVariable("deptIds") List<Long> deptIds,@RequestHeader(SecurityConstants.FROM_SOURCE) String source);


    /**
     *  根据部门id获取部门详细信息
     */
    @GetMapping("/dept/getDeptByDeptId/{deptId}")
    public R<SysDept> getDeptByDeptId(@PathVariable("deptId") Long deptId,@RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     *  根据角色id获取角色详细信息
     * @param roleId
     * @param source
     * @return
     */
    @GetMapping("/role/getRoleByRoleId/{roleId}")
    public R<SysRole> getRoleByRoleId(@PathVariable("roleId") Long roleId, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     *  根据用户id获取用户详细信息
     * @param userId
     * @param source
     * @return
     */
    @GetMapping("/user/infoByUserId/{userId}")
    public R<SysUser> getUserInfoByUserId(@PathVariable("userId") Long userId, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);


    /**
     * 通过用户名查询用户信息
     *
     * @param username 用户名
     * @param source 请求来源
     * @return 结果
     */
    @GetMapping("/user/info/{username}")
    public R<LoginUser> getUserInfo(@PathVariable("username") String username, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 通过userId查询用户信息
     * @return 结果
     */
    @GetMapping("/user/userInfo/{userId}")
    public R<SysUser> getUserInfoById(@PathVariable("userId") Long userId, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);


    /**
     * 注册用户信息
     *
     * @param sysUser 用户信息
     * @param source 请求来源
     * @return 结果
     */
    @PostMapping("/user/register")
    public R<Boolean> registerUserInfo(@RequestBody SysUser sysUser, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
