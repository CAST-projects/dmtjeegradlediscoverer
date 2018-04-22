package com.castsoftware.dmt.discoverer.jee.gradle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.castsoftware.dmt.engine.discovery.BasicProjectsDiscovererAdapter;
import com.castsoftware.dmt.engine.discovery.IProjectsDiscovererUtilities;
import com.castsoftware.dmt.engine.discovery.ProjectsDiscovererWrapper.ProfileOrProjectTypeConfiguration.LanguageConfiguration;
import com.castsoftware.dmt.engine.foldertree.IMetadataInterpreter;
import com.castsoftware.dmt.engine.project.Profile.IReferencedContents;
import com.castsoftware.dmt.engine.project.IResourceReadOnly;
import com.castsoftware.dmt.engine.project.Project;
import com.castsoftware.dmt.engine.validation.GradleProjectHelper;
import com.castsoftware.util.StringHelper;
import com.castsoftware.util.logger.Logging;

/**
 * Basic discoverer for
 */
public class JeeGradleDiscoverer extends BasicProjectsDiscovererAdapter
{
    private static final String GRADLE_FILE = "build.gradle";
    private static int javaLanguageId = -1;
    private static int javaContainerLanguageId = 1;
    //private static int javaXmlLanguageId = -1;
    //private static int javaPropertiesLanguageId = -1;
    private static int javaWebServerLanguageId = -1;
    //private static int javaWebContainerLanguageId = -1;
    private static int javaWebClientLanguageId = -1;
	//List<GradleProject> gradleProjects = new ArrayList<GradleProject>();
    private final Map<String, GradleProject> gradleProjects;
    private final Map<String, List<GradleProject>> gradleProjectsPerBasicIdentity;
    private Boolean parentProjectsSet = false;
	private final Set<GradleProject> gradleSettings;
	private final Map<String, GradleLibrary> gradleLibraries;


	/**
     * Default constructor used by the discovery engine
     */
    public JeeGradleDiscoverer()
    {
        gradleProjects = new HashMap<String, GradleProject>();
        gradleProjectsPerBasicIdentity = new HashMap<String, List<GradleProject>>();
        gradleSettings = new HashSet<GradleProject>();
        gradleLibraries = new HashMap<String, GradleLibrary>();
    }
    private void initLanguages(IProjectsDiscovererUtilities projectsDiscovererUtilities, Project project)
    {
    	if (javaLanguageId > 0)
    		return;
    	
        for (LanguageConfiguration languageConfiguration : projectsDiscovererUtilities.getProjectTypeConfiguration(project.getType()).getLanguageConfigurations())
        {
        	int languageId = languageConfiguration.getLanguageId();
            if ("JavaLanguage".equals(languageConfiguration.getLanguageName()))
            {
            	javaLanguageId = languageId;
            	//TODO: change API to access to the resource languages
            	//for (ResourceTypeConfiguration resourceTypeConfiguration : languageConfiguration.getResourceTypeConfigurations())
            	//{
            	//	if ("CHeaderLanguage".equals(resourceTypeConfiguration.getLanguageName()))
            	//		cHeaderLanguage = languageId;
            	//	}
            	//}
            	//javaContainerLanguageId = 1;
            	//javaXmlLanguageId = 2;
            	//javaPropertiesLanguageId = 3;
                continue;
            }
            else if ("JavaWebServerLanguage".equals(languageConfiguration.getLanguageName()))
            {
            	javaWebServerLanguageId = languageId;
            	//TODO: change API to access to the resource languages
            	//javaWebContainerLanguageId = 1;
                continue;
            }
            else if ("JavaWebClientLanguage".equals(languageConfiguration.getLanguageName()))
            {
            	javaWebClientLanguageId = languageId;
            	continue;
            }
        }
        if (javaLanguageId == -1) 
        {
            throw Logging.fatal("cast.dmt.discover.jee.gradle.missingLanguage","LNG","CLanguage");
        }
        if (javaWebServerLanguageId == -1)
        {
            throw Logging.fatal("cast.dmt.discover.jee.gradle.missingLanguage","LNG","CPlusPlusLanguage");
        }
        if (javaWebClientLanguageId == -1)
        {
            throw Logging.fatal("cast.dmt.discover.jee.gradle.missingLanguage","LNG","CFamilyNotCompilableLanguage");
        }	
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
        initLanguages(projectsDiscovererUtilities,project);
        
        
        if (relativeFilePath.endsWith("settings.gradle"))
        {
            Logging.detail("cast.dmt.discover.jee.gradle.deleteProject", "PATH", relativeFilePath);
            projectsDiscovererUtilities.deleteProject(project.getId());
        	GradleProject gradleProject = new GradleProject(relativeFilePath);
            if (!GradleProjectParser.parseSettings(gradleProjects, relativeFilePath, gradleProject, content))
            {
                Logging.error("cast.dmt.extend.discover.jee.maven.project.parseError", "FILE_PATH", relativeFilePath);
                Logging.info("cast.dmt.discover.jee.gradle.buildProjectEnd", "PATH", relativeFilePath);

                return;
            }
            else
            {
            	gradleSettings.add(gradleProject);
            }
        }
        else if (relativeFilePath.endsWith("deps.gradle"))
        {
            Logging.detail("cast.dmt.discover.jee.gradle.deleteProject", "PATH", relativeFilePath);
            projectsDiscovererUtilities.deleteProject(project.getId());
        	if (!GradleLibraryParser.parse(relativeFilePath, content, gradleLibraries))
	            Logging.error("cast.dmt.extend.discover.jee.maven.project.parseError", "FILE_PATH", relativeFilePath);
        	return;
        }
        else if (relativeFilePath.endsWith("jacoco.gradle"))
        {
            Logging.detail("cast.dmt.discover.jee.gradle.deleteProject", "PATH", relativeFilePath);
            projectsDiscovererUtilities.deleteProject(project.getId());
            return;
        }
        else
        {
        	GradleProject gradleProject = gradleProjects.get(relativeFilePath);
        	if (gradleProject == null)
        	{
        		gradleProject = new GradleProject(relativeFilePath);
        		gradleProjects.put(relativeFilePath, gradleProject);
        	}
        	if (!GradleProjectParser.parse(relativeFilePath, gradleProject, content))
	        {
	            Logging.error("cast.dmt.extend.discover.jee.maven.project.parseError", "FILE_PATH", relativeFilePath);
	
	            Logging.detail("cast.dmt.discover.jee.gradle.deleteProject", "PATH", relativeFilePath);
	
	            projectsDiscovererUtilities.deleteProject(project.getId());
	
	            Logging.info("cast.dmt.discover.jee.gradle.buildProjectEnd", "PATH", relativeFilePath);
	
	            return;
	        }
	        else
	        {
	        	if (!gradleProjects.containsKey(relativeFilePath))
	        	{
	        		// Records the Maven POM parent
		            recordProject(relativeFilePath, gradleProject);
	        	}
	            // Asks for a second parsing step of the project, once all the Gradle projects are available
	            project.addFileReference(relativeFilePath, Project.PROJECT_LANGUAGE_ID, IResourceReadOnly.RESOURCE_TYPE_NEUTRAL_ID);
	        }
        }

        Logging.info("cast.dmt.discover.jee.gradle.buildProjectEnd", "PATH", relativeFilePath);
    }
    private void recordProject(String projectPath, GradleProject gradleProject)
    {
        // Records the Maven project by path
        gradleProjects.put(projectPath, gradleProject);

        // Records the Maven project by basic identity
        // The basic identity does not need to be complete, in that case, it means that the POM parent will be retrieved by path
        String basicIdentity = gradleProject.getProjectPath();
        if (!StringHelper.isEmpty(basicIdentity))
        {
            List<GradleProject> basicIdentityGradleProjects = gradleProjectsPerBasicIdentity.get(basicIdentity);
            if (basicIdentityGradleProjects == null)
            {
                basicIdentityGradleProjects = new LinkedList<GradleProject>();

                gradleProjectsPerBasicIdentity.put(basicIdentity, basicIdentityGradleProjects);
            }

            basicIdentityGradleProjects.add(gradleProject);
        }
    }

    @Override
    public boolean reparseProject(
        Project project,
        String projectContent,
        IReferencedContents contents,
        IProjectsDiscovererUtilities projectsDiscovererUtilities)
    {
        Logging.info("cast.dmt.discover.jee.gradle.reparseProjectStart", "PATH", project.getPath());

        GradleProject gradleProject = gradleProjects.get(project.getId());
        assert gradleProject != null;

        // This third step was to expand the pattern references using validation engine, so nothing more to do
        if (project.getProjectReference(gradleProject.getProjectPath()) != null)
        {
            Logging.info("cast.dmt.discover.jee.gradle.finalyzeProjectsDone");

            project.removeResourceReference(gradleProject.getProjectPath());

            return false;
        }

        // Activates dependencies for gradle projects
        if (!parentProjectsSet)
        {
        	initProjectsInSettings();
            activateProjectsBuildchain();

            parentProjectsSet = true;
        }
        
        // Removes the second step flag reference
        project.removeResourceReference(gradleProject.getProjectPath());

        //TODO: write the project
        if (gradleProject.getApplyPlugins().contains("base") || gradleProject.getApplyPlugins().contains("ear") || gradleProject.getApplyPlugins().contains("jarDisabled"))
        {
        	projectsDiscovererUtilities.deleteProject(project.getId());
        }
        else
        {
        	if (!StringHelper.isEmpty(gradleProject.getName()))
        	{
        		project.setName(gradleProject.getName());
        		project.addMetadata("#!$?EXTREF#!$?", gradleProject.getName());
            	// setName
                // setMetadata
                // ...
        		if (gradleProject.getMainJavaSrcDirs().isEmpty())
        		{
        			// specific for CANOPI
        			if (gradleProject.getName().startsWith("src"))
        				gradleProject.addMainJavaSrcDir("src");
        			else
        				gradleProject.addMainJavaSrcDir("src/java");
        		}
        		for (String srcDir : gradleProject.getMainJavaSrcDirs())
        		{
        			String sourceDirectory = GradleProjectHelper.buildCanonicalPath(project.getPath() + "/" + srcDir);
        			project.addSourceDirectoryReference(sourceDirectory, javaLanguageId);
        		}
//        		for (GradleProject compileProject : gradleProject.getCompileProjects())
//        		{
//        			project.addProjectReference(compileProject.getName());
//        		}
        		for (String compileProjectRef : gradleProject.getCompileProjectRefs())
        		{
        			project.addProjectReference(compileProjectRef);
        		}
        		for (GradleLibrary compileLibraryRef : gradleProject.getCompileLibraries())
        		{
        			project.addDirectoryReference(compileLibraryRef.getName(), javaLanguageId, javaContainerLanguageId);
        		}
        	}
        }
        Logging.info("cast.dmt.discover.jee.gradle.reparseProjectEnd", "PATH", project.getPath());
        return false;
    }
    

    private void initProjectsInSettings()
    {
    	for (GradleProject gradleSetting : gradleSettings)
    	{
    		String rootSettings = gradleSetting.getProjectPath();
    		rootSettings = rootSettings.substring(0, rootSettings.length() - "settings.gradle".length());
        	for (GradleProject gradleProject : gradleProjects.values())
            {
        		if (gradleProject.getProjectPath().startsWith(rootSettings) && gradleProject.getProjectPath().endsWith("build.gradle"))
        		{
        			gradleProject.setMainRootPath(rootSettings);
        			String name = gradleProject.getProjectPath().substring(rootSettings.length());
        			if (name.equals("build.gradle"))
        			{
        				// top build is implicitely associated to the settings
        				gradleProject.setName("build.gradle");
        				gradleProject.isBuild(true);
        			}
        			else
        			{		
        				name = name.substring(0, name.length() - "build.gradle".length() - 1).replace("/", ":");
        				gradleProject.setName(name);
            			if (gradleSetting.getIncludedProjectRefs().contains(name))
            				gradleProject.setIsComputed(true);
        			}
        		}
            }
    	}
    }
    private void activateProjectsBuildchain()
    {
    	for (GradleProject gradleProject : gradleProjects.values())
        {
    		if (gradleProject.getMainRootPath() != null && gradleProject.getProjectPath().endsWith("build.gradle"))
    		{
    			for (String includedFile : gradleProject.getIncludedFiles())
    			{
    				GradleProject includedProject = gradleProjects.get(gradleProject.getMainRootPath() + includedFile);
    				if (includedProject != null)
    					gradleProject.includedProject(includedProject);
    			}
    			for (String compileProjectRef : gradleProject.getCompileProjectRefs())
    			{
    				//GradleProject compileProject = gradleProjects.get(gradleProject.getMainRootPath() + compileProjectRef);
    				GradleProject compileProject = gradleProjects.get(gradleProject.getMainRootPath() + getProjectPath(compileProjectRef));
    				if (compileProject != null)
    				{
//    					if (compileProject.getMainRootPath().equals(""))
//    					{
//	    					compileProject.setMainRootPath(gradleProject.getMainRootPath());
	    					//compileProject.setName(":" + compileProjectRef.substring(0,compileProjectRef.length() - 13).replace("/", ":"));
//	    					compileProject.setName(compileProjectRef);
	    					gradleProject.addCompileProject(compileProject);
//							activateProjectsChain(compileProject);
//    					}
    				}
    				else
    				{
    					gradleProject.addMissingProjectRef(compileProjectRef);
    				}
    			}
    			for (String compileLibraryRef : gradleProject.getCompileLibraryRefs())
    			{
    				GradleLibrary compileLibrary = gradleLibraries.get(compileLibraryRef.replace(".",":"));
    				if (compileLibrary != null)
    				{
    					gradleProject.addCompileLibrary(compileLibrary);
    				}
    				else
    				{
    					gradleProject.addMissingLibraryRef(compileLibraryRef);
    				}
    			}
    		}
        }
    }

    private void activateProjectsChain(GradleProject project)
    {
    	//if (project.getApplyPlugins().contains("ear") || project.getApplyPlugins().contains("war") || project.getApplyPlugins().contains("jar"))
    	//{
    	if (!project.isComputed())
    	{
    		// to avoid loop, set as true first
    		project.setIsComputed(true);
			for (String compileProjectRef : project.getCompileProjectRefs())
			{
				//String compileProjectPath = project.getMainRootPath() + compileProjectRef.replace(":", "/");
				String compileProjectPath = project.getMainRootPath() + getProjectPath(compileProjectRef);
				GradleProject compileProject = gradleProjects.get(compileProjectPath);
				if (compileProject != null)
				{
					compileProject.setMainRootPath(project.getMainRootPath());
					project.addCompileProject(compileProject);
					if (StringHelper.isEmpty(compileProject.getName()))
					{
						//compileProject.setName(":" + compileProjectRef.substring(0,compileProjectRef.length() - 13).replace("/", ":"));
						compileProject.setName(compileProjectRef);
						activateProjectsChain(compileProject);
					}
				}
				else
				{
					project.addMissingProjectRef(compileProjectRef);
				}
			}
 			
    	}
		//}
    }
    
    private void validateCompileProject(GradleProject project)
    {
    	if (project.getApplyPlugins().contains("ear") || project.getApplyPlugins().contains("war") || project.getApplyPlugins().contains("jar"))
    	{
			for (String compileProjectRef : project.getCompileProjectRefs())
			{
				//String compileProjectPath = project.getMainRootPath() + compileProjectRef.replace(":", "/");
				String compileProjectPath = project.getMainRootPath() + getProjectPath(compileProjectRef);
				GradleProject compileProject = gradleProjects.get(compileProjectPath);
				if (compileProject != null)
				{
					if (StringHelper.isEmpty(compileProject.getName()))
						compileProject.setName(compileProjectRef);
						//compileProject.setName(":" + compileProjectRef.substring(0,compileProjectRef.length() - 13).replace("/", ":"));
					project.addCompileProject(compileProject);
				}
			}    		
    	}
    }

    @Override
    public void processProjectFile(String fileName, String content, IMetadataInterpreter metaDataInterpreter)
    {
        return;
    }
    
    private String getProjectPath(String projectName)
    {
    	return projectName.replace(":", "/") + "/build.gradle";
    }

}
