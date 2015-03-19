package com.vmware.vcac.code.stream.jenkins.plugin;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * Sample {@link Builder}.
 * <p/>
 * <p/>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked
 * and a new {@link CodeStreamBuilder} is created. The created
 * instance is persisted to the project configuration XML by using
 * XStream, so this allows you to use instance fields (like {@link })
 * to remember the configuration.
 * <p/>
 * <p/>
 * When a build is performed, the {@link #perform(AbstractBuild, Launcher, BuildListener)}
 * method will be invoked.
 *
 * @author Rishi Saraf
 */
public class CodeStreamBuilder extends Builder {

    private String serverUrl;
    private String userName;
    private String password;
    private String tenant;
    private String pipelineName;

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public CodeStreamBuilder(String serverUrl, String userName, String password, String tenant, String pipelineName) {
        this.serverUrl = serverUrl;
        this.userName = userName;
        this.password = password;
        this.tenant = tenant;
        this.pipelineName = pipelineName;
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

    public String getPipelineName() {
        return pipelineName;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        PrintStream logger = listener.getLogger();
        SSLContextBuilder builder = new SSLContextBuilder();
        CloseableHttpClient httpClient = null;
        try {
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    builder.build());

            httpClient = HttpClients.custom().setSSLSocketFactory(
                    sslsf).build();

//            String identityTokenUrl = "https://blr-v144-ip-156.eng.vmware.com" + "/identity/api/tokens";
            String identityTokenUrl = this.serverUrl + "/identity/api/tokens";
            HttpPost postRequest  = new HttpPost(identityTokenUrl);
            String tokenPayload = String.format("{\"username\": \"%s\", \"password\": \"%s\", \"tenant\": \"%s\"}", userName, password, tenant);
//            StringEntity input = new StringEntity("{\"username\": \"fritz@coke.com\", \"password\": \"password\", \"tenant\": \"qe\"}");
            StringEntity input = new StringEntity(tokenPayload);

            input.setContentType("application/json");

            postRequest.setEntity(input);
            postRequest.setHeader("Content-Type", "application/json");
            HttpResponse response = httpClient.execute(postRequest);

            BufferedReader br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent())));

            StringBuilder output = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                output.append(line);
            }


            Gson gson = new Gson();
            JsonElement jsonElement = new JsonParser().parse(output.toString());
            JsonObject asJsonObject = jsonElement.getAsJsonObject();
            String token = asJsonObject.get("id").getAsString();

            logger.println("Token :" + token);

            String fetchPipeline = this.serverUrl + "/release-management-service/api/release-pipelines/?name=" + this.pipelineName ;

            HttpGet request = new HttpGet(fetchPipeline);
            request.setHeader("accept", "application/json; charset=utf-8");
            String autorization = "Bearer " + token;
            request.setHeader("Authorization", autorization);
            response = httpClient.execute(request);

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuffer resultBuffer = new StringBuffer();
            line = "";
            while ((line = rd.readLine()) != null) {
                resultBuffer.append(line);
            }

            logger.println("Pipeline :" + resultBuffer);

            jsonElement = new JsonParser().parse(resultBuffer.toString());
            String pipelineId = jsonElement.getAsJsonObject().get("content").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsString();


            // Execute pipeline
            CloseableHttpClient newClient =  HttpClients.custom().setSSLSocketFactory(
                    sslsf).build();
            String executePipelineUrl = this.serverUrl + "/release-management-service/api/release-pipelines/" + pipelineId + "/executions";
            HttpPost executePostRequest  = new HttpPost(executePipelineUrl);
            String payload = String.format("{\"description\": \"%s\"}", "Executed from jenkins");
            StringEntity inputEntity = new StringEntity(payload);
            inputEntity.setContentType("application/json");
            executePostRequest.setEntity(inputEntity);
            executePostRequest.setHeader("accept", "application/json; charset=utf-8");
            executePostRequest.setHeader("Authorization", autorization);

            newClient.execute(executePostRequest);


//            BufferedReader bre = new BufferedReader(
//                    new InputStreamReader((executeResponse.getEntity().getContent())));
//
//            StringBuilder outputE = new StringBuilder();
//            String linee;
//            while ((linee = bre.readLine()) != null) {
//                outputE.append(linee);
//            }
//
//            logger.println("Execution response :" + outputE);


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient.close();
        }





        return true;
    }

    /**
     * We'll use this from the <tt>config.jelly</tt>.
     */


    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    /**
     * Descriptor for {@link CodeStreamBuilder}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     * <p/>
     * <p/>
     * See <tt>src/main/resources/hudson/plugins/hello_world/HelloWorldBuilder/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

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
        private String pipelineName;

        /**
         * In order to load the persisted global configuration, you have to
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types 
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Execute CodeStream Pipeline";
        }

        @Override
        public boolean configure(StaplerRequest req, net.sf.json.JSONObject formData) throws FormException {
            // To persist global configuration information,
            // set that to properties and call save().
            // ^Can also use req.bindJSON(this, formData);
            //  (easier when there are many fields; need set* methods for this, like setUseFrench)
            serverUrl = formData.getString("serverUrl");
            tenant = formData.getString("tenant");
            userName = formData.getString("userName");
            password = formData.getString("password");
            pipelineName = formData.getString("pipelineName");
            save();
            return super.configure(req, formData);
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

        public String getPipelineName() {
            return pipelineName;
        }
    }
}

