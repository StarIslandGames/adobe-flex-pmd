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
package com.adobe.ac.pmd.rules.as3;

import org.junit.Test;

import com.adobe.ac.pmd.rules.core.AbstractRegexpBasedRule;
import com.adobe.ac.pmd.rules.core.ViolationPosition;
import com.adobe.ac.pmd.rules.core.test.AbstractRegExpBasedRuleTest;

public class ViewComponentReferencedInModelRuleTest extends AbstractRegExpBasedRuleTest
{
   @Override
   @Test
   public void testProcessConcernedButNonViolatingFiles()
   {
      assertEmptyViolations( "cairngorm.BindableModelLocator.as" );
      assertEmptyViolations( "cairngorm.events.CorrectConstructorEvent.as" );
      assertEmptyViolations( "com.adobe.ac.ncss.BigModel.as" );
      assertEmptyViolations( "cairngorm.NonBindableModelLocator.as" );
   }

   @Override
   @Test
   public void testProcessNonConcernedFiles()
   {
      assertEmptyViolations( "com.adobe.ac.ncss.mxml.IterationsList.mxml" );
   }

   @Override
   @Test
   public void testProcessViolatingFiles()
   {
      assertViolations( "com.adobe.ac.ncss.BigImporterModel.as",
                        new ViolationPosition[]
                        { new ViolationPosition( 35, 35 ) } );
   }

   @Override
   protected String[] getMatchableLines()
   {
      return new String[]
      { "import lala.view.MyObject;",
                  "import MyObject   ",
                  "   import lala.view.MyObject" };
   }

   @Override
   protected AbstractRegexpBasedRule getRegexpBasedRule()
   {
      return new ViewComponentReferencedInModelRule();
   }

   @Override
   protected String[] getUnmatchableLines()
   {
      return new String[]
      { "mport lala.view.MyObject",
                  " text=\"{ vfrfr().frfr.frf.lala }\"/>",
                  " text=\"{vfrfr().frfr.frf.lala}\"/>",
                  "public dynamic class DynamicObject {",
                  "dynamic public class DynamicObject" };
   }
}