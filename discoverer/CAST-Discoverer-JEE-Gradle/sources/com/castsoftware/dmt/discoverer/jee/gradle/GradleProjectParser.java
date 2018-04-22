package com.castsoftware.dmt.discoverer.jee.gradle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.castsoftware.java.string.StringHelper;
import com.castsoftware.util.logger.Logging;

/**
 * Scanner for json file
 */
public class GradleProjectParser
{
	int javaLanguageId = 1;
	int javaContainerLanguageId = 1;
	int javaXmlLanguageId = 2;
	int javaPropertiesLanguageId = 3;
	int javaWebServerLanguageId = 2;
	int javaWebContainerLanguageId = 1;
	int javaWebClientLanguageId = 3;
	
    public GradleProjectParser(GradleProject gradleProject)
    {
        // NOP
    }

    /**
     * Parse the gradle file and add info to the project.
     *
     * @param relativeFilePath
     *            the path to the project file used for reference
     * @param project
     *            the project file interpreter
     * @param projectContent
     *            the file content to scan.
     * @return {@code true} if no error was encountered during scanning. {@code false} otherwise.
     */
    public static Boolean parse(String relativeFilePath, GradleProject project, String projectContent)
    {
    	BufferedReader reader = null;
    	Boolean isInBlock = false;
    	String blockType = "";
    	List<String> blockContent = new ArrayList<String>();
    	int blockContentSection = 0;
    	
    	reader = new BufferedReader(new StringReader(projectContent), projectContent.length());

        try
        {
            for (String readline = reader.readLine(); readline != null; readline = reader.readLine())
            {
            	Boolean isBlock = false;
            	String line = StringHelper.trimBlank(readline);            	
            	if (line.length() == 0)
            		continue;
            	if (!isInBlock)
            	{
            		if (line.startsWith("allprojects"))
	            	{
            			isBlock = true;
	            		blockType = "allprojects";
	            	}
	            	else if (line.startsWith("subprojects"))
	            	{
            			isBlock = true;
	            		blockType = "subprojects";
	            	}
	            	else if (line.startsWith("ext"))
	            	{
            			isBlock = true;
	            		blockType = "ext";
	            	}
	            	else if (line.startsWith("eclipse"))
	            	{
            			isBlock = true;
	            		blockType = "eclipse";
	            	}
	            	else if (line.startsWith("task "))
	            	{
            			isBlock = true;
	            		blockType = "task";
	            	}
	            	else if (line.startsWith("def "))
	            	{
            			isBlock = true;
	            		blockType = "def";
	            	}
	            	else if (line.startsWith("ear"))
	            	{
            			isBlock = true;
	            		blockType = "ear";
	            	}
	            	else if (line.startsWith("war"))
	            	{
            			isBlock = true;
	            		blockType = "war";
	            	}
	            	else if (line.startsWith("jar"))
	            	{
	            		if (line.startsWith("jar."))
	            		{
	            			
	            		}
	            		else
	            		{
	            			isBlock = true;
	            			blockType = "jar";
	            		}
	            	}
	            	else if (line.startsWith("dependencies"))
	            	{
	            		isBlock = true;
	            		blockType = "dependencies";
	            	}
	            	else if (line.startsWith("configurations"))
	            	{
	            		isBlock = true;
	            		blockType = "configurations";
	            	}
	            	else if (line.startsWith("sourceSets"))
	            	{
	            		isBlock = true;
	            		blockType = "sourceSets";
	            	}
	            	else if (line.startsWith("artifacts"))
	            	{
	            		isBlock = true;
	            		blockType = "artifacts";
	            	}
	            	else if (line.startsWith("void"))
	            	{
	            		isBlock = true;
	            		blockType = "void";
	            	}
                	else if (line.startsWith("apply plugin"))
                	{
                		try
                		{
                			String applyPlugin = line.substring(line.indexOf("'") + 1, line.length() - 1);
                			if (applyPlugin.length() > 0)
                				project.addApplyPlugins(applyPlugin);
                		}
                		catch (IndexOutOfBoundsException e)
                		{
                			// 
                		}
                	}
                	else if (line.startsWith("apply from"))
                	{
                		try
                		{
                			String filename = line.substring(line.indexOf("\""), line.length() - 1);
                			filename = "dist.gradle";
                			project.addIncludedFiles(filename);
                		}
                		catch (IndexOutOfBoundsException e)
                		{
                			// 
                		}
                	}
                	else if (line.startsWith("build.dependsOn"))
                	{
                		project.isBuild(true);
                	}
            		
            		if (isBlock)
            		{
            			isInBlock = true;
	            		blockContent.clear();
	            		blockContentSection = 0;
	            		// special case: { not inline
	            		if (line.contains("{"))
	            		{
	            			blockContentSection += addBlockContent(blockContent,line.substring(blockType.length()));
	            			if (blockContentSection <= 0)
	            			{
	            				isInBlock = false;
	            				blockType = "";
	            			}
	            		}
	            	}
            	}
            	else
            	{
        			// in a block
            		blockContentSection += addBlockContent(blockContent,line);
            		if (blockContentSection <= 0)
            		{
            			//TODO: parse the block content
            			if ("void".equals(blockType))
            				parseBlock(blockType, project, blockContent);
            			if ("dependencies".equals(blockType))
            				parseBlock(blockType, project, blockContent);
            			if ("sourceSets".equals(blockType))
            				parseBlockSourceSets(blockType, project, blockContent);
            			if ("jar".equals(blockType))
            				parseBlockJar(blockType, project, blockContent);
            			if ("ext".equals(blockType))
            				parseBlockExt(blockType, project, blockContent);
            			isInBlock = false;
            			isBlock = false;
            			blockType = "";
            		}
            	}

            }
        }
        catch (IOException e)
        {
            Logging.managedError(e, "cast.dmt.discover.jee.gradle.ioExceptionInProjectParsing", "PATH", relativeFilePath);
        }
        finally
        {
        	try {
				reader.close();
			} catch (IOException e) {
				Logging.managedError(e, "cast.dmt.discover.jee.gradle.ioExceptionInProjectParsing", "PATH", relativeFilePath);
			}
        }
        return true;
    }

    private static int addBlockContent(List<String> blockContent, String line)
    {
    	int nb = 0;
    	line = line.trim();
    	if (!line.startsWith("//") && line.length() > 0)
		{
			int pos1 = line.indexOf("{");
			int pos2 = line.indexOf("}");
    		if (pos1 >= 0)
    		{
    			if (pos2 >= 0)
    			{
    				if (pos1 < pos2)
    				{
    					nb++;
    					if (pos1 == 0)
    						blockContent.add("{");
    					else
    					{
    						blockContent.add(line.substring(0,pos1));
    						blockContent.add("{");
    					}
    					if (line.length() > pos1 + 1)
    						nb += addBlockContent(blockContent, line.substring(pos1 + 1));
    				}
    				else
    				{
    					nb--;
    					if (pos2 == 0)
    						blockContent.add("}");
    					else
    					{
    						blockContent.add(line.substring(0,pos2));
    						blockContent.add("}");
    					}
    					if (line.length() > pos2 + 1)
    						nb += addBlockContent(blockContent, line.substring(pos2 + 1));					
    				}
    			}
    			else
    			{
					nb++;
					if (pos1 == 0)
						blockContent.add("{");
					else
					{
						blockContent.add(line.substring(0,pos1));
						blockContent.add("{");
					}
					if (line.length() > pos1 + 1)
						nb += addBlockContent(blockContent, line.substring(pos1 + 1));
    			}    			
    		}
    		else
    		{
    			if (pos2 >= 0)
    			{
					nb--;
					if (pos2 == 0)
						blockContent.add("}");
					else
					{
						blockContent.add(line.substring(0,pos2));
						blockContent.add("}");
					}
					if (line.length() > pos2 + 1)
						nb += addBlockContent(blockContent, line.substring(pos2 + 1));
    			}
    			else
    			{
    				blockContent.add(line);
    			}
    		}
		}
		return nb;
    }

    private static void parseBlockJar(String blockType, GradleProject project, List<String> blockContent)
    {
    	for (String line : blockContent)
    	{
    		if (line.equals("enabled=false"))
    		{
    			project.addApplyPlugins("jarDisable");
    			project.removeApplyPlugins("jar");
    		}
    	}
    }
    private static void parseBlockExt(String blockType, GradleProject project, List<String> blockContent)
    {
    	for (String line : blockContent)
    	{
    		if (line.equals("enabled=false"))
    		{
    			project.addApplyPlugins("jarDisable");
    			project.removeApplyPlugins("jar");
    		}
    	}
    }
    private static void parseBlock(String blockType, GradleProject project, List<String> blockContent)
    {
    	Boolean isInSourceSets = false;
    	Boolean isInDependencies = "dependencies".equals(blockType);
    	Boolean isInMain = false;
    	Boolean isInTest = false;
    	Boolean isInJava = false;
    	Boolean isInResources = false;
    	Boolean isInsideListProjects = false;
    	for (String line : blockContent)
    	{
    		
	    	if (isInsideListProjects)
	    	{
	        	if ("]".equals(line))
	        	{
	        		isInsideListProjects = false;
	        	}
	        	else if (line.startsWith("':"))
	        	{
//	        		int start = line.indexOf("':");
//	        		int end = line.indexOf("'", start + 1);
//	        		if (end > start)
//	        		{
//	            		String projectRef = line.substring(start + 2, end);
//	            		//String relativePath = projectName.replace(":", "/") + "/build.gradle";
//	            		//project.addCompileProjectRef(relativePath);
//	            		project.addCompileProjectRef(projectRef);
//	        		}
    	    		String projectName = extractReference(line);
    	    		if (!"".equals(projectName))
    	    			project.addCompileProjectRef(projectName);
	        	}
	        }
	    	else if ("def earProjects=[".equals(line) || "def libsProjects=[".equals(line) || "def warProjects=[".equals(line) || "def dbProjects=[".equals(line) || "def seProjects=[".equals(line))
	    	{
	    		isInsideListProjects = true;
	    	}
	    	else if (line.startsWith("sourceSets {"))
				isInSourceSets = true;
	    	else if (line.startsWith("main {"))
	    		isInMain = true;
	    	else if (line.startsWith("test {"))
	    		isInTest = true;
	    	else if (line.startsWith("java {"))
	    	{
	    		if (line.contains("}"))
	    		{
	    			String val = line.substring(6, line.length() - 1);
	    			// srcDir
	    			project.addMainJavaSrcDir(val);
	    		}
	    		else
	    			isInJava = true;
	    	}
	    	else if (line.startsWith("resources"))
	    	{
	    		isInResources = true;
	    	}
	    	else if ("}".equals(line))
	    	{
	    		if (isInSourceSets)
	    		{
	        		if (isInJava)
	        			isInJava = false;
	        		else if (isInResources)
	        			isInResources = false;
	        		else if (isInMain)
	        			isInMain = false;
	        		else if (isInTest)
	        			isInTest = false;
	        		else
	        			isInSourceSets = false;
	    		}
	    	}
	    	else if (line.startsWith("earlib project") || line.startsWith("deploy project"))
	    	{
//	    		int start = line.indexOf("':");
//	    		int end = line.indexOf("'", start + 1);
//	    		if (end > start)
//	    		{
//	        		String projectRef = line.substring(start + 2, end);
//	        		//String relativePath = projectName.replace(":", "/") + "/build.gradle";
//	        		//project.addCompileProjectRef(relativePath);
//	        		project.addCompileProjectRef(projectRef);
//	    		}
	    		String projectName = extractReference(line);
	    		if (!"".equals(projectName))
	    			project.addCompileProjectRef(projectName);
	    	}
	    	else if ((line.startsWith("compile project") || line.startsWith("compile (project")) && isInDependencies)
	    	{
	    		String projectName = extractReference(line);
	    		if (!"".equals(projectName))
	    			project.addCompileProjectRef(projectName);
//	    		int start = line.indexOf("':");
//	    		char endChar = '\'';
//	    		if (start < 0)
//	    		{
//	    			start = line.indexOf("\":");
//	    			 endChar = '\"';
//	    		}
//	    		int end = line.indexOf(endChar, start + 1);
//	    		if (end > start)
//	    		{
//	        		String projectName = line.substring(start + 2, end);
//	        		String relativePath = projectName.replace(":", "/") + "/build.gradle";
//	        		project.addCompileProjectRef(relativePath);
//	    		}
	    	}
	    	else if (line.startsWith("compile ") && isInDependencies)
	    	{
	    		// library
	    		String libraryName = line.substring(8).trim();
	    		if (!"".equals(libraryName))
	    			project.addCompileLibraryRef(libraryName);
	    	}
	    	else
	    	{
	    		//
	    	}
	    	
    	}
    }

    private static void parseBlockSourceSets(String blockType, GradleProject project, List<String> blockContent)
    {
    	Boolean isInMain = false;
    	Boolean isInTest = false;
    	Boolean isInJava = false;
    	Boolean isInResources = false;
    	for (String line : blockContent)
    	{
	    	if (line.startsWith("main"))
	    		isInMain = true;
	    	else if (line.startsWith("test"))
	    		isInTest = true;
	    	else if (line.startsWith("java"))
	    	{
    			isInJava = true;
	    	}
	    	else if (line.startsWith("resources"))
	    	{
	    		isInResources = true;
	    	}
	    	else if ("}".equals(line))
	    	{
        		if (isInJava)
        			isInJava = false;
        		else if (isInResources)
        			isInResources = false;
        		else if (isInMain)
        			isInMain = false;
        		else if (isInTest)
        			isInTest = false;
	    	}
	    	else if (isInMain && isInJava)
	    	{
	    		int pos = line.indexOf("srcDir '");
	    		if (pos >= 0)
    			{
	    			String val = line.substring(pos + 8, line.indexOf("'", pos + 8));
	    			// srcDir
	    			project.addMainJavaSrcDir(val);
    			}
	    	}
    	}
    }

    public static Boolean parseSettings(Map<String, GradleProject> gradleProjects, String relativeFilePath, GradleProject project, String projectContent)
    {
    	BufferedReader reader = null;
    	reader = new BufferedReader(new StringReader(projectContent), projectContent.length());

        try
        {
        	if ("settings.gradle".equals(relativeFilePath))
        		project.setMainRootPath("");
        	else
        		project.setMainRootPath(relativeFilePath.substring(0,relativeFilePath.length() - "settings.gradle".length()));
            for (String readline = reader.readLine(); readline != null; readline = reader.readLine())
            {
            	String line = StringHelper.trimBlank(readline);            	
            	if (line.length() != 0 && line.startsWith("include"))
            	{
    	    		String projectName = "";
    	    		int start = line.indexOf("'");
    	    		char endChar = '\'';
    	    		if (start < 0)
    	    		{
    	    			start = line.indexOf("\"");
    	    			 endChar = '\"';
    	    		}
    	    		int end = line.indexOf(endChar, start + 1);
    	    		if (end > start)
    	    		{
    	    			projectName = line.substring(start + 1, end);
    	    		}

    	    		if (!"".equals(projectName))
    	    		{
    	    			if (gradleProjects.containsKey(projectName))
    	    			{
    	    				//
    	    				Logging.info("duplicate", "NAME", projectName);
    	    			}
    	    			else
    	    			{
        	    			project.addIncludedProjectRef(projectName);
        	    			String buildRelativeFilePath = project.getMainRootPath() + projectName.replace(":", "/") + "/build.gradle";
        	    			GradleProject p = gradleProjects.get(buildRelativeFilePath);
        	    			if (p == null)
        	    				p = new GradleProject(project.getMainRootPath(), projectName, buildRelativeFilePath);
        	    			else
        	    			{
        	    				p.setMainRootPath(project.getMainRootPath());
        	    				p.setName(projectName);
        	    			}
    	    				gradleProjects.put(buildRelativeFilePath, p);
    	    			}
    	    		}
            	}
            }
        }
        catch (IOException e)
        {
            Logging.managedError(e, "cast.dmt.discover.jee.gradle.ioExceptionInProjectParsing", "PATH", relativeFilePath);
        }
        finally
        {
        	try {
				reader.close();
			} catch (IOException e) {
				Logging.managedError(e, "cast.dmt.discover.jee.gradle.ioExceptionInProjectParsing", "PATH", relativeFilePath);
			}
        }
        return true;
    }
    
    private static String extractReference(String line)
    {
    	String reference = "";
		int start = line.indexOf("':");
		char endChar = '\'';
		if (start < 0)
		{
			start = line.indexOf("\":");
			 endChar = '\"';
		}
		int end = line.indexOf(endChar, start + 1);
		if (end > start)
		{
			reference = line.substring(start + 2, end);
		}
		return reference;
    }
}

