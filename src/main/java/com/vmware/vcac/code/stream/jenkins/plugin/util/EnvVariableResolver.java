package com.vmware.vcac.code.stream.jenkins.plugin.util;

import com.vmware.vcac.code.stream.jenkins.plugin.model.PipelineParam;
import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static hudson.Util.fixEmptyAndTrim;

/**
 * Created by rsaraf on 4/22/2015.
 */
public class EnvVariableResolver {

    private EnvVars environment;


    public EnvVariableResolver(AbstractBuild<?, ?> build, BuildListener listener) throws IOException, InterruptedException {
        environment = build.getEnvironment(listener);
        environment.overrideAll(build.getBuildVariables());
    }

    public String replaceBuildParamWithValue(String paramValue) {
        return fixEmptyAndTrim(environment.expand(paramValue));
    }

    public List<PipelineParam> replaceBuildParamWithValue(List<PipelineParam> pipelineParams) {
        if (pipelineParams != null) {
            for (PipelineParam param : pipelineParams) {
                param.setName(replaceBuildParamWithValue(param.getName()));
                param.setValue(replaceBuildParamWithValue(param.getValue()));
            }
        } else {
            pipelineParams = new ArrayList<PipelineParam>();
        }

        return pipelineParams;
    }

    public EnvVars getEnvironment() {
        return environment;
    }
}
