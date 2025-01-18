package com.ruoyi.rtc.mapper;

import java.util.List;
import com.ruoyi.rtc.domain.SysMeeting;

/**
 * 会议管理Mapper接口
 * 
 * @author dz
 * @date 2025-01-18
 */
public interface SysMeetingMapper 
{
    /**
     * 查询会议管理
     * 
     * @param id 会议管理主键
     * @return 会议管理
     */
    public SysMeeting selectSysMeetingById(Long id);

    /**
     * 查询会议管理列表
     * 
     * @param sysMeeting 会议管理
     * @return 会议管理集合
     */
    public List<SysMeeting> selectSysMeetingList(SysMeeting sysMeeting);

    /**
     * 新增会议管理
     * 
     * @param sysMeeting 会议管理
     * @return 结果
     */
    public int insertSysMeeting(SysMeeting sysMeeting);

    /**
     * 修改会议管理
     * 
     * @param sysMeeting 会议管理
     * @return 结果
     */
    public int updateSysMeeting(SysMeeting sysMeeting);

    /**
     * 删除会议管理
     * 
     * @param id 会议管理主键
     * @return 结果
     */
    public int deleteSysMeetingById(Long id);

    /**
     * 批量删除会议管理
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysMeetingByIds(Long[] ids);
}
