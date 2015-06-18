package com.vmware.vcac.code.stream.jenkins.plugin.model;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Created by rsaraf on 6/18/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskExecutionInfo implements Serializable {

    protected String id;
    protected ExecutionInfo executionInfo;
    protected String outputParams;
    protected String inputParams;
    protected String messages;
    protected Task task;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ExecutionInfo getExecutionInfo() {
        return executionInfo;
    }

    public void setExecutionInfo(ExecutionInfo executionInfo) {
        this.executionInfo = executionInfo;
    }

    public String getOutputParams() {
        return outputParams;
    }

    public void setOutputParams(String outputParams) {
        this.outputParams = outputParams;
    }

    public String getInputParams() {
        return inputParams;
    }

    public void setInputParams(String inputParams) {
        this.inputParams = inputParams;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
