package com.castsoftware.dmt.discoverer.jee.gradle;

import org.junit.Test;

import com.castsoftware.dmt.engine.discovery.IProjectsDiscoverer;
import com.castsoftware.dmt.engine.discovery.ProjectsDiscovererWrapper;
import com.castsoftware.dmt.engine.discovery.ProjectsDiscovererWrapper.ProfileOrProjectTypeConfiguration;
import com.castsoftware.dmt.engine.discovery.ProjectsDiscovererWrapper.ProfileOrProjectTypeConfiguration.LanguageConfiguration;
import com.castsoftware.dmt.engine.discovery.ProjectsDiscoveryEngineTester;
import com.castsoftware.dmt.engine.project.Profile.ReferenceCollation;

/**
 * Tests for gradle based projects discovery
 *
 */
public class JeeGradleDiscovererUnitTest
{

    private static class JeeGradleDiscovererUnitTestTester extends ProjectsDiscoveryEngineTester
    {
        JeeGradleDiscovererUnitTestTester(String desc)
        {
            super(JeeGradleDiscovererUnitTest.class, desc);
        }

        @Override
        protected IProjectsDiscoverer createTestDiscoverer()
        {
            return new JeeGradleDiscoverer();
        }

        @Override
        protected void configureTestdiscoverer(ProjectsDiscovererWrapper discovererWrapper)
        {
            ProfileOrProjectTypeConfiguration projectTypeConfiguration = discovererWrapper.addProjectTypeConfiguration(
                "dmtdevjeetechno.J2EEProject", "build.gradle", ReferenceCollation.WindowsNTFS, null);
            LanguageConfiguration javaLanguage = projectTypeConfiguration.addLanguageConfiguration("JavaLanguage",
                "*.java;*.sqlj", ReferenceCollation.WindowsNTFS);
            javaLanguage.addResourceTypeConfiguration("JavaContainerLanguage", null, ReferenceCollation.WindowsNTFS, "*.jar",
                ReferenceCollation.WindowsNTFS);
            javaLanguage.addResourceTypeConfiguration("XMLLanguage", "*.xml", ReferenceCollation.WindowsNTFS, null,
                ReferenceCollation.WindowsNTFS);
            javaLanguage.addResourceTypeConfiguration("JavaPropertiesLanguage", "*.properties", ReferenceCollation.WindowsNTFS,
                null,
                ReferenceCollation.WindowsNTFS);
            LanguageConfiguration javaWebServerLanguage = projectTypeConfiguration.addLanguageConfiguration(
                "JavaWebServerLanguage", "*.jsp", ReferenceCollation.WindowsNTFS);
            javaWebServerLanguage.addResourceTypeConfiguration("JavaWebContainerLanguage", null, ReferenceCollation.WindowsNTFS,
                "*.ear;*.war",
                ReferenceCollation.WindowsNTFS);
            projectTypeConfiguration.addLanguageConfiguration("JavaWebClientLanguage",
                "*.htm;*.html;*.htc;*.js;*.vbs", ReferenceCollation.WindowsNTFS);

            // projectOrigin
            discovererWrapper.configure("Gradle Java project");
         }
    }

    /**
     * Test discovery for one project
     *
     * @throws Throwable
     *             if anything goes wrong
     */
    @Test
    public void unitTest1() throws Throwable
    {
        new JeeGradleDiscovererUnitTestTester("UT1").go();
    }

}
