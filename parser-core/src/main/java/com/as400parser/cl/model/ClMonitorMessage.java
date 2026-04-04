package com.as400parser.cl.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an inline MONMSG (Monitor Message) condition attached to a CL command.
 * Contains the monitored message IDs and the commands to execute when the condition triggers.
 */
public class ClMonitorMessage {

    /** The monitored message ID(s), e.g., "CPF9898" or "*ANY". */
    private String msgId;

    /** The CMPDTA comparison data filter, if specified. */
    private String cmpData;

    /**
     * Commands to execute if the monitored condition is triggered.
     * Populated from the EXEC(...) clause of a MONMSG command.
     */
    private List<ClCommand> execCommands = new ArrayList<>();

    public String getMsgId() { return msgId; }
    public void setMsgId(String msgId) { this.msgId = msgId; }

    public String getCmpData() { return cmpData; }
    public void setCmpData(String cmpData) { this.cmpData = cmpData; }

    public List<ClCommand> getExecCommands() { return execCommands; }
    public void setExecCommands(List<ClCommand> execCommands) { this.execCommands = execCommands; }
}
