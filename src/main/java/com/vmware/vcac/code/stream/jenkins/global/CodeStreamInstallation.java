package com.vmware.vcac.code.stream.jenkins.global;

import hudson.Extension;
import hudson.tools.ToolDescriptor;
import hudson.tools.ToolInstallation;
import hudson.tools.ToolProperty;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

import java.util.List;

/**
 * Created by rsaraf on 3/18/2015.
 */
public class CodeStreamInstallation extends ToolInstallation {

    public CodeStreamInstallation(String name, String home, List<? extends ToolProperty<?>> properties) {
        super(name, home, properties);
    }

//    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends ToolDescriptor<CodeStreamInstallation> {

        /**
         * To persist global configuration information,
         * simply store it in a field and call save().
         * <p/>
         * <p/>
         * If you don't want fields to be persisted, use <tt>transient</tt>.
         */
        private String serverUrl;
        private String userName;
        private String password;
        private String tenant;

        /**
         * In order to load the persisted global configuration, you have to
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            setInstallations();
            load();
        }


        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "VMware CodeStream";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            // To persist global configuration information,
            // set that to properties and call save().
            // ^Can also use req.bindJSON(this, formData);
            //  (easier when there are many fields; need set* methods for this, like setUseFrench)
            JSONObject json = formData.getJSONObject("codeStream");
            serverUrl = json.getString("serverUrl");
            tenant = json.getString("tenant");
            userName = json.getString("userName");
            password = json.getString("password");
            save();
            return super.configure(req, formData);
        }

        @Override
        public CodeStreamInstallation newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            return (CodeStreamInstallation) super.newInstance(req, formData.getJSONObject("codeStreamInstallation"));
        }

        public String getServerUrl() {
            return serverUrl;
        }

        public String getUserName() {
            return userName;
        }

        public String getPassword() {
            return password;
        }

        public String getTenant() {
            return tenant;
        }
    }
}
