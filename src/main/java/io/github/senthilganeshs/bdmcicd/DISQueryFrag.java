package io.github.senthilganeshs.bdmcicd;

public final class DISQueryFrag implements QueryFragment {
    private final String fullQuery = 
        "name ~in~ (%s) where project ~in~ (%s), folder ~in~ (%s)";

    private final String queryWithProject = 
        "name ~in~ (%s) where project ~in~ (%s)";

    private final String queryWithFolder = 
        "name ~in~ (%s) where folder ~in~ (%s)";

    private final String queryWithName = 
        "name ~in~ (%s)";

    private final String queryAll = "all";

    private String folders;

    private String projects;

    private String names;

    DISQueryFrag () {
        this.names = "";
        this.projects = "";
        this.folders = "";
    }

    @Override
    public void addObjectName(final String name) {
        if (names.isEmpty())
            names = name;
        else 
            names += "," + name;
    }

    @Override
    public void addFolderPath(String fp) {
        if (folders.isEmpty()) 
            folders = "/" + "," + fp;
        else {
            if (!folders.contains(fp))
                folders += "," + fp;
        }
    }

    @Override
    public void addProjectName(String projectName) {
        if (projects.isEmpty())
            projects = projectName;
        else {
            if (!projects.contains(projectName))
                projects += "," + projectName;
        }
    }

    @Override
    public String build() {
        if (names.isEmpty())
            return queryAll;
        if (folders.isEmpty() && projects.isEmpty()) {
            return String.format(queryWithName, names);
        } else if (folders.isEmpty()) {
            return String.format(queryWithProject, names, projects);
        } else if (projects.isEmpty()) {
            return String.format(queryWithFolder, names, folders);
        }
        return String.format(fullQuery, names, projects, folders);
    }
}