package io.github.senthilganeshs.bdmcicd.client.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;

import javax.json.Json;
import javax.json.JsonObject;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import io.github.senthilganeshs.bdmcicd.ClientException;
import io.github.senthilganeshs.bdmcicd.client.DISClient;

public final class DISHttpClient implements DISClient {

    private final CloseableHttpClient httpClient;
    private final String objectsURL;
    private final String repositoryPass;
    private final String repositoryUser;
    private final String repositoryName;
    private final String securityDomain;
    private final String password;
    private final String userName;
    private final String disPrefix;
   
    public DISHttpClient (
        final String hostName,
        final int disHttpPort,
        final String userName,
        final String password,
        final String securityDomain,
        final String repositoryName,
        final String repositoryUser,
        final String repositoryPass) {

        
        this.disPrefix =  String.format(
            "http://%s:%d/DataIntegrationService/modules/core/v1", 
            hostName,
            disHttpPort);
        
        this.objectsURL = disPrefix + "/objects";
        
        this.userName = userName;
        this.password = password;
        this.securityDomain = securityDomain;
        this.repositoryName = repositoryName;
        this.repositoryUser = repositoryUser;
        this.repositoryPass = repositoryPass;
        
        this.httpClient = HttpClients.createDefault();
    }
    
    private static String encodeSpaces (final String text) {
        return text.replace(' ', '+');
    }

    @Override
    public JsonObject query(final String query) throws ClientException {
        final String encodedURL = objectsURL + encodeSpaces("?query=" + query);
        HttpGet get = new HttpGet(encodedURL);
        get.addHeader("username", userName);
        get.addHeader("encryptedpassword", password);
        get.addHeader("securitydomain", securityDomain);
        get.addHeader("repositoryservice", repositoryName);
        get.addHeader("repositoryusername", repositoryUser);
        get.addHeader("repositoryencryptedpassword", repositoryPass);
        get.addHeader("Content-Type", "application/json");
        
        try (InputStream in = httpClient.execute(get).getEntity().getContent()) {
            return Json.createReader(
                new StringReader(
                    IOUtils.toString(in, Charset.defaultCharset())
                )
            ).readObject();
        } catch (UnsupportedOperationException | IOException e) {
            throw new ClientException(e);
        }
    }

    @Override
    public JsonObject export(final String query, final String appName, final String fileName, final String patchName, final String patchDesc) throws ClientException {
        final String encodedURL = objectsURL + encodeSpaces("?query=" + query);
        HttpPost post = new HttpPost (encodedURL);
        post.addHeader("accept", "application/json");
        post.addHeader("username", userName);
        post.addHeader("encryptedpassword", password);
        post.addHeader("securitydomain", securityDomain);
        post.addHeader("Content-Type", "application/json");
        
        StringBuilder body = new StringBuilder();
        body.append("{");
        body.append(String.format("{" + 
            "  \"action\": \"DeployObjectsToFile\"," + 
            "  \"deployObjToFilePayload\": {" + 
            "    \"patchName\": \"%s\"," + 
            "    \"patchDescription\": \"%s\"," + 
            "    \"applicationName\": \"%s\"," + 
            "    \"filePath\": \"%s\""
            + "}",
            patchName,
            patchDesc,
            appName,
            fileName));
        
        try (final InputStream in = httpClient.execute(post).getEntity().getContent()) {
            return Json.createReader(
                new StringReader(
                    IOUtils.toString(in, Charset.defaultCharset())
                )
            ).readObject();
        } catch (UnsupportedOperationException | IOException e) {
            throw new ClientException(e);
        }
    }

    @Override
    public void deploy(final String fileName, final String appName, final String callbackURL) throws ClientException {
        
        final String deployURL = disPrefix + String.format("/applications/%s", appName);
        
        HttpPost post = new HttpPost (deployURL);
        post.addHeader("accept", "application/json");
        post.addHeader("username", userName);
        post.addHeader("encryptedpassword", password);
        post.addHeader("securitydomain", securityDomain);
        post.addHeader("Content-Type", "application/json");
        
        StringBuilder body = new StringBuilder();
        body.append("{");
        if (callbackURL == null || callbackURL.isEmpty()) {
            body.append(String.format("{" + 
                "  \"filePath\": \"%s\""  + 
                "}",
                fileName));
        } else {
            body.append(String.format("{" + 
                "  \"filePath\": \"%s\"," + 
                "  \"httpCallbackUrl\": \"%s\"" + 
                "}",
                fileName,
                callbackURL));
        }
        
        try (final InputStream in = httpClient.execute(post).getEntity().getContent()) {
            Json.createReader(
                new StringReader(
                    IOUtils.toString(in, Charset.defaultCharset())
                )
            ).readObject();
        } catch (UnsupportedOperationException | IOException e) {
            throw new ClientException(e);
        }
    }

    @Override
    public void run(final String mappingName, final String appName, final String callbackURL) {
        //TBD
    }

    @Override
    public void close() throws Exception {
        httpClient.close();
    }
}