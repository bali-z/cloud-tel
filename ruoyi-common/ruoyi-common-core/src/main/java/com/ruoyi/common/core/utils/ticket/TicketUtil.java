package com.ruoyi.common.core.utils.ticket;


import com.ruoyi.common.core.utils.JwtUtils;
import com.ruoyi.common.core.utils.StringUtils;
import io.jsonwebtoken.Claims;

import java.util.HashMap;
import java.util.Map;

import static com.ruoyi.common.core.constant.TicketConstants.*;

/**
 *  会议票据生成校验工具类
 */
public class TicketUtil {

    /**
     *  生成票据
     * @param meetingId
     * @param ownerUserId
     * @param memberUserId
     * @return
     */
    public static String createTicket(String meetingId,Long ownerUserId,Long memberUserId){
        Map<String, Object> claims = new HashMap<>();
        claims.put(MEETING_ID,meetingId);
        claims.put(MEETING_OWNER_USER_ID,ownerUserId);
        claims.put(MEETING_MEMBER_USER_ID, memberUserId);
        String ticket = JwtUtils.createToken(claims);
        return ticket;
    }

    /**
     *  校验票据
     * @param ticket
     * @param meetingId
     * @param ownerUserId
     * @param memberUserId
     * @return
     */
    public static boolean verifyTicket(String ticket,String meetingId,Long ownerUserId,Long memberUserId){
        if(StringUtils.isEmpty(ticket)){
            return false;
        }
        Claims claims = JwtUtils.parseToken(ticket);
        String ticketMeetingId = (String)claims.get(MEETING_ID);
        Long ticketOwnerUserId = Long.parseLong(String.valueOf(claims.get(MEETING_OWNER_USER_ID)));
        Long ticketMemberUserId = Long.parseLong(String.valueOf(claims.get(MEETING_MEMBER_USER_ID)));
        return meetingId.equals(ticketMeetingId) && ownerUserId.equals(ticketOwnerUserId) && memberUserId.equals(ticketMemberUserId);
    }
}
