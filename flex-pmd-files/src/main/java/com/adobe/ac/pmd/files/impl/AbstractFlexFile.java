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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import com.adobe.ac.pmd.StackTraceUtils;
import com.adobe.ac.pmd.files.IFlexFile;

/**
 * Abstract class representing a Flex File (either MXML or AS)
 * 
 * @author xagnetti
 */
abstract class AbstractFlexFile implements IFlexFile
{
   private static final Logger LOGGER = Logger.getLogger( AbstractFlexFile.class.getName() );

   private static final boolean doesCurrentLineContain( final String line,
                                                        final String search )
   {
      return line.contains( search );
   }

   private final String         className;
   private final File           file;
   private final List< String > lines;
   private final String         packageName;

   protected AbstractFlexFile( final File underlyingFile,
                               final File rootDirectory )
   {
      final String filePath = underlyingFile.getPath();
      final CharSequence rootPath = rootDirectory.getPath();

      file = underlyingFile;
      className = underlyingFile.getName();
      packageName = computePackageName( filePath,
                                        rootPath );
      lines = new ArrayList< String >();
      try
      {
         String[] linesArray;
         linesArray = FileUtils.readStrings( underlyingFile );
         for ( final String string : linesArray )
         {
            lines.add( string );
         }
      }
      catch ( final IOException e )
      {
         LOGGER.warning( StackTraceUtils.print( e ) );
      }
   }

   /*
    * (non-Javadoc)
    * @seecom.adobe.ac.pmd.files.IFlexFile#compareTo(com.adobe.ac.pmd.files.
    * AbstractFlexFile)
    */
   public final int compareTo( final IFlexFile otherViolation )
   {
      return getFilename().compareTo( otherViolation.getFilename() );
   }

   /*
    * (non-Javadoc)
    * @see com.adobe.ac.pmd.files.IFlexFile#contains(java.lang.String, int)
    */
   public final boolean contains( final String stringToLookup,
                                  final Set< Integer > linesToBeIgnored )
   {
      int lineIndex = 1;
      boolean found = false;

      for ( final String line : lines )
      {
         if ( doesCurrentLineContain( line,
                                      stringToLookup )
               && !linesToBeIgnored.contains( lineIndex ) )
         {
            found = true;
            break;
         }
         lineIndex++;
      }
      return found;
   }

   /*
    * (non-Javadoc)
    * @see com.adobe.ac.pmd.files.IFlexFile#getClassName()
    */
   public final String getClassName()
   {
      return className;
   }

   /*
    * (non-Javadoc)
    * @see com.adobe.ac.pmd.files.IFlexFile#getCommentClosingTag()
    */
   public abstract String getCommentClosingTag();

   /*
    * (non-Javadoc)
    * @see com.adobe.ac.pmd.files.IFlexFile#getCommentOpeningTag()
    */
   public abstract String getCommentOpeningTag();

   /*
    * (non-Javadoc)
    * @see com.adobe.ac.pmd.files.IFlexFile#getFilename()
    */
   public final String getFilename()
   {
      return file.getName();
   }

   /*
    * (non-Javadoc)
    * @see com.adobe.ac.pmd.files.IFlexFile#getFilePath()
    */
   public final String getFilePath()
   {
      return file.toURI().getPath();
   }

   /*
    * (non-Javadoc)
    * @see com.adobe.ac.pmd.files.IFlexFile#getFullyQualifiedName()
    */
   public final String getFullyQualifiedName()
   {
      return ( StringUtils.isEmpty( packageName ) ? ""
                                                 : packageName
                                                       + "." )
            + className;
   }

   public String getLineAt( final int lineIndex )
   {
      return lines.get( lineIndex - 1 );
   }

   /*
    * (non-Javadoc)
    * @see com.adobe.ac.pmd.files.IFlexFile#getLines()
    */
   public final List< String > getLines()
   {
      return lines;
   }

   public int getLinesNb()
   {
      return lines.size();
   }

   /*
    * (non-Javadoc)
    * @see com.adobe.ac.pmd.files.IFlexFile#getPackageName()
    */
   public final String getPackageName()
   {
      return packageName;
   }

   /*
    * (non-Javadoc)
    * @see com.adobe.ac.pmd.files.IFlexFile#isMainApplication()
    */
   public abstract boolean isMainApplication();

   /*
    * (non-Javadoc)
    * @see com.adobe.ac.pmd.files.IFlexFile#isMxml()
    */
   public abstract boolean isMxml();

   private String computePackageName( final String filePath,
                                      final CharSequence rootPath )
   {
      String temporaryPackage;

      temporaryPackage = filePath.replace( className,
                                           "" )
                                 .replace( rootPath,
                                           "" )
                                 .replace( System.getProperty( "file.separator" ),
                                           "." );
      if ( temporaryPackage.endsWith( "." ) )
      {
         temporaryPackage = temporaryPackage.substring( 0,
                                                        temporaryPackage.length() - 1 );
      }
      if ( temporaryPackage.length() > 0
            && temporaryPackage.charAt( 0 ) == '.' )
      {
         temporaryPackage = temporaryPackage.substring( 1,
                                                        temporaryPackage.length() );
      }
      return temporaryPackage;
   }
}
