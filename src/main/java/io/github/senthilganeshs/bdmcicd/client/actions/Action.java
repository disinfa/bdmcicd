package io.github.senthilganeshs.bdmcicd.client.actions;

import io.github.senthilganeshs.bdmcicd.ClientException;
import io.github.senthilganeshs.bdmcicd.client.DISClient;

public interface Action {
    Deploy export (final String appName, final String fileName, final String patchName, final String patchDesc) throws ClientException ;   

    Report report() throws ClientException ;
    
    
    public static Action create (final String query, final DISClient client) {
        return new DISExportAction(query, client);
    }
}