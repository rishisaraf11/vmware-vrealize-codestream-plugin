package com.vmware.vcac.code.stream.jenkins.plugin.model;

import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;


/**
 * Created by rsaraf on 6/18/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StageExecutionInfo implements Serializable {

    protected String id;
    protected String name;
    protected List<TaskExecutionInfo> tasks;
    protected ExecutionStatus status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TaskExecutionInfo> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskExecutionInfo> tasks) {
        this.tasks = tasks;
    }

    public ExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(ExecutionStatus status) {
        this.status = status;
    }
}
