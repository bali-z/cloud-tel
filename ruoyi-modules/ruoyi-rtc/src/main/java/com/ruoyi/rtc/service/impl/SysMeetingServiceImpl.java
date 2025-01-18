package com.ruoyi.rtc.service.impl;

import java.util.List;
import com.ruoyi.common.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.rtc.mapper.SysMeetingMapper;
import com.ruoyi.rtc.domain.SysMeeting;
import com.ruoyi.rtc.service.ISysMeetingService;

/**
 * 会议管理Service业务层处理
 * 
 * @author dz
 * @date 2025-01-18
 */
@Service
public class SysMeetingServiceImpl implements ISysMeetingService 
{
    @Autowired
    private SysMeetingMapper sysMeetingMapper;

    /**
     * 查询会议管理
     * 
     * @param id 会议管理主键
     * @return 会议管理
     */
    @Override
    public SysMeeting selectSysMeetingById(Long id)
    {
        return sysMeetingMapper.selectSysMeetingById(id);
    }

    /**
     * 查询会议管理列表
     * 
     * @param sysMeeting 会议管理
     * @return 会议管理
     */
    @Override
    public List<SysMeeting> selectSysMeetingList(SysMeeting sysMeeting)
    {
        return sysMeetingMapper.selectSysMeetingList(sysMeeting);
    }

    /**
     * 新增会议管理
     * 
     * @param sysMeeting 会议管理
     * @return 结果
     */
    @Override
    public int insertSysMeeting(SysMeeting sysMeeting)
    {
        sysMeeting.setCreateTime(DateUtils.getNowDate());
        return sysMeetingMapper.insertSysMeeting(sysMeeting);
    }

    /**
     * 修改会议管理
     * 
     * @param sysMeeting 会议管理
     * @return 结果
     */
    @Override
    public int updateSysMeeting(SysMeeting sysMeeting)
    {
        return sysMeetingMapper.updateSysMeeting(sysMeeting);
    }

    /**
     * 批量删除会议管理
     * 
     * @param ids 需要删除的会议管理主键
     * @return 结果
     */
    @Override
    public int deleteSysMeetingByIds(Long[] ids)
    {
        return sysMeetingMapper.deleteSysMeetingByIds(ids);
    }

    /**
     * 删除会议管理信息
     * 
     * @param id 会议管理主键
     * @return 结果
     */
    @Override
    public int deleteSysMeetingById(Long id)
    {
        return sysMeetingMapper.deleteSysMeetingById(id);
    }
}
