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
package com.adobe.ac.pmd.maven;

import java.util.Locale;

import net.sourceforge.pmd.PMDException;

import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.MavenReportException;

import com.adobe.ac.pmd.FlexPmdParameters;
import com.adobe.ac.pmd.FlexPmdViolations;

/**
 * @author xagnetti
 * @goal report
 * @phase site
 */
public class FlexPmdReportMojo extends AbstractFlexPmdMojo
{
   /**
    * Specifies the location of the source files to be used.
    * 
    * @parameter default-value="false"
    * @required
    * @readonly
    */
   private boolean aggregate;

   public FlexPmdReportMojo()
   {
      super();
   }

   public FlexPmdReportMojo( final MavenProject projectToBeSet,
                             final FlexPmdParameters parameters )
   {
      super( projectToBeSet, parameters );
   }

   @Override
   protected void onXmlReportExecuted( final FlexPmdViolations violations,
                                       final Locale locale ) throws MavenReportException
   {
      super.onXmlReportExecuted( violations,
                                 locale );

      final FlexPmdParameters parameters = new FlexPmdParameters( getExcludePackage(),
                                                                  getOutputDirectoryFile(),
                                                                  getRuleSet(),
                                                                  getSourceDirectory() );
      final FlexPmdHtmlEngine flexPmdHtmlEngine = new FlexPmdHtmlEngine( getSink(),
                                                                         getBundle( locale ),
                                                                         aggregate,
                                                                         getProject(),
                                                                         parameters );

      try
      {
         flexPmdHtmlEngine.executeReport( violations );
      }
      catch ( final PMDException e )
      {
         throw new MavenReportException( "An exception has been thrown while executing the HTML report", e );
      }
   }
}
