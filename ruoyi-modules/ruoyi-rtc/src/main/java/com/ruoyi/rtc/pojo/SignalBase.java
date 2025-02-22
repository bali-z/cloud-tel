package com.ruoyi.rtc.pojo;

import com.ruoyi.common.core.utils.JwtUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.rtc.domain.SysMeeting;
import io.jsonwebtoken.Claims;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

import static com.ruoyi.rtc.pojo.Constants.*;

/**
 * @author dz
 */
@Data
@Accessors(chain = true)
public class SignalBase {
    /**
     * 信令发送方
     */
    private String sourceSessionId;

    /**
     *  发送方 userId
     */
    private Long sourceUserId;
    /**
     * 信令接受方
     */
    private String targetSessionId;

    /**
     *  接受方 userId
     */
    private Long targetUserId;

    /**
     * 会议id
     */
    private String meetingId;

    /**
     * 票据 (meetingId,meetingOwnerUserId,meetingMemberUserId) -> JWT -> ticket
     */
    private String ticket;


    /**
     * 创建票据
     *
     * @param meeting
     * @param currentUserUserId
     * @return
     */
    public static String createTicket(SysMeeting meeting, Long currentUserUserId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(MEETING_TICKET_ITEM_MEETING_ID, meeting.getMeetingId());
        claims.put(MEETING_TICKET_ITEM_MEETING_OWNER_USER_ID, meeting.getUserId());
        claims.put(MEETING_TICKET_ITEM_MEETING_MEMBER_USER_ID, currentUserUserId);
        return JwtUtils.createToken(claims);
    }


    /**
     * 校验票据是否有效
     *
     * @param ticket
     * @param userId
     * @param meetingId
     * @param meetingOwnerUserId
     * @return
     */
    public static boolean verifyTicket(String ticket, Long userId, String meetingId, Long meetingOwnerUserId) {
        // 票据为空
        if (StringUtils.isEmpty(ticket)) {
            return false;
        }
        Claims claims = JwtUtils.parseToken(ticket);
        String meetingIdInTicket = (String) claims.get(MEETING_TICKET_ITEM_MEETING_ID);
        Long userIdInTicket = ((Integer) claims.get(MEETING_TICKET_ITEM_MEETING_OWNER_USER_ID)).longValue();
        Long memberIdInTicket = ((Integer) claims.get(MEETING_TICKET_ITEM_MEETING_MEMBER_USER_ID)).longValue();

        return meetingId.equals(meetingIdInTicket) && meetingOwnerUserId.equals(userIdInTicket) && userId.equals(memberIdInTicket);
    }

}
