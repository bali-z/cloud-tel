package com.ruoyi.common.redis.util;

import com.ruoyi.common.core.pojo.MeetingProcess;
import com.ruoyi.common.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Date;

@SuppressWarnings("ALL")
public class MeetingProcessUtil {

    @Autowired
    private RedisService redisService;

    private static final String MEETING_PROCESS_PREFIX = "meeting_progress:mettingId:";

    /**
     * 根据会议ID获取会议进程信息
     */
    public MeetingProcess getMeetingProcess(String meetingId) {
        return redisService.getCacheObject(MEETING_PROCESS_PREFIX + meetingId);
    }

    /**
     * 缓存会议进程信息
     */
    public void cacheMeetingProcess(MeetingProcess meetingProcess) {
        Date currentTime = new Date();
        redisService.setCacheObject(MEETING_PROCESS_PREFIX + meetingProcess.getMeetingId(), meetingProcess);
        redisService.expire(MEETING_PROCESS_PREFIX + meetingProcess.getMeetingId(), meetingProcess.getPlanEndTime().getTime() - currentTime.getTime() + 1000);
    }

    public void removeMeetingProcess(String meetingId) {
        redisService.deleteObject(MEETING_PROCESS_PREFIX + meetingId);
    }

    /**
     * 删除会议进程中的成员
     *
     * @param meetingId
     * @param userId
     */
    public void removeMemberFromMeetingProcess(String meetingId, Long userId) {
        MeetingProcess meetingProcess = getMeetingProcess(meetingId);
        meetingProcess.getMeetingMembers().removeIf(member -> member.getUserId().equals(userId));
        cacheMeetingProcess(meetingProcess);
    }

    /**
     * 删除所有会议进程中的某个成员
     */
    public void removeAllMemberFromMeetingProcess(Long userId) {
        Collection<String> keys = redisService.keys(MEETING_PROCESS_PREFIX + "*");
        for (String key : keys) {
            MeetingProcess meetingProcess = redisService.getCacheObject(key);
            meetingProcess.getMeetingMembers().removeIf(member -> member.getUserId().equals(userId));
            cacheMeetingProcess(meetingProcess);
        }
    }


}
