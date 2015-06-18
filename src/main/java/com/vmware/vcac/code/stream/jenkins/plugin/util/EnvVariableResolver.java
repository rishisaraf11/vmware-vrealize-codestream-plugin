package com.vmware.vcac.code.stream.jenkins.plugin.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.vmware.vcac.code.stream.jenkins.plugin.model.PipelineParam;
import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;


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
        List<PipelineParam> temp = new ArrayList<PipelineParam>();
        if (pipelineParams != null) {
            for (PipelineParam param : pipelineParams) {
                try {
                    PipelineParam cloned = param.clone();
                    cloned.setName(replaceBuildParamWithValue(cloned.getName()));
                    cloned.setValue(replaceBuildParamWithValue(cloned.getValue()));
                    temp.add(cloned);
                } catch (CloneNotSupportedException e) {
                    new IOException("Not able to clone pipeline param");
                }
            }
        }
        return temp;
    }

    public EnvVars getEnvironment() {
        return environment;
    }
}
