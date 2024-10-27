package com.sondertara.common.timing.scheduling;

import java.util.Date;

/**
 * 指定的时间点调度，只调度一次
 *
 *  */
public class InstantTrigger implements Trigger {
    /**
     * mills
     */
    private Date deadline;

    /**
     *      */
    public InstantTrigger(Date instant){
        this.deadline = instant;
    }
    public InstantTrigger(long instant){
        this(new Date(instant));
    }

    @Override
    public Date nextExecutionTime(TriggerContext triggerContext) {
        Date lastExecution = triggerContext.lastScheduledExecutionTime();
        if (lastExecution == null) {
            return deadline;
        } else {
            return null;
        }
    }
}
