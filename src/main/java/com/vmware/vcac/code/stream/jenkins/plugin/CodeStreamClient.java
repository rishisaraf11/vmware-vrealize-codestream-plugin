package com.vmware.vcac.code.stream.jenkins.plugin;

import com.google.gson.*;
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
import java.util.List;

/**
 * Created by rsaraf on 3/23/2015.
 */
public class CodeStreamClient {
    private String token;
    private String CODESTREAM_API_URL = "";
    private String FETCH_TOKEN = "";
    private String CHECK_EXEC_STATUS = "";
    private String FETCH_PIPELINE = "";
    private String EXECUTE_PIPELINE = "";
    private String TOKEN_JSON = "{\"username\": \"%s\", \"password\": \"%s\", \"tenant\": \"%s\"}";
    private String EXEC_PAYLOAD = "{\"description\": \"%s\", \"pipelineParams\": %s}";

    public CodeStreamClient(String serverUrl, String userName, String password, String tenant) throws IOException {
        this.FETCH_TOKEN =  serverUrl +  "/identity/api/tokens";
        this.CODESTREAM_API_URL =   serverUrl + "/release-management-service/api/release-pipelines/";
        this.FETCH_PIPELINE = CODESTREAM_API_URL + "?name=%s";
        this.EXECUTE_PIPELINE = CODESTREAM_API_URL + "%s/executions";
        this.CHECK_EXEC_STATUS = CODESTREAM_API_URL + "%s/executions/%s";
        this.token = populateToken(userName, password, tenant);
    }

    private String populateToken(String userName, String password, String tenant) throws IOException {
        String tokenPayload = String.format(TOKEN_JSON, userName, password, tenant);
        HttpResponse httpResponse = this.post(FETCH_TOKEN, tokenPayload);
        String responseAsJson = this.getResponseAsJsonString(httpResponse);
        JsonObject stringJsonAsObject = getJsonObject(responseAsJson);
        JsonElement idElement = stringJsonAsObject.get("id");
        if (idElement == null) {
            handleError(stringJsonAsObject);
        }  else {
            token = idElement.getAsString();
        }
        return token;
    }

    public JsonObject fetchPipeline(String pipelineName) throws IOException {
        JsonObject response = null;
        String url = String.format(FETCH_PIPELINE, pipelineName);
        HttpResponse pipelineResponse = get(url);
        String responseAsJson = this.getResponseAsJsonString(pipelineResponse);
        JsonObject stringJsonAsObject = getJsonObject(responseAsJson);
        JsonElement contentElement = stringJsonAsObject.get("content");
        if (contentElement == null) {
            handleError(stringJsonAsObject);
        } else {
            JsonArray contents = contentElement.getAsJsonArray();
            if (contents.size() == 1) {
                response = contents.get(0).getAsJsonObject();
            } else {
                if (contents.size() > 1) {
                    throw new IOException("More than one pipeline with name " + pipelineName + " found");
                } else if (contents.size() < 1) {
                    throw new IOException("Pipeline with name " + pipelineName + " not found");
                }
            }
        }
        return response;
    }

    public JsonObject executePipeline(String pipelineId, List<PipelineParam> pipelineParams) throws IOException {
        JsonObject response = null;
        String url = String.format(EXECUTE_PIPELINE, pipelineId);
        Gson gson = new Gson();
        String pipelineParamsArray = gson.toJson(pipelineParams);

        String payload = String.format("{\"description\": \"%s\", \"pipelineParams\": %s}", "Executed from jenkins", pipelineParamsArray);
        HttpResponse httpResponse = this.post(url, payload);
        String responseAsJson = this.getResponseAsJsonString(httpResponse);
        response = getJsonObject(responseAsJson);
        return response;
    }

    public ExecutionStatus getPipelineExecStatus(String pipelineId, String pipelineExecId) throws IOException {
        ExecutionStatus executionStatus = null;
        String url = String.format(CHECK_EXEC_STATUS, pipelineId, pipelineExecId);
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


    private HttpResponse post(String URL, String payload) throws IOException {
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

    private void handleError(JsonObject asJsonObject) throws IOException {
        JsonElement errorElement = asJsonObject.get("errors");
        if (errorElement != null) {
            JsonObject errorElJsonObj = errorElement.getAsJsonArray().get(0).getAsJsonObject();
            JsonElement messageEle = errorElJsonObj.get("systemMessage");
            if (messageEle == null) {
                messageEle =  errorElJsonObj.get("message");
            }
            String systemErrorMessage = messageEle.toString();
            throw new IOException(systemErrorMessage);
        }
    }

}

