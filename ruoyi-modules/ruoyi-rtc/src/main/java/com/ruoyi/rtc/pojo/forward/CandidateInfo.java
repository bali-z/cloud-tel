package com.ruoyi.rtc.pojo.forward;

import com.ruoyi.rtc.pojo.SignalBase;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author dz
 */
@Data
@Accessors(chain = true)
public class CandidateInfo extends SignalBase {
    /**
     * The candidate string as defined in the ICE Candidate Attributes section of RFC 5245.
     */
    private final String candidate;

    /**
     * A unique identifier for the candidate, used in the ICE candidate gathering process.
     */
    private final String foundation;

    /**
     * The port number of the candidate.
     */
    private final Integer port;

    /**
     * The priority of the candidate, used in the ICE candidate selection process.
     */
    private final Integer priority;

    /**
     * The IP address of the related candidate, used in the ICE candidate gathering process.
     */
    private final String relatedAddress;

    /**
     * The port number of the related candidate, used in the ICE candidate gathering process.
     */
    private final Integer relatedPort;

    /**
     * The index of the media description line (m-line) in the SDP associated with this candidate.
     */
    private final Integer sdpMLineIndex;

    /**
     * The mid attribute of the media description line (m-line) in the SDP associated with this candidate.
     */
    private final String sdpMid;

    /**
     * A fragment of the username fragment used in the ICE candidate gathering process.
     */
    private final String usernameFragment;
}
