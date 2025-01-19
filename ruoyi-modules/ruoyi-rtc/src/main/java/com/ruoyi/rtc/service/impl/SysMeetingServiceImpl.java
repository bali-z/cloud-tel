package com.ruoyi.rtc.service.impl;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.JwtUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.redis.generator.SnowflakeIdGenerator;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.rtc.domain.SysMeeting;
import com.ruoyi.rtc.mapper.SysMeetingMapper;
import com.ruoyi.rtc.pojo.MeetingInfo;
import com.ruoyi.rtc.pojo.MeetingJoinForm;
import com.ruoyi.rtc.service.ISysMeetingService;
import com.ruoyi.system.api.model.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.ruoyi.rtc.pojo.Constants.*;

/**
 * 会议管理Service业务层处理
 *
 * @author dz
 * @date 2025-01-18
 */
@SuppressWarnings("ALL")
@Service
public class SysMeetingServiceImpl implements ISysMeetingService {
    @Autowired
    private SysMeetingMapper sysMeetingMapper;

    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;

    @Autowired
    private RedisService redisService;

    /**
     * 查询会议管理
     *
     * @param id 会议管理主键
     * @return 会议管理
     */
    @Override
    public SysMeeting selectSysMeetingById(Long id) {
        return sysMeetingMapper.selectSysMeetingById(id);
    }

    /**
     * 查询会议管理列表
     *
     * @param sysMeeting 会议管理
     * @return 会议管理
     */
    @Override
    public List<SysMeeting> selectSysMeetingList(SysMeeting sysMeeting) {
        return sysMeetingMapper.selectSysMeetingList(sysMeeting);
    }

    /**
     * 新增会议管理
     *
     * @param sysMeeting 会议管理
     * @return 结果
     */
    @Override
    public int insertSysMeeting(SysMeeting sysMeeting) {
        sysMeeting.setCreateTime(DateUtils.getNowDate());
        sysMeeting.setMeetingId(snowflakeIdGenerator.nextId());
        sysMeeting.setUserId(SecurityUtils.getUserId());
        return sysMeetingMapper.insertSysMeeting(sysMeeting);
    }

    /**
     * 修改会议管理
     *
     * @param sysMeeting 会议管理
     * @return 结果
     */
    @Override
    public int updateSysMeeting(SysMeeting sysMeeting) {
        return sysMeetingMapper.updateSysMeeting(sysMeeting);
    }

    /**
     * 批量删除会议管理
     *
     * @param ids 需要删除的会议管理主键
     * @return 结果
     */
    @Override
    public int deleteSysMeetingByIds(Long[] ids) {
        return sysMeetingMapper.deleteSysMeetingByIds(ids);
    }

    /**
     * 删除会议管理信息
     *
     * @param id 会议管理主键
     * @return 结果
     */
    @Override
    public int deleteSysMeetingById(Long id) {
        return sysMeetingMapper.deleteSysMeetingById(id);
    }


    /**
     *
     * @param form
     * @return
     */
    @Override
    public R joinMeeting(MeetingJoinForm form) {
        String meetingId = form.getMeetingId();
        String password = form.getPassword();
        // 查询会议信息
        SysMeeting meeting = getMeetingByMeetingId(meetingId);
        if (meeting == null) {
            return R.fail("会议不存在!");
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (meeting.getStartTime().getTime() > currentTimeMillis) {
            return R.fail("会议尚未开始!");
        }
        if (meeting.getEndTime().getTime() < currentTimeMillis) {
            return R.fail("会议已结束!");
        }
        LoginUser currentUser = SecurityUtils.getLoginUser();
        Long currentUserUserId = currentUser.getUserid();
        Map<String, Object> claims = new HashMap<>();
        claims.put(MEETING_TICKET_ITEM_MEETING_ID, meeting.getMeetingId());
        claims.put(MEETING_TICKET_ITEM_MEETING_OWNER_USER_ID, meeting.getUserId());
        claims.put(MEETING_TICKET_ITEM_MEETING_MEMBER_USER_ID, currentUserUserId);
        // 会议票据
        String ticket = JwtUtils.createToken(claims);
        // 当前请求用户是 会议发起人不需要校验密码
        if (currentUserUserId.equals(meeting.getUserId())) {
            cacheOnlineMeeting(meetingId);
            return R.ok("ticket", ticket);
        }
        // 不是会议发起人
        if (StringUtils.isEmpty(meeting.getPassword())) {
            // 会议没有设置密码,默认允许加入会议
            cacheOnlineMeeting(meetingId);
            return R.ok();
        }
        // 会议有密码，进入会议没有输入密码 || 密码不对
        if (StringUtils.isEmpty(password) || !password.equals(meeting.getPassword())) {
            return R.fail();
        } else {
            cacheOnlineMeeting(meetingId);
            return R.ok("ticket", ticket);
        }
    }


    /**
     *  只是把会议加入到在线会议中去了，相当于这个会议正在执行状态
     * @param meetingId
     */
    public void cacheOnlineMeeting(String meetingId) {
        // 查询会议信息
        SysMeeting meeting = getMeetingByMeetingId(meetingId);
        long currentTimeMillis = System.currentTimeMillis();
        // 已经结束的不缓存
        if (meeting == null || meeting.getStartTime().getTime() > currentTimeMillis || meeting.getEndTime().getTime() < currentTimeMillis) {
            return;
        }
        // 封装 MeetingInfo 信息
        MeetingInfo meetingInfo = new MeetingInfo();
        meetingInfo.setMeetingId(meetingId).setMeetingTitle(meeting.getTitle())
                .setMeetingOwnerUserId(meeting.getUserId()).setMeetingPassword(meeting.getPassword());
        String key = ONLINE_MEETING_PREFIX_KEY + meetingId;
        if (redisService.hasKey(key)) {
            return;
        }
        redisService.setCacheObject(key, meetingInfo);
        long timeOut = (meeting.getEndTime().getTime() - meeting.getStartTime().getTime()) / 1000 + 1;
        redisService.expire(key,timeOut, TimeUnit.SECONDS);
    }


    /**
     * 根据 meetingId 获取 meeting 信息
     *
     * @param meetingId
     * @return
     */
    @Override
    public SysMeeting getMeetingByMeetingId(String meetingId) {
        return sysMeetingMapper.selectSysMeetingByMeetingId(meetingId);
    }
}
