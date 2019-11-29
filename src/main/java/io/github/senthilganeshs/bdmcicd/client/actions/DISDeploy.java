package io.github.senthilganeshs.bdmcicd.client.actions;

import javax.json.JsonObject;

import io.github.senthilganeshs.bdmcicd.ClientException;
import io.github.senthilganeshs.bdmcicd.client.DISClient;

public final class DISDeploy implements Deploy {

    private final String fileName;
    private final DISClient disClient;
    private final JsonObject json;

    DISDeploy (final String fileName, final JsonObject json, final DISClient disClient) {
        this.fileName = fileName;
        this.json = json;
        this.disClient = disClient;
    }

    @Override
    public void deploy(final String appName, final String callbackURL) throws ClientException {
        disClient.deploy(fileName, appName, callbackURL);
    }

    @Override
    public Report report() throws ClientException {
        return new JsonReport(json);
    }
}