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
package com.adobe.ac.pmd.files.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.adobe.ac.pmd.files.IMxmlFile;

class MxmlFile extends AbstractFlexFile implements IMxmlFile
{
   private boolean  mainApplication = false;
   private String[] scriptBlock;

   protected MxmlFile( final File file,
                       final File rootDirectory )
   {
      super( file, rootDirectory );

      computeIfIsMainApplication();
      extractScriptBlock();
   }

   @Override
   public final boolean doesCurrentLineContainOneLineComment( final String line )
   {
      return false;
   }

   @Override
   public final String getCommentClosingTag()
   {
      return "-->";
   }

   @Override
   public final String getCommentOpeningTag()
   {
      return "<!--";
   }

   public final String[] getScriptBlock()
   {
      return scriptBlock;
   }

   @Override
   public final boolean isMainApplication()
   {
      return mainApplication;
   }

   @Override
   public final boolean isMxml()
   {
      return true;
   }

   private void computeIfIsMainApplication()
   {
      for ( final String line : getLines() )
      {
         if ( line.contains( "Application " )
               && line.charAt( 0 ) == '<' )
         {
            mainApplication = true;
            break;
         }
      }
   }

   private void copyScriptLinesKeepingOriginalLineIndices( final int startLine,
                                                           final int endLine )
   {
      final List< String > scriptLines = fillMxmlLine( startLine,
                                                       endLine );
      final String firstLine = "package "
            + getPackageName() + "{";
      final String secondLine = "class "
            + getClassName().split( "\\." )[ 0 ] + "{";

      scriptLines.set( 0,
                       firstLine );
      scriptLines.set( 1,
                       secondLine );
      scriptLines.set( scriptLines.size() - 1,
                       "}}" );

      scriptBlock = scriptLines.toArray( new String[ scriptLines.size() ] );
   }

   private void extractScriptBlock()
   {
      int currentLineIndex = 0;
      int startLine = 0;
      int endLine = 0;

      for ( final String line : getLines() )
      {
         if ( line.contains( "Script>" ) )
         {
            if ( line.contains( "</" ) )
            {
               endLine = currentLineIndex - 1;
               break;
            }
            else if ( line.contains( "<" ) )
            {
               startLine = currentLineIndex + 2;
            }
         }
         currentLineIndex++;
      }

      copyScriptLinesKeepingOriginalLineIndices( startLine,
                                                 endLine );
   }

   private List< String > fillMxmlLine( final int startLine,
                                        final int endLine )
   {
      final List< String > scriptLines = new ArrayList< String >();

      for ( int j = 0; j < startLine; j++ )
      {
         scriptLines.add( "" );
      }
      scriptLines.addAll( new ArrayList< String >( getLines() ).subList( startLine,
                                                                         endLine ) );
      for ( int j = endLine; j < getLines().size(); j++ )
      {
         scriptLines.add( "" );
      }
      return scriptLines;
   }
}