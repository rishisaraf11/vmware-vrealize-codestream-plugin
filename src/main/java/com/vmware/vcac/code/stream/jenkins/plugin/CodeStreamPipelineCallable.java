package com.vmware.vcac.code.stream.jenkins.plugin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.EnvironmentContributingAction;
import hudson.remoting.Callable;
import org.jenkinsci.remoting.RoleChecker;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rsaraf on 3/25/2015.
 */
public class CodeStreamPipelineCallable implements Callable<Map<String,String>, IOException>, Serializable {
    private AbstractBuild<?, ?> build;
    private String serverUrl;
    private String userName;
    private String password;
    private String tenant;
    private String pipelineName;
//    private PrintStream logger;
    private List<PipelineParam> pipelineParams;
    private boolean waitExec;

    public CodeStreamPipelineCallable(String serverUrl, String userName, String password, String tenant, String pipelineName, List<PipelineParam> pipelineParams, boolean waitExec) {
        this.serverUrl = serverUrl;
        this.userName = userName;
        this.password = password;
        this.tenant = tenant;
        this.pipelineName = pipelineName;
        this.pipelineParams = pipelineParams;
        this.waitExec = waitExec;
    }

    @Override
    public Map<String,String> call() throws IOException {
        Map<String,String> data = new HashMap<String,String>();
        try {
            CodeStreamClient codeStreamClient = new CodeStreamClient(serverUrl, userName, password, tenant);
            JsonObject pipelineJsonObj = codeStreamClient.fetchPipeline(pipelineName);
            String pipelineId = pipelineJsonObj.get("id").getAsString();
            String status = pipelineJsonObj.get("status").getAsString();
            System.out.println("Successfully fetched Pipeline with id:" + pipelineId);
            if (!status.equals("ACTIVATED")) {
                throw new IOException(pipelineName + " is not activated");
            }

            JsonObject execJsonRes = codeStreamClient.executePipeline(pipelineId, pipelineParams);
            JsonElement execIdElement = execJsonRes.get("id");
            if (execIdElement != null) {
                String execId = execIdElement.getAsString();
                data.put("CS_PIPELINE_EXECUTION_ID", execId);
                System.out.println("Pipeline executed successfully with execution id :" + execId);
                if (waitExec) {
                    while (!codeStreamClient.isPipelineCompleted(pipelineId, execId)) {
                        System.out.println("Waiting for pipeline execution to complete");
                        Thread.sleep(10 * 1000);
                    }
                    ExecutionStatus pipelineExecStatus = codeStreamClient.getPipelineExecStatus(pipelineId, execId);
                    data.put("CS_PIPELINE_EXECUTION_STATUS", pipelineExecStatus.toString());
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

    @Override
    public void checkRoles(RoleChecker roleChecker) throws SecurityException {

    }


}
