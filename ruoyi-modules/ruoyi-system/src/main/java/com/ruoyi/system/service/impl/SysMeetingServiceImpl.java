package com.ruoyi.system.service.impl;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.pojo.MeetingProcess;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.ticket.TicketUtil;
import com.ruoyi.common.redis.generator.SnowflakeIdGenerator;
import com.ruoyi.common.redis.util.MeetingProcessUtil;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.system.api.domain.SysUser;
import com.ruoyi.system.domain.SysMeeting;
import com.ruoyi.system.mapper.SysMeetingMapper;
import com.ruoyi.system.service.ISysMeetingService;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * 系统会议Service业务层处理
 *
 * @author dmzhang
 * @date 2025-02-22
 */
@SuppressWarnings("ALL")
@Service
public class SysMeetingServiceImpl implements ISysMeetingService {
    @Autowired
    private SysMeetingMapper sysMeetingMapper;

    @Autowired
    private MeetingProcessUtil meetingProcessUtil;

    @Autowired
    private ISysUserService userService;

    /**
     * 雪花算法ID迭代器
     */
    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;


    /**
     * 查询系统会议
     *
     * @param meetingId 系统会议主键
     * @return 系统会议
     */
    @Override
    public SysMeeting selectSysMeetingByMeetingId(String meetingId) {
        return sysMeetingMapper.selectSysMeetingByMeetingId(meetingId);
    }

    /**
     * 查询系统会议列表
     *
     * @param sysMeeting 系统会议
     * @return 系统会议
     */
    @Override
    public List<SysMeeting> selectSysMeetingList(SysMeeting sysMeeting) {
        return sysMeetingMapper.selectSysMeetingList(sysMeeting);
    }

    /**
     * 新增系统会议
     *
     * @param sysMeeting 系统会议
     * @return 结果
     */
    @Override
    public int insertSysMeeting(SysMeeting sysMeeting) {
        sysMeeting.setMeetingId(snowflakeIdGenerator.nextId());
        sysMeeting.setHoldUserId(SecurityUtils.getUserId());
        return sysMeetingMapper.insertSysMeeting(sysMeeting);
    }

    /**
     * 修改系统会议
     *
     * @param sysMeeting 系统会议
     * @return 结果
     */
    @Override
    public int updateSysMeeting(SysMeeting sysMeeting) {
        return sysMeetingMapper.updateSysMeeting(sysMeeting);
    }

    /**
     * 批量删除系统会议
     *
     * @param meetingIds 需要删除的系统会议主键
     * @return 结果
     */
    @Override
    public int deleteSysMeetingByMeetingIds(String[] meetingIds) {
        return sysMeetingMapper.deleteSysMeetingByMeetingIds(meetingIds);
    }

    /**
     * 删除系统会议信息
     *
     * @param meetingId 系统会议主键
     * @return 结果
     */
    @Override
    public int deleteSysMeetingByMeetingId(String meetingId) {
        return sysMeetingMapper.deleteSysMeetingByMeetingId(meetingId);
    }

    /**
     *  生成票据
     * @param sysMeeting
     * @return
     */
    @Override
    public R<String> takeTicket(SysMeeting sysMeeting) {
        if(null == sysMeeting || null == sysMeeting.getMeetingId()){
            return R.fail("会议号为空!");
        }
        Long userId = SecurityUtils.getUserId();
        String meetingId = sysMeeting.getMeetingId();
        SysMeeting meeting = sysMeetingMapper.selectSysMeetingByMeetingId(meetingId);
        if(null == meetingId){
            return R.fail("会议不存在!");
        }

        // 如果会议没到开始时间
        Date currentTime = new Date();
        if(currentTime.before(meeting.getPlanStartTime())){
            return R.fail("会议尚未开始!");
        }
        if(currentTime.after(meeting.getPlanEndTime())){
            return R.fail("会议已经结束");
        }
        // 暂存会议信息
        if(null == meetingProcessUtil.getMeetingProcess(meetingId)) {
            // 缓存会议信息
            SysUser sysUser = userService.selectUserById(meeting.getHoldUserId());
            MeetingProcess meetingProcess = new MeetingProcess();
            meetingProcess.setMeetingId(meeting.getMeetingId());
            meetingProcess.setPassword(meeting.getPassword());
            meetingProcess.setHoldUserId(meeting.getHoldUserId());
            meetingProcess.setName(sysUser.getUserName());
            meetingProcess.setAvatarUrl(sysUser.getAvatar());
            meetingProcess.setPlanStartTime(meeting.getPlanStartTime());
            meetingProcess.setPlanEndTime(meeting.getPlanEndTime());
            meetingProcess.setMeetingMembers(new HashSet<>());
            // 暂存会议信息
            meetingProcessUtil.cacheMeetingProcess(meetingProcess);
        }
        // 如果当前用户就是会议发起人则直接给人家生成票据
        if(userId.equals(meeting.getHoldUserId())){
            return R.ok(TicketUtil.createTicket(meetingId,meeting.getHoldUserId(),userId));
        }
        // 否则鉴权
        // 1.密码是否正确
        if(!StringUtils.isEmpty(sysMeeting.getPassword()) && sysMeeting.getPassword().equals(meeting.getPassword())){
            return R.ok(TicketUtil.createTicket(meetingId,meeting.getHoldUserId(),userId));
        }
        // 2.密码不正确/会议有密码但是没输入密码
        if(!StringUtils.isEmpty(meeting.getPassword()) && !meeting.getPassword().equals(sysMeeting.getPassword())){
            return R.fail("会议密码错误!");
        }
        // 会议没有密码无法鉴权交给会议发起者处理，这里就生成ticket
        return R.ok();
    }
}
