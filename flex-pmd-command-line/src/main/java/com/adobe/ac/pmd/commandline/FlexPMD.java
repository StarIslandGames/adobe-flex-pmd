/**
 *    Copyright (c) 2008. Adobe Systems Incorporated.
 *    All rights reserved.
 *
 *    Redistribution and use in source and binary forms, with or without
 *    modification, are permitted provided that the following conditions
 *    are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in
 *        the documentation and/or other materials provided with the
 *        distribution.
 *      * Neither the name of Adobe Systems Incorporated nor the names of
 *        its contributors may be used to endorse or promote products derived
 *        from this software without specific prior written permission.
 *
 *    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *    "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *    LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 *    PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 *    OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *    EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *    PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *    PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *    LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *    NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *    SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.adobe.ac.pmd.commandline;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.PMDException;

import com.adobe.ac.pmd.CommandLineOptions;
import com.adobe.ac.pmd.CommandLineUtils;
import com.adobe.ac.pmd.FlexPmdViolations;
import com.adobe.ac.pmd.engines.FlexPmdXmlEngine;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

final public class FlexPMD
{
   private static JSAPResult   config;
   private static final Logger LOGGER = Logger.getLogger( FlexPMD.class.getName() );

   /**
    * @param args
    * @throws JSAPException
    * @throws PMDException
    * @throws FileNotFoundException
    * @throws URISyntaxException
    * @throws Exception
    */
   public static void main( final String[] args ) throws JSAPException,
                                                 FileNotFoundException,
                                                 PMDException,
                                                 URISyntaxException
   {
      LOGGER.setLevel( Level.WARNING );
      startFlexPMD( args );
      LOGGER.info( "FlexPMD terminated" );
      System.exit( 0 );
   }

   static boolean areCommandLineOptionsCorrect( final String[] args ) throws JSAPException
   {
      final JSAP jsap = new JSAP();
      config = parseCommandLineArguments( args,
                                          jsap );

      if ( !config.success() )
      {
         LOGGER.log( Level.SEVERE,
                     "Usage: java "
                           + FlexPMD.class.getName() + " " + jsap.getUsage() );
      }

      return config.success();
   }

   static String getParameterValue( final CommandLineOptions option )
   {
      return config.getString( option.toString() );
   }

   static boolean startFlexPMD( final String[] args ) throws JSAPException,
                                                     PMDException,
                                                     FileNotFoundException,
                                                     URISyntaxException
   {
      if ( areCommandLineOptionsCorrect( args ) )
      {
         new FlexPmdXmlEngine().executeReport( new File( getParameterValue( CommandLineOptions.SOURCE_DIRECTORY ) ),
                                               new File( getParameterValue( CommandLineOptions.OUTPUT ) ),
                                               new File( getParameterValue( CommandLineOptions.RULE_SET ) ),
                                               new FlexPmdViolations() );
      }

      return config.success();
   }

   private static JSAPResult parseCommandLineArguments( final String[] args,
                                                        final JSAP jsap ) throws JSAPException
   {
      CommandLineUtils.registerParameter( jsap,
                                          CommandLineOptions.SOURCE_DIRECTORY,
                                          true );
      CommandLineUtils.registerParameter( jsap,
                                          CommandLineOptions.OUTPUT,
                                          true );
      CommandLineUtils.registerParameter( jsap,
                                          CommandLineOptions.RULE_SET,
                                          false );

      return jsap.parse( args );
   }

   private FlexPMD()
   {
   }
}
