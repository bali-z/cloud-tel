package com.ruoyi.system.controller;

import com.ruoyi.common.core.utils.poi.ExcelUtil;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.ruoyi.system.domain.SysMeeting;
import com.ruoyi.system.service.ISysMeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 系统会议Controller
 *
 * @author dmzhang
 * @date 2025-02-22
 */
@RestController
@RequestMapping("/meeting")
public class SysMeetingController extends BaseController {
    @Autowired
    private ISysMeetingService sysMeetingService;

    /**
     * 查询系统会议列表
     */
    @RequiresPermissions("system:meeting:list")
    @GetMapping("/list")
    public TableDataInfo list(SysMeeting sysMeeting) {
        startPage();
        List<SysMeeting> list = sysMeetingService.selectSysMeetingList(sysMeeting);
        return getDataTable(list);
    }

    /**
     * 导出系统会议列表
     */
    @RequiresPermissions("system:meeting:export")
    @Log(title = "系统会议", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysMeeting sysMeeting) {
        List<SysMeeting> list = sysMeetingService.selectSysMeetingList(sysMeeting);
        ExcelUtil<SysMeeting> util = new ExcelUtil<SysMeeting>(SysMeeting.class);
        util.exportExcel(response, list, "系统会议数据");
    }

    /**
     * 获取系统会议详细信息
     */
    @RequiresPermissions("system:meeting:query")
    @GetMapping(value = "/{meetingId}")
    public AjaxResult getInfo(@PathVariable("meetingId") String meetingId) {
        return success(sysMeetingService.selectSysMeetingByMeetingId(meetingId));
    }

    /**
     * 新增系统会议
     */
    @RequiresPermissions("system:meeting:add")
    @Log(title = "系统会议", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysMeeting sysMeeting) {
        return toAjax(sysMeetingService.insertSysMeeting(sysMeeting));
    }

    /**
     * 修改系统会议
     */
    @RequiresPermissions("system:meeting:edit")
    @Log(title = "系统会议", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysMeeting sysMeeting) {
        return toAjax(sysMeetingService.updateSysMeeting(sysMeeting));
    }

    /**
     * 删除系统会议
     */
    @RequiresPermissions("system:meeting:remove")
    @Log(title = "系统会议", businessType = BusinessType.DELETE)
    @DeleteMapping("/{meetingIds}")
    public AjaxResult remove(@PathVariable String[] meetingIds) {
        return toAjax(sysMeetingService.deleteSysMeetingByMeetingIds(meetingIds));
    }
}
