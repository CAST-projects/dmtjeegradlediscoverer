package com.castsoftware.dmt.discoverer.jee.gradle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.castsoftware.dmt.engine.project.Project;

public class GradleProject {
	private String name;
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
	private List<String> applyPlugins;
	private Boolean basePlugin = false;
	private List<String> includedFiles;
	
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
		this.projectPath = projectPath;
		this.isParsed = false;
		this.isBuild = false;
		mainJavaSrcDirs = new HashSet<String>();
	}
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
		this.mainRootPath = mainRootPath;
		this.name = name;
		this.projectPath = projectPath;
		this.isParsed = false;
		this.isBuild = false;
		mainJavaSrcDirs = new HashSet<String>();
	}
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
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

	public Set<String> getMainJavaSrcDirs() {
		return mainJavaSrcDirs;
	}

	public void addMainJavaSrcDir(String mainJavaSrcDir) {
		mainJavaSrcDirs.add(mainJavaSrcDir);
	}

	public String getMainResourcesSrcDir() {
		return mainResourcesSrcDir;
	}

	public void setMainResourcesSrcDir(String mainResourcesSrcDir) {
		this.mainResourcesSrcDir = mainResourcesSrcDir;
	}

	public String getTestJavaSrcDir() {
		return testJavaSrcDir;
	}

	public void setTestJavaSrcDir(String testJavaSrcDir) {
		this.testJavaSrcDir = testJavaSrcDir;
	}

	public String getTestResourcesSrcDir() {
		return testResourcesSrcDir;
	}

	public void setTestResourcesSrcDir(String testResourcesSrcDir) {
		this.testResourcesSrcDir = testResourcesSrcDir;
	}
	public String getProjectPath() {
		return projectPath;
	}
	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}
	public Boolean getIsParsed() {
		return isParsed;
	}
	public void setIsParsed(Boolean isParsed) {
		this.isParsed = isParsed;
	}
	public Set<String> getCompileProjectRefs() {
		return compileProjectRefs;
	}
	public void addCompileProjectRef(String compileProject) {
		compileProjectRefs.add(compileProject);
	}
	public Set<String> getCompileLibraryRefs() {
		return compileLibraryRefs;
	}
	public void addCompileLibraryRef(String compileLibrary) {
		compileLibraryRefs.add(compileLibrary);
	}
	public List<String> getApplyPlugins() {
		return applyPlugins;
	}
	public void addApplyPlugins(String applyPlugin) {
		this.applyPlugins.add(applyPlugin);
		if ("base".equals(applyPlugin))
		{
			basePlugin = true;
			int pos = projectPath.indexOf("build.gradle");
			if (pos > 0)
				mainRootPath = projectPath.substring(0, pos);
		}
	}
	public void removeApplyPlugins(String applyPlugin) {
		this.applyPlugins.remove(applyPlugin);
	}
	public Boolean isBasePlugin() {
		return basePlugin;
	}
	public Set<String> getIncludedProjectRefs() {
		return includedProjectRefs;
	}
	public void addIncludedProjectRef(String includedProject) {
		includedProjectRefs.add(includedProject);
	}
	public List<String> getIncludedFiles() {
		return includedFiles;
	}
	public void addIncludedFiles(String includedFile) {
		this.includedFiles.add(includedFile);
	}
	public void includedProject(GradleProject includedProject) {
		for (String compileProject : includedProject.getCompileProjectRefs())
			compileProjectRefs.add(compileProject);
		//TODO: transfer all infos
	}
	public void addIncludedProject(GradleProject includedProject) {
		includedProjects.add(includedProject);
	}
	public Set<GradleProject> getIncludedProjects() {
		return includedProjects;
	}
	public String getMainRootPath() {
		return mainRootPath;
	}
	public void setMainRootPath(String mainRootPath) {
		this.mainRootPath = mainRootPath;
	}
	public Set<GradleProject> getCompileProjects() {
		return compileProjects;
	}
	public void addCompileProject(GradleProject compileProject) {
		compileProjects.add(compileProject);
	}
	public Set<GradleLibrary> getCompileLibraries() {
		return compileLibraries;
	}
	public void addCompileLibrary(GradleLibrary compileLibrary) {
		compileLibraries.add(compileLibrary);
	}
	public Boolean isComputed() {
		return isComputed;
	}
	public void setIsComputed(Boolean isComputed) {
		this.isComputed = isComputed;
	}
	public Set<String> getMissingProjectRefs() {
		return missingProjectRefs;
	}
	public void addMissingProjectRef(String compileProjectRef) {
		missingProjectRefs.add(compileProjectRef);
	}
	public Set<String> getMissingLibraryRefs() {
		return missingLibraryRefs;
	}
	public void addMissingLibraryRef(String compileLibraryRef) {
		missingLibraryRefs.add(compileLibraryRef);
	}
	public Boolean isBuild() {
		return isBuild;
	}
	public void isBuild(Boolean isBuild) {
		this.isBuild = isBuild;
	}
}
