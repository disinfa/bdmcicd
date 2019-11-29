package io.github.senthilganeshs.bdmcicd.client.actions;

import java.io.OutputStream;

import javax.json.JsonObject;

import io.github.senthilganeshs.bdmcicd.ClientException;

public interface Report {
    void render (final OutputStream os) throws ClientException ;
    
    public static Report create (final JsonObject json) {
        return new JsonReport(json);
    }
}
