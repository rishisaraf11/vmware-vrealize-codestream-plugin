package com.vmware.vcac.code.stream.jenkins.plugin.model;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.Serializable;

/**
 * Created by rsaraf on 6/18/2015.
 */
public class ExecutionInfo implements Serializable {

    protected String id;
    protected XMLGregorianCalendar startedAt;
    protected XMLGregorianCalendar finishedAt;
    protected ExecutionStatus status;
    protected long runtimeInMs;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public XMLGregorianCalendar getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(XMLGregorianCalendar startedAt) {
        this.startedAt = startedAt;
    }

    public XMLGregorianCalendar getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(XMLGregorianCalendar finishedAt) {
        this.finishedAt = finishedAt;
    }

    public ExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(ExecutionStatus status) {
        this.status = status;
    }

    public long getRuntimeInMs() {
        return runtimeInMs;
    }

    public void setRuntimeInMs(long runtimeInMs) {
        this.runtimeInMs = runtimeInMs;
    }
}
