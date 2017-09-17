package com.castsoftware.dmt.discoverer.jee.gradle;

import com.castsoftware.dmt.engine.discovery.BasicProjectsDiscovererAdapter;
import com.castsoftware.dmt.engine.discovery.IProjectsDiscovererUtilities;
import com.castsoftware.dmt.engine.foldertree.IMetadataInterpreter;
import com.castsoftware.dmt.engine.project.Profile.IReferencedContents;
import com.castsoftware.dmt.engine.project.Project;
import com.castsoftware.util.logger.Logging;

/**
 * Basic discoverer for
 */
public class JeeGradleDiscoverer extends BasicProjectsDiscovererAdapter
{
    private static final String GRADLE_FILE = "build.gradle";
    /**
     * Default constructor used by the discovery engine
     */
    public JeeGradleDiscoverer()
    {
    }

    @Override
    public boolean mustProcessFile(String fileName)
    {
        return fileName.endsWith(GRADLE_FILE);
    }

    @Override
    public void buildProject(String relativeFilePath, String content, Project project,
        IProjectsDiscovererUtilities projectsDiscovererUtilities)
    {
        Logging.info("cast.dmt.discover.jee.gradle.buildProjectStart", "PATH", relativeFilePath);
        Logging.info("cast.dmt.discover.jee.gradle.buildProjectEnd", "PATH", relativeFilePath);
    }

    @Override
    public boolean reparseProject(
        Project project,
        String projectContent,
        IReferencedContents contents,
        IProjectsDiscovererUtilities projectsDiscovererUtilities)
    {
        Logging.info("cast.dmt.discover.jee.gradle.reparseProjectStart", "PATH", project.getPath());
        Logging.info("cast.dmt.discover.jee.gradle.reparseProjectEnd", "PATH", project.getPath());
        return false;
    }

    @Override
    public void processProjectFile(String fileName, String content, IMetadataInterpreter metaDataInterpreter)
    {
        return;
    }
}
