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
package com.adobe.ac.pmd.rules.as3.event;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import org.junit.Test;

import com.adobe.ac.pmd.rules.as3.event.EventMissingCloneFunctionRule;
import com.adobe.ac.pmd.rules.core.AbstractAstFlexRuleTest;
import com.adobe.ac.pmd.rules.core.AbstractFlexRule;
import com.adobe.ac.pmd.rules.core.ViolationPosition;

public class EventMissingCloneFunctionRuleTest
      extends AbstractAstFlexRuleTest
{

   @Override
   @Test
   public void testProcessConcernedButNonViolatingFiles()
         throws FileNotFoundException, URISyntaxException
   {
      assertEmptyViolations( "com.adobe.ac.ncss.VoidConstructor.as" );

      assertEmptyViolations( "com.adobe.ac.ncss.event.DynamicCustomEvent.as" );
   }

   @Override
   @Test
   public void testProcessNonConcernedFiles() throws FileNotFoundException,
         URISyntaxException
   {
      assertEmptyViolations( "com.adobe.ac.ncss.mxml.IterationsList2.mxml" );
   }

   @Override
   @Test
   public void testProcessViolatingFiles() throws FileNotFoundException,
         URISyntaxException
   {
      assertViolations(
            "com.adobe.ac.ncss.event.FirstCustomEvent.as",
            new ViolationPosition[]
            { new ViolationPosition( -1, -1 ) } );

      assertViolations(
            "com.adobe.ac.ncss.event.SecondCustomEvent.as",
            new ViolationPosition[]
            { new ViolationPosition( -1, -1 ) } );
   }

   @Override
   protected AbstractFlexRule getRule()
   {
      return new EventMissingCloneFunctionRule();
   }
}