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
package com.adobe.ac.pmd.rules.architecture;

import java.util.HashMap;
import java.util.Map;

import com.adobe.ac.pmd.rules.core.AbstractAstFlexRuleTest;
import com.adobe.ac.pmd.rules.core.AbstractFlexRule;
import com.adobe.ac.pmd.rules.core.ViolationPosition;

public class UseInternalClassOutsideApiClassTest extends AbstractAstFlexRuleTest
{
   @Override
   protected Map< String, ViolationPosition[] > getExpectedViolatingFiles()
   {
      final HashMap< String, ViolationPosition[] > violatedFiles = new HashMap< String, ViolationPosition[] >();

      addToMap( addToMap( addToMap( addToMap( violatedFiles,
                                              "functional.func2.restricted.Func2RestrictedClass.as",
                                              new ViolationPosition[]
                                              { new ViolationPosition( 34, 34 ) } ),
                                    "functional.func1.restricted.Func1RestrictedClass.as",
                                    new ViolationPosition[]
                                    { new ViolationPosition( 35, 35 ) } ),
                          "functional.func2.api.Func2ExposedClass.as",
                          new ViolationPosition[]
                          { new ViolationPosition( 34, 34 ) } ),
                "functional.func1.api.Func1ExposedClass.as",
                new ViolationPosition[]
                { new ViolationPosition( 36, 36 ) } );

      addToMap( violatedFiles,
                "functional.FunctionalClient.as",
                new ViolationPosition[]
                { new ViolationPosition( 34, 34 ),
                            new ViolationPosition( 36, 36 ) } );

      return violatedFiles;
   }

   @Override
   protected AbstractFlexRule getRule()
   {
      return new UseInternalClassOutsideApiClass();
   }
}