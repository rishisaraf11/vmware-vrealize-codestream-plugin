package com.vmware.vcac.code.stream.jenkins.plugin.model;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Created by rsaraf on 6/18/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReleasePipelineExecutionInfo implements Serializable {


    protected String id;

    protected String vcoExecutionId;
    protected String description;
    protected String currentStatusMessage;
    protected ExecutionInfo executionInfo;
    protected List<StageExecutionInfo> stages;
    protected String createdBy;
    protected XMLGregorianCalendar updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVcoExecutionId() {
        return vcoExecutionId;
    }

    public void setVcoExecutionId(String vcoExecutionId) {
        this.vcoExecutionId = vcoExecutionId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCurrentStatusMessage() {
        return currentStatusMessage;
    }

    public void setCurrentStatusMessage(String currentStatusMessage) {
        this.currentStatusMessage = currentStatusMessage;
    }

    public List<StageExecutionInfo> getStages() {
        return stages;
    }

    public void setStages(List<StageExecutionInfo> stages) {
        this.stages = stages;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public XMLGregorianCalendar getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(XMLGregorianCalendar updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ExecutionInfo getExecutionInfo() {
        return executionInfo;
    }

    public void setExecutionInfo(ExecutionInfo executionInfo) {
        this.executionInfo = executionInfo;
    }
}
