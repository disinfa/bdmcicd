package io.github.senthilganeshs.bdmcicd;

public interface QueryFragment {
    void addObjectName (final String name);
    void addFolderPath (final String fp);
    void addProjectName (final String projectName);

    String build();
    
    public static QueryFragment create () {
        return new DISQueryFrag();
    }
}