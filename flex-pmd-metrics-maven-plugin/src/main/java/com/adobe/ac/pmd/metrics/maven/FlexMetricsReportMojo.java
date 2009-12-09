/**
 *    Copyright (c) 2009, Adobe Systems, Incorporated
 *    All rights reserved.
 *
 *    Redistribution  and  use  in  source  and  binary  forms, with or without
 *    modification,  are  permitted  provided  that  the  following  conditions
 *    are met:
 *
 *      * Redistributions  of  source  code  must  retain  the  above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions  in  binary  form  must reproduce the above copyright
 *        notice,  this  list  of  conditions  and  the following disclaimer in
 *        the    documentation   and/or   other  materials  provided  with  the
 *        distribution.
 *      * Neither the name of the Adobe Systems, Incorporated. nor the names of
 *        its  contributors  may be used to endorse or promote products derived
 *        from this software without specific prior written permission.
 *
 *    THIS  SOFTWARE  IS  PROVIDED  BY THE  COPYRIGHT  HOLDERS AND CONTRIBUTORS
 *    "AS IS"  AND  ANY  EXPRESS  OR  IMPLIED  WARRANTIES,  INCLUDING,  BUT NOT
 *    LIMITED  TO,  THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 *    PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER
 *    OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,  INCIDENTAL,  SPECIAL,
 *    EXEMPLARY,  OR  CONSEQUENTIAL  DAMAGES  (INCLUDING,  BUT  NOT  LIMITED TO,
 *    PROCUREMENT  OF  SUBSTITUTE   GOODS  OR   SERVICES;  LOSS  OF  USE,  DATA,
 *    OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *    LIABILITY,  WHETHER  IN  CONTRACT,  STRICT  LIABILITY, OR TORT (INCLUDING
 *    NEGLIGENCE  OR  OTHERWISE)  ARISING  IN  ANY  WAY  OUT OF THE USE OF THIS
 *    SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.adobe.ac.pmd.metrics.maven;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.doxia.site.renderer.SiteRenderer;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import com.adobe.ac.pmd.metrics.maven.generators.NcssAggregateReportGenerator;
import com.adobe.ac.pmd.metrics.maven.generators.NcssReportGenerator;
import com.adobe.ac.pmd.metrics.maven.utils.ModuleReport;
import com.adobe.ac.pmd.metrics.maven.utils.NcssExecuter;

/**
 * @author xagnetti
 * @goal metrics
 */
public class FlexMetricsReportMojo extends AbstractMavenReport
{
   private static final String OUTPUT_NAME = "javancss";

   private static ResourceBundle getBundle( final Locale locale )
   {
      return ResourceBundle.getBundle( "flexMetrics",
                                       locale,
                                       FlexMetricsReportMojo.class.getClassLoader() ); // NOPMD
   }

   /**
    * Specifies the maximum number of lines to take into account into the
    * reports.
    * 
    * @parameter default-value="5"
    * @required
    */
   private int                  lineThreshold;

   /**
    * Specifies the directory where the HTML report will be generated.
    * 
    * @parameter expression="${project.reporting.outputDirectory}"
    * @required
    * @readonly
    */
   private final File           outputDirectory;

   /**
    * @parameter expression="${project}"
    * @required
    * @readonly
    */
   private final MavenProject   project;

   /**
    * The projects in the reactor for aggregation report.
    * 
    * @parameter expression="${reactorProjects}"
    * @readonly
    */
   private List< MavenProject > reactorProjects;

   /**
    * @parameter 
    *            expression="${component.org.codehaus.doxia.site.renderer.SiteRenderer}"
    * @required
    * @readonly
    */
   private SiteRenderer         siteRenderer;

   /**
    * Specifies the location of the source files to be used.
    * 
    * @parameter expression="${project.build.sourceDirectory}"
    * @required
    * @readonly
    */
   private final File           sourceDirectory;

   /**
    * Specified the name of the temporary file generated by Javancss prior
    * report generation.
    * 
    * @parameter default-value="javancss-raw-report.xml"
    * @required
    */
   private String               tempFileName;

   /**
    * Specifies the directory where the XML report will be generated.
    * 
    * @parameter default-value="${project.build.directory}"
    * @required
    */
   private File                 xmlOutputDirectory;

   public FlexMetricsReportMojo( final MavenProject projectToBeSet,
                                 final File source,
                                 final File output )
   {
      super();
      project = projectToBeSet;
      sourceDirectory = source;
      outputDirectory = output;
   }

   /**
    * @see org.apache.maven.reporting.MavenReport#canGenerateReport()
    */
   public boolean canGenerateReport()
   {
      return canGenerateSingleReport()
            || canGenerateAggregateReport();
   }

   /**
    * @see org.apache.maven.reporting.MavenReport#execute(java.util.Locale)
    */
   @Override
   public void executeReport( final Locale locale ) throws MavenReportException
   {
      if ( !canGenerateReport() )
      {
         throw new MavenReportException( "Cannot generate report " );
      }

      if ( canGenerateSingleReport() )
      {
         try
         {
            generateSingleReport( locale );
         }
         catch ( final DocumentException e )
         {
            throw new MavenReportException( e.getMessage(), e );
         }
         catch ( final IOException e )
         {
            throw new MavenReportException( e.getMessage(), e );
         }
      }
      if ( canGenerateAggregateReport() )
      {
         generateAggregateReport( locale );
      }
   }

   /**
    * @see org.apache.maven.reporting.MavenReport#getDescription(java.util.Locale)
    */
   public String getDescription( final Locale locale )
   {
      return getBundle( locale ).getString( "report.ncss.description" );
   }

   /**
    * @see org.apache.maven.reporting.MavenReport#getName(java.util.Locale)
    */
   public String getName( final Locale locale )
   {
      return getBundle( locale ).getString( "report.ncss.name" );
   }

   /**
    * @see org.apache.maven.reporting.MavenReport#getOutputName()
    */
   public String getOutputName()
   {
      return OUTPUT_NAME;
   }

   public void setLineThreshold( final int lineThresholdToBeSet )
   {
      lineThreshold = lineThresholdToBeSet;
   }

   public void setSiteRenderer( final SiteRenderer siteRendererToBeSet )
   {
      siteRenderer = siteRendererToBeSet;
   }

   public void setTempFileName( final String tempFileNameToBeSet )
   {
      tempFileName = tempFileNameToBeSet;
   }

   public void setXmlOutputDirectory( final File xmlOutputDirectoryToBeSet )
   {
      xmlOutputDirectory = xmlOutputDirectoryToBeSet;
   }

   /**
    * Build a path for the output filename.
    * 
    * @return A String representation of the output filename.
    */
   /* package */File buildOutputFile()
   {
      return new File( getXmlOutputDirectory()
            + File.separator + tempFileName );
   }

   /**
    * @see org.apache.maven.reporting.AbstractMavenReport#getOutputDirectory()
    */
   @Override
   protected String getOutputDirectory()
   {
      return outputDirectory.getAbsolutePath();
   }

   /**
    * @see org.apache.maven.reporting.AbstractMavenReport#getProject()
    */
   @Override
   protected MavenProject getProject()
   {
      return project;
   }

   /**
    * @see org.apache.maven.reporting.AbstractMavenReport#getSiteRenderer()
    */
   @Override
   protected SiteRenderer getSiteRenderer()
   {
      return siteRenderer;
   }

   /**
    * Getter for the source directory
    * 
    * @return the source directory as a File object.
    */
   protected File getSourceDirectory()
   {
      return sourceDirectory;
   }

   protected String getXmlOutputDirectory()
   {
      return xmlOutputDirectory.getAbsolutePath();
   }

   private boolean canGenerateAggregateReport()
   {
      return !project.getModules().isEmpty();
   }

   private boolean canGenerateSingleReport()
   {
      return sourceDirectory != null
            && sourceDirectory.exists();
   }

   private void generateAggregateReport( final Locale locale ) throws MavenReportException
   {
      final String basedir = project.getBasedir().toString();
      final String output = xmlOutputDirectory.toString();
      if ( getLog().isDebugEnabled() )
      {
         getLog().debug( "basedir: "
               + basedir );
         getLog().debug( "output: "
               + output );
      }
      String relative = null;
      if ( output.startsWith( basedir ) )
      {
         relative = output.substring( basedir.length() + 1 );
      }
      else
      {
         getLog().error( "Unable to aggregate report because I can't "
               + "determine the relative location of the XML report" );
         return;
      }
      getLog().debug( "relative: "
            + relative );
      final List< ModuleReport > reports = new ArrayList< ModuleReport >();
      for ( final MavenProject mavenProject : reactorProjects )
      {
         final MavenProject child = mavenProject;
         final File xmlReport = new File( child.getBasedir() // NOPMD
               + File.separator + relative, tempFileName );
         if ( xmlReport.exists() )
         {
            reports.add( new ModuleReport( child, loadDocument( xmlReport ) ) ); // NOPMD
         }
         else
         {
            getLog().debug( "xml file not found: "
                  + xmlReport );
         }
      }
      getLog().debug( "Aggregating "
            + reports.size() + " JavaNCSS reports" );

      new NcssAggregateReportGenerator( getSink(), getBundle( locale ), getLog() ).doReport( locale,
                                                                                             reports,
                                                                                             lineThreshold );
   }

   private void generateSingleReport( final Locale locale ) throws MavenReportException,
                                                           DocumentException,
                                                           IOException
   {
      if ( getLog().isDebugEnabled() )
      {
         getLog().debug( "Calling NCSSExecuter with src    : "
               + sourceDirectory );
         getLog().debug( "Calling NCSSExecuter with output : "
               + buildOutputFile() );
      }
      // run javaNCss and produce an temp xml file
      new NcssExecuter( sourceDirectory, buildOutputFile() ).execute();
      if ( !isTempReportGenerated() )
      {
         throw new MavenReportException( "Can't process temp ncss xml file." );
      }
      // parse the freshly generated file and write the report
      final NcssReportGenerator reportGenerator = new NcssReportGenerator( getSink(),
                                                                           getBundle( locale ),
                                                                           getLog() );
      reportGenerator.doReport( loadDocument(),
                                lineThreshold );
   }

   /**
    * Check that the expected temporary file generated by JavaNCSS exists.
    * 
    * @return <code>true</code> if the temporary report exists,
    *         <code>false</code> otherwise.
    */
   private boolean isTempReportGenerated()
   {
      return buildOutputFile().exists();
   }

   private Document loadDocument() throws MavenReportException
   {
      return loadDocument( buildOutputFile() );
   }

   /**
    * Load the xml file generated by javancss. It first tries to load it as is.
    * If this fails it tries to load it with the forceEncoding parameter which
    * defaults to the system property "file.encoding". If this latter fails, it
    * throws a MavenReportException.
    */
   private Document loadDocument( final File file ) throws MavenReportException
   {
      try
      {
         return loadDocument( file,
                              null );
      }
      catch ( final DocumentException ignored )
      {
         try
         {
            return loadDocument( file,
                                 System.getProperty( "file.encoding" ) );
         }
         catch ( final DocumentException de )
         {
            throw new MavenReportException( de.getMessage(), de );
         }
      }
   }

   private Document loadDocument( final File file,
                                  final String encoding ) throws DocumentException
   {
      final SAXReader saxReader = new SAXReader();
      if ( encoding != null )
      {
         saxReader.setEncoding( encoding );
         getLog().debug( "Loading xml file with encoding : "
               + encoding );
      }
      return saxReader.read( file );
   }
}
