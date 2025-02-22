package com.ruoyi.rtc.util;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.rtc.domain.SysMeeting;
import com.ruoyi.rtc.handler.SignalWebSocketHandler;
import com.ruoyi.rtc.mapper.SysMeetingMapper;
import com.ruoyi.rtc.pojo.MeetingInfo;
import com.ruoyi.rtc.pojo.MeetingMember;
import com.ruoyi.rtc.pojo.RtcR;
import com.ruoyi.rtc.pojo.SignalType;
import com.ruoyi.rtc.pojo.forward.EnterInfo;
import com.ruoyi.system.api.RemoteUserService;
import com.ruoyi.system.api.domain.SysUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static com.ruoyi.rtc.pojo.Constants.ONLINE_MEETING_PREFIX_KEY;

/**
 * @author dz
 * <p>
 * 会议工具类
 */
public class MeetingUtil {

    /**
     * 在会议中添加新人
     *
     * @param meetingId
     * @param member
     */
    public static void addMemberToMeeting(RedisService redisService, String meetingId,MeetingMember member) {
        Lock lock = SignalWebSocketHandler.getMeetingLock(meetingId);
        try {
            lock.lock();
            MeetingInfo meetingInfo = redisService.getCacheObject(ONLINE_MEETING_PREFIX_KEY + meetingId);
            List<MeetingMember> members = meetingInfo.getMeetingMembers();
            // 避免重复添加
            boolean exist = false;
            for (MeetingMember meetingMember : members) {
                if(meetingMember.getUserId().equals(member.getUserId())){
                    exist = true;
                    break;
                }
            }
            // 成员不在会议成员中才添加
            if(!exist) {
                members.add(member);
            }
            // 如果加入会议的是会议的创建者
            if(member.getUserId().equals(meetingInfo.getMeetingOwnerUserId())){
                // 会议创建者的其他信息补充
                meetingInfo.setMeetingOwnerName(member.getName());
                meetingInfo.setMeetingOwnerSessionId(member.getSessionId());
                meetingInfo.setMeetingOwnerAvatar(member.getAvatar());
            }
            // 会议信息缓存
            cacheMeeting(redisService,meetingId,meetingInfo.getEndTime(),meetingInfo);

            // 发一个进入会议的信令，给其他成员发送自己的信息以便于交换offer answer candidate
            EnterInfo enterInfo = new EnterInfo();
            enterInfo.setMeetingId(meetingId);
            enterInfo.setSourceSessionId(member.getSessionId());
            enterInfo.setTicket(member.getTicket());
            enterInfo.setUserId(member.getUserId());
            enterInfo.setName(member.getName());
            enterInfo.setAvatar(member.getAvatar());

            // 给其他成员发送Enter信令
            for (MeetingMember meetingMember : members) {
                if (meetingMember.getUserId().equals(member.getUserId())) {
                    continue;
                }
                SignalWebSocketHandler.sendMessage(JSONObject.toJSONString(RtcR.instance(Constants.SUCCESS,SignalType.ENTER,"",enterInfo)), meetingMember.getSessionId());
            }

        } catch (Exception exception) {
            lock.unlock();
        } finally {
            lock.unlock();
        }


    }

    /**
     * 踢除会议中某人
     * 从会议缓存中删除，并给那个session发一个下线信令
     *
     * @param meetingId
     * @param memberUserId
     */
    public static void eliminateMemberFromMeeting(RedisService redisService, String meetingId, Long memberUserId) {
        Lock lock = SignalWebSocketHandler.getMeetingLock(meetingId);
        try {
            lock.lock();
            MeetingInfo meetingInfo = redisService.getCacheObject(ONLINE_MEETING_PREFIX_KEY + meetingId);
            List<MeetingMember> members = meetingInfo.getMeetingMembers();
            Iterator<MeetingMember> iterator = members.iterator();
            MeetingMember member = null;
            while (iterator.hasNext()) {
                member = iterator.next();
                if (memberUserId.equals(member.getUserId())) {
                    iterator.remove();
                    // 强制退出信令发送给被推出的家伙
                    SignalWebSocketHandler.sendMessage(JSONObject.toJSONString(RtcR.instance(Constants.SUCCESS,SignalType.FORCED_RETURN,"您被请出会议!","您被请出会议!")), member.getSessionId());
                }
            }
            redisService.setCacheObject(ONLINE_MEETING_PREFIX_KEY + meetingId, meetingInfo);
        } catch (Exception exception) {
            lock.unlock();
            exception.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取会议信息
     *
     * @param redisService
     * @param meetingId
     * @return
     */
    public static MeetingInfo getMeetingInfo(RedisService redisService, String meetingId) {
        return redisService.getCacheObject(ONLINE_MEETING_PREFIX_KEY + meetingId);
    }

    /**
     * 缓存某个会议信息
     *
     * @param redisService
     * @param meetingId
     * @param sysMeetingMapper
     */
    public static void cacheMeetingInfo(RedisService redisService, String meetingId, SysMeetingMapper sysMeetingMapper, RemoteUserService remoteUserService) {
        // 查询会议信息
        SysMeeting meeting = sysMeetingMapper.selectSysMeetingByMeetingId(meetingId);

        // 没查询到会议信息
        if (meeting == null) {
            return;
        }
        // 获取会议发起者信息
        R<SysUser> userInfo = remoteUserService.getUserInfoById(meeting.getUserId(), SecurityConstants.INNER);

        // 封装 MeetingInfo 信息
        MeetingInfo meetingInfo = new MeetingInfo();
        meetingInfo.setMeetingId(meetingId).setMeetingTitle(meeting.getTitle())
                .setMeetingOwnerUserId(meeting.getUserId()).setMeetingPassword(meeting.getPassword())
                .setStartTime(meeting.getStartTime()).setEndTime(meeting.getEndTime())
                .setMeetingOwnerName(userInfo.getData().getUserName())
                .setMeetingOwnerAvatar(userInfo.getData().getAvatar()).setMeetingMembers(new ArrayList<>());
        // 缓存会议
        cacheMeeting(redisService, meetingId, meeting.getEndTime(), meetingInfo);
    }

    private static void cacheMeeting(RedisService redisService, String meetingId, Date endTime, MeetingInfo meetingInfo) {
        long currentTimeMillis = System.currentTimeMillis();
        String key = ONLINE_MEETING_PREFIX_KEY + meetingId;
        redisService.setCacheObject(key, meetingInfo);
        long timeOut = (endTime.getTime() - currentTimeMillis) / 1000 + 60;
        redisService.expire(key, timeOut, TimeUnit.SECONDS);
    }

}
