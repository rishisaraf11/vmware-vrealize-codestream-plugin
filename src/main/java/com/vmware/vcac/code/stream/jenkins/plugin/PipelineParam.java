package com.vmware.vcac.code.stream.jenkins.plugin;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.Serializable;

/**
 * Created by rsaraf on 3/23/2015.
 */
@ExportedBean
public class PipelineParam extends AbstractDescribableImpl<PipelineParam> implements Serializable {
    private String name;

    private String type="STRING";
    private String description="";
    private String value;

    @DataBoundConstructor
    public PipelineParam(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<PipelineParam> {

        @Override
        public String getDisplayName() {
            return "";
        }
    }
}
