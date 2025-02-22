package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.SysMeeting;

import java.util.List;

/**
 * 系统会议Service接口
 *
 * @author dmzhang
 * @date 2025-02-22
 */
public interface ISysMeetingService {
    /**
     * 查询系统会议
     *
     * @param meetingId 系统会议主键
     * @return 系统会议
     */
    public SysMeeting selectSysMeetingByMeetingId(String meetingId);

    /**
     * 查询系统会议列表
     *
     * @param sysMeeting 系统会议
     * @return 系统会议集合
     */
    public List<SysMeeting> selectSysMeetingList(SysMeeting sysMeeting);

    /**
     * 新增系统会议
     *
     * @param sysMeeting 系统会议
     * @return 结果
     */
    public int insertSysMeeting(SysMeeting sysMeeting);

    /**
     * 修改系统会议
     *
     * @param sysMeeting 系统会议
     * @return 结果
     */
    public int updateSysMeeting(SysMeeting sysMeeting);

    /**
     * 批量删除系统会议
     *
     * @param meetingIds 需要删除的系统会议主键集合
     * @return 结果
     */
    public int deleteSysMeetingByMeetingIds(String[] meetingIds);

    /**
     * 删除系统会议信息
     *
     * @param meetingId 系统会议主键
     * @return 结果
     */
    public int deleteSysMeetingByMeetingId(String meetingId);

    R<String> takeTicket(SysMeeting sysMeeting);
}
