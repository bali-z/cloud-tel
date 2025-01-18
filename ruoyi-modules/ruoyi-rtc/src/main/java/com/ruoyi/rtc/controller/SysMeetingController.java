package com.ruoyi.rtc.controller;

import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.ruoyi.rtc.domain.SysMeeting;
import com.ruoyi.rtc.service.ISysMeetingService;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.utils.poi.ExcelUtil;
import com.ruoyi.common.core.web.page.TableDataInfo;

/**
 * 会议管理Controller
 * 
 * @author dz
 * @date 2025-01-18
 */
@RestController
@RequestMapping("/meeting")
public class SysMeetingController extends BaseController
{
    @Autowired
    private ISysMeetingService sysMeetingService;

    /**
     * 查询会议管理列表
     */
    @RequiresPermissions("rtc:meeting:list")
    @GetMapping("/list")
    public TableDataInfo list(SysMeeting sysMeeting)
    {
        startPage();
        List<SysMeeting> list = sysMeetingService.selectSysMeetingList(sysMeeting);
        return getDataTable(list);
    }

    /**
     * 导出会议管理列表
     */
    @RequiresPermissions("rtc:meeting:export")
    @Log(title = "会议管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysMeeting sysMeeting)
    {
        List<SysMeeting> list = sysMeetingService.selectSysMeetingList(sysMeeting);
        ExcelUtil<SysMeeting> util = new ExcelUtil<SysMeeting>(SysMeeting.class);
        util.exportExcel(response, list, "会议管理数据");
    }

    /**
     * 获取会议管理详细信息
     */
    @RequiresPermissions("rtc:meeting:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(sysMeetingService.selectSysMeetingById(id));
    }

    /**
     * 新增会议管理
     */
    @RequiresPermissions("rtc:meeting:add")
    @Log(title = "会议管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysMeeting sysMeeting)
    {
        return toAjax(sysMeetingService.insertSysMeeting(sysMeeting));
    }

    /**
     * 修改会议管理
     */
    @RequiresPermissions("rtc:meeting:edit")
    @Log(title = "会议管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysMeeting sysMeeting)
    {
        return toAjax(sysMeetingService.updateSysMeeting(sysMeeting));
    }

    /**
     * 删除会议管理
     */
    @RequiresPermissions("rtc:meeting:remove")
    @Log(title = "会议管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(sysMeetingService.deleteSysMeetingByIds(ids));
    }
}
