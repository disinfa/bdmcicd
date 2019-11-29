package io.github.senthilganeshs.bdmcicd.client.actions;

import io.github.senthilganeshs.bdmcicd.ClientException;
import io.github.senthilganeshs.bdmcicd.client.DISClient;

public  final class DISExportAction implements Action {

    private final String query;
    private final DISClient disClient;

    DISExportAction (
        final String query, 
        final DISClient disClient) {

        System.out.println("Query = " + query);
        this.query = query;
        this.disClient = disClient;
    }

    @Override
    public Deploy export(final String appName, final String fileName, final String patchName, final String patchDesc) throws ClientException {
        return new DISDeploy(
            fileName, 
            disClient.export(query, appName, fileName, patchName, patchDesc),
            disClient);
    }

    @Override
    public Report report() throws ClientException {
        return new JsonReport(disClient.query(query));
    }
}