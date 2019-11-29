package io.github.senthilganeshs.bdmcicd.client;

import javax.json.JsonObject;

import io.github.senthilganeshs.bdmcicd.ClientException;
import io.github.senthilganeshs.bdmcicd.client.http.DISHttpClient;

public interface DISClient extends AutoCloseable {

    JsonObject query (final String query) throws ClientException;

    JsonObject export(final String query, final String appName, final String fileName, final String patchName, final String patchDesc) throws ClientException;

    void deploy (final String fileName, final String appName, final String callbackURL) throws ClientException;

    void run (final String mappingName, final String appName, final String callbackURL) throws ClientException;
    

    public static DISClient create (final String hostName, final int port, final String userName, final String password, final String securityDomain, final String repoName, final String repoUser, final String repoPass) {
        return new DISHttpClient(hostName, port, userName, password, securityDomain, repoName, repoUser, repoPass);
    }
}