package com.castsoftware.dmt.discoverer.jee.gradle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.castsoftware.java.string.StringHelper;
import com.castsoftware.util.logger.Logging;

/**
 * Scanner for json file
 */
public class GradleLibraryParser
{
    public GradleLibraryParser(GradleProject gradleProject)
    {
        // NOP
    }

    /**
     * Parse the gradle file and add info to the project.
     *
     * @param relativeFilePath
     *            the path to the project file used for reference
     * @param gradleLibraries
     *            the library file interpreter
     * @param fileContent
     *            the file content to parse.
     * @return {@code true} if no error was encountered during scanning. {@code false} otherwise.
     */
    public static Boolean parse(String relativeFilePath, String fileContent, Map<String, GradleLibrary> gradleLibraries)
    {
    	BufferedReader reader = null;
    	Boolean isInExt = false;
    	Boolean isInLibraryBlock = false;
    	String prefixLibrary = "";
    	List<String> blockContent = new ArrayList<String>();
    	Map<String, String> variables = new HashMap<String, String>();
    	
    	reader = new BufferedReader(new StringReader(fileContent), fileContent.length());

        try
        {
            for (String readline = reader.readLine(); readline != null; readline = reader.readLine())
            {
            	String line = StringHelper.trimBlank(readline);            	
            	if (line.length() == 0)
            		continue;
            	if (!isInExt)
            	{
	            	if (line.startsWith("ext{"))
	            		isInExt = true;
	            	continue;
            	}
            	else
            	{
            		if (line.equals("}"))
            		{
            			isInExt = false;
            			continue;
            		}
            	}
            	if (!isInLibraryBlock)
            	{
	            	if (line.contains("=["))
	            	{
	            		int pos = line.indexOf("=[");
	            		isInLibraryBlock = true;
	            		prefixLibrary = line.substring(0, pos);
	            		if (line.length() > pos + 2)
	            		{
	            			addBlockContent(blockContent,line.substring(line.indexOf("=[" + 2)));
	            			if (line.endsWith("]"))
	            				isInLibraryBlock = false;
	            		}
	            	}
                	else if (line.contains("="))
                	{
                		// variable
                		String var = line.substring(0,line.indexOf("=")).trim();
                		String val = line.substring(line.indexOf("=") + 1).trim();
                		if (!variables.containsKey(var))
                			variables.put(var, val);
                	}
            	}
            	else if (line.equals("]"))
            	{
            		isInLibraryBlock = false;
            		parseBlockLibrary(prefixLibrary, gradleLibraries, blockContent, variables);
            		blockContent.clear();
            	}
            	else
            	{
            		addBlockContent(blockContent,line);
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
        // variable replacement
        return true;
    }

    private static void addBlockContent(List<String> blockContent, String line)
    {
    	line = line.trim();
    	if (!line.startsWith("//") && line.length() > 0)
		{
			int pos = line.indexOf(",");
			if (pos == 0)
    		{
				blockContent.add(",");
    		}
			else if (pos > 0)
			{
				blockContent.add(line.substring(0,pos).trim());
				blockContent.add(",");
				if (line.length() > pos + 1)
					addBlockContent(blockContent, line.substring(pos + 1));
    		}
    		else
    		{
    			blockContent.add(line);
    		}
		}
		return;
    }

    private static void parseBlockLibrary(String prefixLibrary, Map<String, GradleLibrary> gradleLibraries, List<String> blockContent, Map<String, String> variables)
    {
    	GradleLibrary currentLibrary = null;
    	List<String> blockLibraryContent = new ArrayList<String>();
		Boolean newFileTreeLibrary = false;
		Boolean newSimpleLibrary = false;
		Boolean isInIncudes = false;
    	for (String line : blockContent)
    	{
    		int pos = line.indexOf(":fileTree");
    		if (pos > 0)
    			newFileTreeLibrary = true;
    		else if (currentLibrary == null && line.indexOf(":") > 0)
    		{
    			pos = line.indexOf(":");
    			newSimpleLibrary = true;
    		}

    		if (newFileTreeLibrary || newSimpleLibrary)
    		{
				String suffixLibrary = line.substring(0,pos).trim();
				String libraryName = prefixLibrary + ":" + suffixLibrary;
				
				currentLibrary = gradleLibraries.get(libraryName);
				if (currentLibrary == null)
				{
					GradleLibrary lib = new GradleLibrary(libraryName);
					gradleLibraries.put(libraryName, lib);
					blockLibraryContent.clear();
					if (newSimpleLibrary)
					{
						if (line.length() > pos + 2)
						{
							String ref = line.substring(pos + 2);
							int pos2 = ref.indexOf("\"");
							if (pos2 > 0)
								ref = ref.substring(0,pos2);
							ref = replaceVariable(ref, variables);
							lib.setReference(ref);
						}
					}
					else if (newFileTreeLibrary)
					{
						currentLibrary = lib;
						if (line.length() > pos)
						{
							int pos1 = line.indexOf("dir:");
							if (pos1 > 0)
							{
								String val = line.substring(pos1 + 4).trim();
								val = replaceVariable(val, variables);
								currentLibrary.setDir(val);
							}
			    	    }
					}
				}				
				newSimpleLibrary=false;
				newFileTreeLibrary = false;
	    	}
    		else
    		{
				if (line.startsWith("include:"))
				{
				}
				else if (line.startsWith("includes:"))
				{
					isInIncudes=true;
					int pos1 = line.indexOf("[");
					if (pos1 > 0 && line.length() > pos1)
					{
						String include = line.substring(pos1 + 1).trim();
						include = replaceVariable(include,variables);
						currentLibrary.addInclude(include);
					}
				}
				else if (isInIncudes)
				{
					if (currentLibrary != null)
		    		{
		    			if (line.contains(".jar"))
		    			{
		    				line = replaceVariable(line, variables);
		    				currentLibrary.addInclude(line);
		    			}
		    		}
					if (line.contains("]"))
						isInIncudes = false;
				}
				else if (line.endsWith(")"))
					currentLibrary = null;
	    	}
    	}
    }
    
    private static String replaceVariable(String ref, Map<String, String> variables)
    {
    	int pos = ref.indexOf("$");
    	if (pos < 0)
    		return ref;
    	String var = ref.substring(pos + 1);
    	String val = variables.get(var);
    	if (val != null)
    		ref = ref.substring(0, pos) + val.replace("'", "");
    	return ref;
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

