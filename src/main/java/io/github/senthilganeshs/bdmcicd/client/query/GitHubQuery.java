package io.github.senthilganeshs.bdmcicd.client.query;

import java.io.IOException;

import javax.json.JsonArray;

import com.jcabi.github.Coordinates;
import com.jcabi.github.RtGithub;

import io.github.senthilganeshs.bdmcicd.QueryFragment;
import io.github.senthilganeshs.bdmcicd.client.DISClient;
import io.github.senthilganeshs.bdmcicd.client.actions.Action;

public final class GitHubQuery implements Query {
    private final QueryFragment queryBuilder;
    private final DISClient disClient;

    GitHubQuery (
        final String oauth, 
        final String repoPath, 
        final String commitHash,
        final DISClient disClient) {

        this.disClient = disClient;
        this.queryBuilder = QueryFragment.create();

        try {
            final JsonArray files =
                new RtGithub(oauth).repos()
                .get(
                    new Coordinates.Simple(repoPath))
                .commits()
                .get(commitHash)
                .json()
                .getJsonArray("files");

            final int size = files.size();
            for (int i = 0; i < size; i ++) {
                consume(files.getJsonObject(i).getJsonString("filename").getString());
            }
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
        }                
    }

    @Override
    public Action execute() {
        return Action.create(queryBuilder.build(), disClient);
    }

    private void consume (final String fileName) {
        String[] splits = fileName.split("/");
        if (splits == null || splits.length == 0 || splits.length == 1)
            return;
        queryBuilder.addProjectName(splits[1]);
        String fp = "";
        for (int i = 2; i < splits.length - 2; i ++) {
            fp += "/" + splits[i];
        }
        if (!fp.isEmpty())
            queryBuilder.addFolderPath(fp);
        final String name = splits[splits.length - 1];
        queryBuilder.addObjectName(name.substring(0, name.lastIndexOf(".")));
    }            
} 