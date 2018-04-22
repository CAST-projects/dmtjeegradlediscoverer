package com.castsoftware.dmt.discoverer.jee.gradle;

import java.util.HashSet;
import java.util.Set;

public class GradleLibrary {
	private String name;
	private String version;
	private String reference;
	private String dir;
	private final Set<String> includes;
	
	public GradleLibrary(String name)
	{
		this.name = name;
		includes = new HashSet<String>();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public String getDir() {
		return dir;
	}
	public void setDir(String dir) {
		if (dir.startsWith("dir:"))
			if (dir.length() > 4)
				dir = dir.substring(4);
		this.dir = dir.replace("\"", "");
	}
	public void addInclude(String include) {
		if (include.length() > 0)
			includes.add(include.replace("\"", "").replace("'", ""));
	}
	public Set<String> getIncludes() {
		return includes;
	}

}
