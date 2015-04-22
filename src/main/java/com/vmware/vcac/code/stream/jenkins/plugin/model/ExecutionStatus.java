package com.vmware.vcac.code.stream.jenkins.plugin.model;

/**
 * Created by rsaraf on 3/23/2015.
 */
public enum ExecutionStatus {

    STARTED,
    COMPLETED,
    FAILED,
    NOT_STARTED,
    IN_PROGRESS,
    PAUSED,
    DELETED,
    CANCELED;

    public String value() {
        return name();
    }

    public static ExecutionStatus fromValue(String v) {
        return valueOf(v);
    }

}