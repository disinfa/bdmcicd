package io.github.senthilganeshs.bdmcicd.client.actions;

import io.github.senthilganeshs.bdmcicd.ClientException;

public  interface Deploy {
    void deploy (final String appName, final String callbackURL) throws ClientException ;

    Report report() throws ClientException ;
}