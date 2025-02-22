package com.ruoyi.rtc.meeting.signal;

import com.ruoyi.rtc.meeting.SignalTypeEnum;
import lombok.Data;

import java.io.Serializable;

@SuppressWarnings("ALL")
@Data
public class JoinConfirm extends MeetingSignal implements Serializable {

    /**
     *  请求参会人姓名
     */
    private String name;

    /**
     *  请求参会人头像
     */
    private String avatar;

    public JoinConfirm(){
        super();
        super.setCode(SignalTypeEnum.JOIN_CONFIRM);
    }
}
