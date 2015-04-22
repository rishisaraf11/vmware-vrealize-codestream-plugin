package com.vmware.vcac.code.stream.jenkins.plugin;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.vmware.vcac.code.stream.jenkins.plugin.model.ExecutionStatus;
import com.vmware.vcac.code.stream.jenkins.plugin.model.PipelineParam;
import com.vmware.vcac.code.stream.jenkins.plugin.model.PluginParam;
import hudson.model.AbstractBuild;
import hudson.remoting.Callable;
import org.jenkinsci.remoting.RoleChecker;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static hudson.Util.fixEmptyAndTrim;

/**
 * Created by rsaraf on 3/25/2015.
 */
public class CodeStreamPipelineCallable implements Callable<Map<String, String>, IOException>, Serializable {
    private AbstractBuild<?, ?> build;
    private PluginParam params;

    public CodeStreamPipelineCallable(PluginParam params) {
        this.params = params;

    }

    @Override
    public Map<String, String> call() throws IOException {
        Map<String, String> data = new HashMap<String, String>();
        try {
            CodeStreamClient codeStreamClient = new CodeStreamClient(params);
            JsonObject pipelineJsonObj = codeStreamClient.fetchPipeline(params.getPipelineName());
            String pipelineId = pipelineJsonObj.get("id").getAsString();
            String status = pipelineJsonObj.get("status").getAsString();
            Map<String, PipelineParam> defaultParams = getPipelineParams(pipelineJsonObj);
            System.out.println("Successfully fetched Pipeline with id:" + pipelineId);
            if (!status.equals("ACTIVATED")) {
                throw new IOException(params.getPipelineName() + " is not activated");
            }

            if (!params.getPipelineParams().isEmpty()) {
                for (PipelineParam userParam : params.getPipelineParams()) {
                    PipelineParam defaultParam = defaultParams.get(fixEmptyAndTrim(userParam.getName()));
                    defaultParam.setValue(fixEmptyAndTrim(userParam.getValue()));
                }
            }

            JsonObject execJsonRes = codeStreamClient.executePipeline(pipelineId, new ArrayList(defaultParams.values()));
            JsonElement execIdElement = execJsonRes.get("id");
            if (execIdElement != null) {
                String execId = execIdElement.getAsString();
                data.put("CS_PIPELINE_EXECUTION_ID", execId);
                System.out.println("Pipeline executed successfully with execution id :" + execId);
                if (params.isWaitExec()) {
                    while (!codeStreamClient.isPipelineCompleted(pipelineId, execId)) {
                        System.out.println("Waiting for pipeline execution to complete");
                        Thread.sleep(10 * 1000);
                    }
                    JsonObject pipelineExecutionResponse = codeStreamClient.getPipelineExecutionResponse(pipelineId, execId);
                    ExecutionStatus pipelineExecStatus = codeStreamClient.getPipelineExecStatus(pipelineExecutionResponse);
                    data.put("CS_PIPELINE_EXECUTION_STATUS", pipelineExecStatus.toString());
                    data.put("CS_PIPELINE_EXECUTION_RES", pipelineExecutionResponse.toString());
                    switch (pipelineExecStatus) {
                        case COMPLETED:
                            System.out.println("Pipeline complete successfully");
                            break;
                        case FAILED:
                            System.out.println("Pipeline execution failed");
                            throw new IOException("Pipeline execution failed. Please go to CodeStream for more details");
                        case CANCELED:
                            throw new IOException("Pipeline execution cancelled. Please go to CodeStream for more details");
                    }
                }
            } else {
                codeStreamClient.handleError(execJsonRes);
            }
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        return data;
    }

    private Map<String, PipelineParam> getPipelineParams(JsonObject pipelineJsonObj) {
        Type type = new TypeToken<List<PipelineParam>>() {
        }.getType();
        Gson gson = new Gson();
        List<PipelineParam> params = gson.fromJson(pipelineJsonObj.get("pipelineParams").getAsJsonArray().toString(), type);
        Map<String, PipelineParam> paramMap = new HashMap<String, PipelineParam>();
        for (PipelineParam param : params) {
            paramMap.put(param.getName(), param);
        }
        return paramMap;
    }

    @Override
    public void checkRoles(RoleChecker roleChecker) throws SecurityException {

    }


}
