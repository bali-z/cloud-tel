package com.ruoyi.rtc.service.impl;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.JwtUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.redis.generator.SnowflakeIdGenerator;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.rtc.domain.SysMeeting;
import com.ruoyi.rtc.mapper.SysMeetingMapper;
import com.ruoyi.rtc.pojo.MeetingJoinForm;
import com.ruoyi.rtc.service.ISysMeetingService;
import com.ruoyi.system.api.model.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     *  todo: 授权用的，外带将会议加入redis的功能
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
        Long currentUserUserid = currentUser.getUserid();
        Map<String, Object> claims = new HashMap<>();
        claims.put(MEETING_TICKET_ITEM_MEETING_ID, meeting.getMeetingId());
        claims.put(MEETING_TICKET_ITEM_MEETING_OWNER_USER_ID, meeting.getUserId());
        claims.put(MEETING_TICKET_ITEM_MEETING_MEMBER_USER_ID, currentUserUserid);
        // 会议票据
        String ticket = JwtUtils.createToken(claims);
        // 当前请求用户是 会议发起人
        if (currentUserUserid.equals(meeting.getUserId())) {
            return R.ok("ticket", ticket);
        }
        // 当前请求用户 不是会议发起人
        // 用户没有输入密码 不下发密钥，进入会议时让管理员确认
        if(StringUtils.isEmpty(password)){
            return R.ok();
        }
        // 会议没有设置密码，是否限制加入会议 暂时先这样,统一由管理员确认
        if(StringUtils.isEmpty(meeting.getPassword())){
            return R.ok();
        }
        // 密码错误 返回错误
        if (!meeting.getPassword().equals(password)) {
            return R.fail("密码错误!");
        }else {
            return R.ok("ticket", ticket);
        }
//        // todo 是否限制加入会议 暂时不写逻辑多！
//        if (meeting.getIsRestricted().equals('Y')) {
//
//        }
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
