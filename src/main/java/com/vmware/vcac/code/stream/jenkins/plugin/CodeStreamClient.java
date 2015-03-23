package com.vmware.vcac.code.stream.jenkins.plugin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by rsaraf on 3/23/2015.
 */
public class CodeStreamClient {
    private String serverUrl;
    private String token;
    private String CHECK_EXEC_STATUS = "%s/release-management-service/api/release-pipelines/%s/executions/%s";

    public CodeStreamClient(String serverUrl, String token) {
        this.serverUrl = serverUrl;
        this.token = token;
    }

    public ExecutionStatus getPipelineExecStatus(String pipelineId, String pipelineExecId) throws IOException {
        ExecutionStatus executionStatus = null;
        String url = String.format(CHECK_EXEC_STATUS, serverUrl, pipelineId, pipelineExecId);
        HttpResponse httpResponse = this.get(url);
        String responseAsJson = this.getResponseAsJsonString(httpResponse);
        JsonObject stringJsonAsObject = getJsonObject(responseAsJson);
        JsonElement executionInfoObj = stringJsonAsObject.get("executionInfo");
        if (executionInfoObj != null) {
            String executionStatusString = executionInfoObj.getAsJsonObject().get("status").getAsString();
            if (StringUtils.isNotBlank(executionStatusString)) {
                executionStatus = ExecutionStatus.fromValue(executionStatusString);
            }
        }
        return executionStatus;
    }

    public boolean isPipelineCompleted(String pipelineId, String pipelineExecId) throws IOException {
        ExecutionStatus pipelineExecStatus = this.getPipelineExecStatus(pipelineId, pipelineExecId);
        switch (pipelineExecStatus) {
            case COMPLETED:
            case FAILED:
            case CANCELED:
                return true;
            default:
                return false;
        }
    }

    private JsonObject getJsonObject(String responseAsJson) {
        JsonElement execResponseParse = new JsonParser().parse(responseAsJson);
        return execResponseParse.getAsJsonObject();
    }

    public HttpResponse get(String URL) throws IOException {
        CloseableHttpClient httpClient = null;
        try {
            httpClient = getHttpClient();
            HttpGet request = new HttpGet(URL);
            request.setHeader("accept", "application/json; charset=utf-8");
            if (StringUtils.isNotBlank(token)) {
                String authorization = "Bearer " + token;
                request.setHeader("Authorization", authorization);
            }
            return httpClient.execute(request);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }

    private CloseableHttpClient getHttpClient() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                builder.build());
        return HttpClients.custom().setSSLSocketFactory(
                sslsf).build();
    }


    private HttpResponse post(String URL, String payload, String token) throws IOException {
        CloseableHttpClient httpClient = null;
        try {
            httpClient = getHttpClient();
            HttpPost postRequest = new HttpPost(URL);
            StringEntity input = new StringEntity(payload);
            input.setContentType("application/json");
            postRequest.setEntity(input);
            postRequest.setHeader("Content-Type", "application/json");
            postRequest.setHeader("accept", "application/json; charset=utf-8");
            if (StringUtils.isNotBlank(token)) {
                String authorization = "Bearer " + token;
                postRequest.setHeader("Authorization", authorization);
            }
            return httpClient.execute(postRequest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getResponseAsJsonString(HttpResponse response) throws IOException {
        BufferedReader br = new BufferedReader(
                new InputStreamReader((response.getEntity().getContent())));

        StringBuilder output = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            output.append(line);
        }
        return output.toString();
    }

}

