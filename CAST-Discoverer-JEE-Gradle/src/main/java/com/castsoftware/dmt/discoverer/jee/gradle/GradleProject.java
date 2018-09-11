package com.castsoftware.dmt.discoverer.jee.gradle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.castsoftware.dmt.engine.project.Project;

/**
 *
 */
public class GradleProject {
	private String name;
    private String baseName;
    private String appendix;
    private String version;
    private String classifier;
    private String extension;
    private String archiveName;
	private String projectPath;
	private String mainRootPath;
	private final Set<String> mainJavaSrcDirs;
	private String mainResourcesSrcDir;
	private String testJavaSrcDir;
	private String testResourcesSrcDir;
	private Boolean isParsed;
	private Boolean isBuild;
	private final Set<String> compileProjectRefs;
	private final Set<GradleProject> compileProjects;
	private final Set<String> compileLibraryRefs;
	private final Set<GradleLibrary> compileLibraries;
	private final Set<String> missingProjectRefs;
	private final Set<String> missingLibraryRefs;
	private Boolean isComputed = false;
	private final Set<String> includedProjectRefs;
	private final Set<GradleProject> includedProjects;
	private final List<String> applyPlugins;
	private Boolean basePlugin = false;
	private final List<String> includedFiles;


    /**
     * @param projectPath
     */
	public GradleProject(String projectPath) {
		compileProjectRefs = new HashSet<String>();
		compileProjects = new HashSet<GradleProject>();
		compileLibraryRefs = new HashSet<String>();
		compileLibraries = new HashSet<GradleLibrary>();
		missingProjectRefs = new HashSet<String>();
		missingLibraryRefs = new HashSet<String>();
		applyPlugins = new ArrayList<String>();
		includedFiles = new ArrayList<String>();
		includedProjectRefs = new HashSet<String>();
		includedProjects = new HashSet<GradleProject>();
        setProjectPath(projectPath);
		isParsed = false;
		isBuild = false;
		mainJavaSrcDirs = new HashSet<String>();
	}

    /**
     * @param mainRootPath
     * @param name
     * @param projectPath
     */
	public GradleProject(String mainRootPath, String name, String projectPath) {
		compileProjectRefs = new HashSet<String>();
		compileProjects = new HashSet<GradleProject>();
		compileLibraryRefs = new HashSet<String>();
		compileLibraries = new HashSet<GradleLibrary>();
		missingProjectRefs = new HashSet<String>();
		missingLibraryRefs = new HashSet<String>();
		applyPlugins = new ArrayList<String>();
		includedFiles = new ArrayList<String>();
		includedProjectRefs = new HashSet<String>();
		includedProjects = new HashSet<GradleProject>();
        setMainRootPath(mainRootPath);
        setName(name);
        setProjectPath(projectPath);
		isParsed = false;
		isBuild = false;
		mainJavaSrcDirs = new HashSet<String>();
	}

    /**
     * @return
     */
	public String getName() {
		return name;
	}

    /**
     * @param name
     */
	public void setName(String name) {
		this.name = name;
	}

    /**
     * @param project
     */
	public void fillProject(Project project)
	{
    	//project.addMetadata("pchGenerator", pchGeneratorPath);
        //project.setName(name);
        //project.addMetadata(IResourceReadOnly.METADATA_REFKEY, name);
        //if (project.getOption(macroName) == null)
        //    project.addOption(macroName, macroValue);
        //if (project.getMetadata(macroName) == null)
        //    project.addMetadata(macroName, macroValue);
        //if (project.getFileReference(fileRef) == null)
        //{
        //    project.addSourceFileReference(fileRef, languageId);
        //}
        //if (project.getDirectoryReference(directoryRef) == null)
        //    project.addDirectoryReference(directoryRef, languageId, resourceTypeId);
        //}
        //if (project.getDirectoryReference(directoryRef) == null)
        //    project.addDirectoryReference(directoryRef, languageId, resourceTypeId);
        //}
		return;
	}

    /**
     * @return
     */
	public Set<String> getMainJavaSrcDirs() {
		return mainJavaSrcDirs;
	}

    /**
     * @param mainJavaSrcDir
     */
	public void addMainJavaSrcDir(String mainJavaSrcDir) {
		mainJavaSrcDirs.add(mainJavaSrcDir);
	}

    /**
     * @return
     */
	public String getMainResourcesSrcDir() {
		return mainResourcesSrcDir;
	}

    /**
     * @param mainResourcesSrcDir
     */
	public void setMainResourcesSrcDir(String mainResourcesSrcDir) {
		this.mainResourcesSrcDir = mainResourcesSrcDir;
	}

    /**
     * @return
     */
	public String getTestJavaSrcDir() {
		return testJavaSrcDir;
	}

    /**
     * @param testJavaSrcDir
     */
	public void setTestJavaSrcDir(String testJavaSrcDir) {
		this.testJavaSrcDir = testJavaSrcDir;
	}

    /**
     * @return
     */
	public String getTestResourcesSrcDir() {
		return testResourcesSrcDir;
	}

    /**
     * @param testResourcesSrcDir
     */
	public void setTestResourcesSrcDir(String testResourcesSrcDir) {
		this.testResourcesSrcDir = testResourcesSrcDir;
	}

    /**
     * @return
     */
	public String getProjectPath() {
		return projectPath;
	}

    /**
     * @param projectPath
     */
	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}

    /**
     * @return
     */
	public Boolean getIsParsed() {
		return isParsed;
	}

    /**
     * @param isParsed
     */
	public void setIsParsed(Boolean isParsed) {
		this.isParsed = isParsed;
	}

    /**
     * @return
     */
	public Set<String> getCompileProjectRefs() {
		return compileProjectRefs;
	}

    /**
     * @param compileProject
     */
	public void addCompileProjectRef(String compileProject) {
		compileProjectRefs.add(compileProject);
	}

    /**
     * @return
     */
	public Set<String> getCompileLibraryRefs() {
		return compileLibraryRefs;
	}

    /**
     * @param compileLibrary
     */
	public void addCompileLibraryRef(String compileLibrary) {
		compileLibraryRefs.add(compileLibrary);
	}

    /**
     * @return
     */
	public List<String> getApplyPlugins() {
		return applyPlugins;
	}

    /**
     * @param applyPlugin
     */
	public void addApplyPlugins(String applyPlugin) {
		applyPlugins.add(applyPlugin);
		if ("base".equals(applyPlugin))
		{
			basePlugin = true;
			int pos = projectPath.indexOf("build.gradle");
			if (pos > 0)
				mainRootPath = projectPath.substring(0, pos);
		}
        else if ("java".equals(applyPlugin))
        {
            if (name == null || name.endsWith(".gradle"))
            {
                int lastIndex = projectPath.lastIndexOf("/");
                if (lastIndex > 0)
                {
                    try
                    {
                        String projectName = projectPath.substring(0, lastIndex);
                        lastIndex = projectName.lastIndexOf("/");
                        if (lastIndex > 0)
                            setName(projectName.substring(lastIndex + 1));
                        else
                            setName(projectName);
                    }
                    catch (IndexOutOfBoundsException e)
                    {
                        // unable to define a name
                    }
                }
                else
                {
                    // TODO: specific case for which the gradle file is directly under the root
                    setName("xxx");
                }
            }
        }

	}

    /**
     * @param applyPlugin
     */
	public void removeApplyPlugins(String applyPlugin) {
		applyPlugins.remove(applyPlugin);
	}

    /**
     * @return
     */
	public Boolean isBasePlugin() {
		return basePlugin;
	}

    /**
     * @return
     */
	public Set<String> getIncludedProjectRefs() {
		return includedProjectRefs;
	}

    /**
     * @param includedProject
     */
	public void addIncludedProjectRef(String includedProject) {
		includedProjectRefs.add(includedProject);
	}

    /**
     * @return
     */
	public List<String> getIncludedFiles() {
		return includedFiles;
	}

    /**
     * @param includedFile
     */
	public void addIncludedFiles(String includedFile) {
		includedFiles.add(includedFile);
	}

    /**
     * @param includedProject
     */
	public void includedProject(GradleProject includedProject) {
		for (String compileProject : includedProject.getCompileProjectRefs())
			compileProjectRefs.add(compileProject);
		//TODO: transfer all infos
	}

    /**
     * @param includedProject
     */
	public void addIncludedProject(GradleProject includedProject) {
		includedProjects.add(includedProject);
	}

    /**
     * @return
     */
	public Set<GradleProject> getIncludedProjects() {
		return includedProjects;
	}

    /**
     * @return
     */
	public String getMainRootPath() {
		return mainRootPath;
	}

    /**
     * @param mainRootPath
     */
	public void setMainRootPath(String mainRootPath) {
		this.mainRootPath = mainRootPath;
	}

    /**
     * @return
     */
	public Set<GradleProject> getCompileProjects() {
		return compileProjects;
	}

    /**
     * @param compileProject
     */
	public void addCompileProject(GradleProject compileProject) {
		compileProjects.add(compileProject);
	}

    /**
     * @return
     */
	public Set<GradleLibrary> getCompileLibraries() {
		return compileLibraries;
	}

    /**
     * @param compileLibrary
     */
	public void addCompileLibrary(GradleLibrary compileLibrary) {
		compileLibraries.add(compileLibrary);
	}

    /**
     * @return
     */
	public Boolean isComputed() {
		return isComputed;
	}

    /**
     * @param isComputed
     */
	public void setIsComputed(Boolean isComputed) {
		this.isComputed = isComputed;
	}

    /**
     * @return
     */
	public Set<String> getMissingProjectRefs() {
		return missingProjectRefs;
	}

    /**
     * @param compileProjectRef
     */
	public void addMissingProjectRef(String compileProjectRef) {
		missingProjectRefs.add(compileProjectRef);
	}

    /**
     * @return
     */
	public Set<String> getMissingLibraryRefs() {
		return missingLibraryRefs;
	}

    /**
     * @param compileLibraryRef
     */
	public void addMissingLibraryRef(String compileLibraryRef) {
		missingLibraryRefs.add(compileLibraryRef);
	}

    /**
     * @return
     */
	public Boolean isBuild() {
		return isBuild;
	}

    /**
     * @param isBuild
     */
	public void isBuild(Boolean isBuild) {
		this.isBuild = isBuild;
	}

    /**
     * @return
     */
    public String getBaseName()
    {
        return baseName;
    }

    /**
     * @param baseName
     */
    public void setBaseName(String baseName)
    {
        this.baseName = baseName;
    }

    /**
     * @return
     */
    public String getAppendix()
    {
        return appendix;
    }

    /**
     * @param appendix
     */
    public void setAppendix(String appendix)
    {
        this.appendix = appendix;
    }

    /**
     * @return
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * @param version
     */
    public void setVersion(String version)
    {
        this.version = version;
    }

    /**
     * @return
     */
    public String getClassifier()
    {
        return classifier;
    }

    /**
     * @param classifier
     */
    public void setClassifier(String classifier)
    {
        this.classifier = classifier;
    }

    /**
     * @return
     */
    public String getExtension()
    {
        if (extension != null)
            return extension;
        return "jar";
    }

    /**
     * @param extension
     */
    public void setExtension(String extension)
    {
        this.extension = extension;
    }

    /**
     * @return
     */
    public String getArchiveName()
    {
        if (archiveName != null)
            return archiveName;
        String name = getBaseName();
        if (getAppendix() != null)
            name += "-" + getAppendix();
        if (getVersion() != null)
            name += "-" + getVersion();
        if (getClassifier() != null)
            name += "-" + getClassifier();
        return name + "." + getExtension();

    }

    /**
     * @param archiveName
     */
    public void setArchiveName(String archiveName)
    {
        this.archiveName = archiveName;
    }
}
