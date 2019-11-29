package io.github.senthilganeshs.bdmcicd.client.query;

import io.github.senthilganeshs.bdmcicd.client.DISClient;
import io.github.senthilganeshs.bdmcicd.client.actions.Action;

public interface Query {
    Action execute();
    
    static Query createGitHubQuery(final String oauth, final String repoPath, final String commitHash, final DISClient disClient) {
        return new GitHubQuery(oauth, repoPath, commitHash, disClient);
    }
}