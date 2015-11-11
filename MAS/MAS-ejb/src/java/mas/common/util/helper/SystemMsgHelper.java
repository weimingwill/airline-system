/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.common.util.helper;

import java.util.List;
import mas.common.entity.SystemMsg;

/**
 *
 * @author winga_000
 */
public class SystemMsgHelper {
    private String sender;
    private List<SystemMsg> msgs;

    public SystemMsgHelper() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public List<SystemMsg> getMsgs() {
        return msgs;
    }

    public void setMsgs(List<SystemMsg> msgs) {
        this.msgs = msgs;
    }
}
