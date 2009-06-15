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
package com.adobe.ac.pmd.rules.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.adobe.ac.pmd.FlexPmdTestBase;
import com.adobe.ac.pmd.IFlexViolation;
import com.adobe.ac.pmd.files.IFlexFile;
import com.adobe.ac.pmd.parser.exceptions.TokenException;

public abstract class AbstractFlexRuleTest extends FlexPmdTestBase
{
   protected static Map< String, ViolationPosition[] > addToMap( final Map< String, ViolationPosition[] > map,
                                                                 final String resource,
                                                                 final ViolationPosition[] positions )
   {
      map.put( resource,
               positions );
      return map;
   }

   /**
    * Test case which contains non-concerned files by the given rule
    * 
    * @throws TokenException
    * @throws IOException
    */
   @Test
   public final void testProcessNonViolatingFiles() throws IOException,
                                                   TokenException
   {
      final Map< String, List< IFlexViolation >> violatedFiles = extractActualViolatedFiles();
      final StringBuffer buffer = buildMessageName( violatedFiles );

      if ( !violatedFiles.isEmpty() )
      {
         fail( buffer.toString() );
      }
   }

   /**
    * Test case which contains violating files
    */
   @Test
   public final void testProcessViolatingFiles()
   {
      for ( final String fileName : getViolatingFiles().keySet() )
      {
         assertViolations( fileName,
                           getViolatingFiles().get( fileName ) );
      }
   }

   protected abstract AbstractFlexRule getRule();

   protected abstract Map< String, ViolationPosition[] > getViolatingFiles();

   protected List< IFlexViolation > processFile( final String resourcePath ) throws IOException,
                                                                            TokenException
   {
      return getRule().processFile( getTestFiles().get( resourcePath ),
                                    null,
                                    getTestFiles() );
   }

   private void assertViolations( final String resourcePath,
                                  final ViolationPosition[] expectedPositions )
   {
      try
      {
         final List< IFlexViolation > violations = processFile( resourcePath );

         assertEquals( VIOLATIONS_NUMBER_NOT_CORRECT
                             + " for " + resourcePath,
                       expectedPositions.length,
                       violations.size() );

         if ( expectedPositions.length != 0 )
         {
            for ( int i = 0; i < expectedPositions.length; i++ )
            {
               final IFlexViolation violation = violations.get( i );
               final ViolationPosition expectedPosition = expectedPositions[ i ];

               assertEquals( BEGIN_LINE_NOT_CORRECT
                                   + " at " + i + "th violation on " + resourcePath,
                             expectedPosition.getBeginLine(),
                             violation.getBeginLine() );
               assertEquals( END_LINE_NOT_CORRECT
                                   + " at " + i + "th violation on " + resourcePath,
                             expectedPosition.getEndLine(),
                             violation.getEndLine() );
            }
         }
      }
      catch ( final IOException e )
      {
         fail( e.getMessage() );
      }
      catch ( final TokenException e )
      {
         fail( e.getMessage() );
      }
   }

   private StringBuffer buildMessageName( final Map< String, List< IFlexViolation >> violatedFiles )
   {
      final StringBuffer buffer = new StringBuffer( 100 );

      for ( final String violatedFileName : violatedFiles.keySet() )
      {
         final List< IFlexViolation > violations = violatedFiles.get( violatedFileName );

         buffer.append( violatedFileName
               + " should not contain any violations " + " (" + violations.size() + " found" );

         if ( violations.size() == 1 )
         {
            buffer.append( " at "
                  + violations.get( 0 ).getBeginLine() + ":" + violations.get( 0 ).getEndLine() );
         }
         buffer.append( ")\n" );
      }
      return buffer;
   }

   private Map< String, List< IFlexViolation >> extractActualViolatedFiles() throws IOException,
                                                                            TokenException
   {
      final Map< String, List< IFlexViolation > > violatedFiles = new HashMap< String, List< IFlexViolation > >();

      for ( final Map.Entry< String, IFlexFile > fileNameEntry : getTestFiles().entrySet() )
      {
         if ( !getViolatingFiles().containsKey( fileNameEntry.getKey() ) )
         {
            final List< IFlexViolation > violations = processFile( fileNameEntry.getKey() );

            if ( !violations.isEmpty() )
            {
               violatedFiles.put( fileNameEntry.getKey(),
                                  violations );
            }
         }
      }
      return violatedFiles;
   }
}