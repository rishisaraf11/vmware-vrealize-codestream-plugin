package com.vmware.vcac.code.stream.jenkins.plugin.util;

import com.vmware.vcac.code.stream.jenkins.plugin.model.PipelineParam;
import hudson.model.ParameterValue;
import hudson.model.ParametersAction;
import hudson.model.StringParameterValue;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static hudson.Util.fixEmptyAndTrim;

/**
 * Created by rsaraf on 4/22/2015.
 */
public class CodeStreamPluginHelper {

    private ParametersAction parametersAction;

    public CodeStreamPluginHelper(ParametersAction parametersAction) {
        this.parametersAction = parametersAction;
    }

    public String replaceBuildParamWithValue(String paramValue) {
        if (StringUtils.isNotBlank(paramValue) && paramValue.startsWith("$")) {
            ParameterValue parameter = parametersAction.getParameter(paramValue.replace("$", ""));
            if (parameter != null && parameter instanceof StringParameterValue) {
                return fixEmptyAndTrim(((StringParameterValue) parameter).value);
            }
        }
        return fixEmptyAndTrim(paramValue);
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
}
