package com.castsoftware.dmt.engine.validation;

import com.castsoftware.util.FileHelper;

/**
 * A {@link GradleProjectHelper} is a Maven project helper.
 */
public class GradleProjectHelper
{
    private GradleProjectHelper()
    {
        // NOP
    }

    /**
     * @see <a href="http://commons.apache.org/io/api-1.4/org/apache/commons/io/FilenameUtils.html">http://commons.apache.org/io/api-1.4/org/apache/commons/io/FilenameUtils.html</a>
     * @see <a href="http://msdn.microsoft.com/en-us/library/aa365247(VS.85).aspx">http://msdn.microsoft.com/en-us/library/aa365247(VS.85).aspx</a>
     * @see <a href="http://en.wikipedia.org/wiki/Path_(computing)">http://en.wikipedia.org/wiki/Path_(computing)</a>
     *
     * @param path
     *            the path to be canonized
     * @return the corresponding canonical path
     */
    public static String buildCanonicalPath(String path)
    {
        if (path == null)
            return null;

        path = FileHelper.getPortablePath(path);

        return ProjectsValidationHelper.buildCanonicalPath(path);
    }

    /**
     * Builds a full file path from a root folder path and a relative path.
     *
     * @param rootPath
     *            - the root folder path
     * @param relativePath
     *            - the relative file path
     * @return the corresponding full file path
     */
    public static String buildFilePath(String rootPath, String relativePath)
    {
        String path = FileHelper.buildFilePath(rootPath, relativePath);

        return FileHelper.getPortablePath(path);
    }
}
