package io.github.senthilganeshs.bdmcicd.client.actions;

import java.io.OutputStream;
import java.util.Collections;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

import io.github.senthilganeshs.bdmcicd.ClientException;

public final class JsonReport implements Report {

    private JsonObject report;

    JsonReport (final JsonObject report) {
        this.report = report;
    }

    @Override
    public void render(OutputStream os) throws ClientException {
        JsonWriterFactory writerFactory = Json.createWriterFactory(Collections.singletonMap(JsonGenerator.PRETTY_PRINTING, true));
        JsonWriter jsonWriter = writerFactory.createWriter(os);
        jsonWriter.writeObject(report);
        jsonWriter.close();
    }            
}