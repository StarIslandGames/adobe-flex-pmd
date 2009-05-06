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
package com.adobe.ac.pmd;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import net.sourceforge.pmd.PMDException;

import org.junit.Test;
import org.xml.sax.SAXException;

import com.adobe.ac.pmd.engines.AbstractFlexPmdEngine;
import com.adobe.ac.pmd.engines.AbstractTestFlexPmdEngine;
import com.adobe.ac.pmd.engines.FlexPmdXmlEngine;

public class FlexPmdXmlEngineTest
      extends AbstractTestFlexPmdEngine
{
   private static final String OUTPUT_DIRECTORY_URL = "target/report/";

   public FlexPmdXmlEngineTest(
         final String name )
   {
      super( name );
   }

   @Test
   @Override
   public void testExecuteReport() throws PMDException, SAXException,
         URISyntaxException, IOException
   {
      super.testExecuteReport();

      final File outXmlReport = new File( OUTPUT_DIRECTORY_URL
            + FlexPmdXmlEngine.PMD_XML );
      // final File outXmlReportReference = new File( getClass().getResource(
      // "/pmd.xml" ).toURI().getPath() );

      // assertEquals(
      // "XML report is not identical", readString( outXmlReportReference ),
      // readString( outXmlReport ) );

      final SchemaFactory factory = SchemaFactory
            .newInstance( "http://www.w3.org/2001/XMLSchema" );

      final URL schemaResource = getClass().getResource(
            "/pmd.xsd" );

      assertNotNull( "pmd.xsd is not loaded", schemaResource );

      final Schema schema = factory.newSchema( schemaResource );

      final Validator validator = schema.newValidator();

      final Source source = new StreamSource( outXmlReport );

      try
      {
         validator.validate( source );
      }
      catch ( final Exception ex )
      {
         fail( FlexPmdXmlEngine.PMD_XML
               + " is not valid: " + ex.getMessage() );
      }
   }

   @Override
   protected AbstractFlexPmdEngine getFlexPmdEngine()
   {
      return new FlexPmdXmlEngine();
   }
}
