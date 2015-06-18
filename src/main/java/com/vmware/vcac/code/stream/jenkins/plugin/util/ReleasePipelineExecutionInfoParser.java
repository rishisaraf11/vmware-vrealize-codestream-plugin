package com.vmware.vcac.code.stream.jenkins.plugin.util;

import java.io.IOException;
import java.util.List;

import com.vmware.vcac.code.stream.jenkins.plugin.model.ExecutionInfo;
import com.vmware.vcac.code.stream.jenkins.plugin.model.ExecutionStatus;
import com.vmware.vcac.code.stream.jenkins.plugin.model.ReleasePipelineExecutionInfo;
import com.vmware.vcac.code.stream.jenkins.plugin.model.StageExecutionInfo;
import com.vmware.vcac.code.stream.jenkins.plugin.model.TaskExecutionInfo;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Created by rsaraf on 4/27/2015.
 */
public class ReleasePipelineExecutionInfoParser {

    ReleasePipelineExecutionInfo executionResponse;
    String executionResponseJson;
    ObjectMapper mapper = new ObjectMapper();

    public ReleasePipelineExecutionInfoParser(ReleasePipelineExecutionInfo executionResponse) {
        this.executionResponse = executionResponse;
    }

    public ReleasePipelineExecutionInfoParser(String executionResponseJson) throws IOException {
        this.executionResponseJson = executionResponseJson;
        this.executionResponse = mapper.readValue(executionResponseJson, ReleasePipelineExecutionInfo.class);
    }

    public TaskExecutionInfo getFailedTask() {
        TaskExecutionInfo failedTask = null;
        List<StageExecutionInfo> stages = executionResponse.getStages();
        for (StageExecutionInfo stage : stages) {
            for (TaskExecutionInfo executionInfo : stage.getTasks()) {
                ExecutionInfo taskExecStatus = executionInfo.getExecutionInfo();
                if (taskExecStatus != null) {
                    ExecutionStatus status = taskExecStatus.getStatus();
                    if (ExecutionStatus.FAILED == status) {
                        failedTask = executionInfo;
                        break;
                    }
                }
            }
        }
        return failedTask;
    }

    public ExecutionStatus getPipelineExecStatus() throws IOException {
        ExecutionStatus executionStatus = null;
        ExecutionInfo info = executionResponse.getExecutionInfo();
        if (info != null) {
            return info.getStatus();
        }
        return executionStatus;
    }

    public boolean isPipelineCompleted() throws IOException {
        ExecutionStatus pipelineExecStatus = this.getPipelineExecStatus();
        switch (pipelineExecStatus) {
            case COMPLETED:
            case FAILED:
            case CANCELED:
                return true;
            default:
                return false;
        }
    }

    public String getPipelineExeResponseAsJson() throws IOException {
        return executionResponseJson;
    }


}
