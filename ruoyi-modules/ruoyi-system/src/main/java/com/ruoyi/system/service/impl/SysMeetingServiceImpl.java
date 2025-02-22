package com.ruoyi.system.service.impl;

import com.ruoyi.common.redis.generator.SnowflakeIdGenerator;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.system.domain.SysMeeting;
import com.ruoyi.system.mapper.SysMeetingMapper;
import com.ruoyi.system.service.ISysMeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
