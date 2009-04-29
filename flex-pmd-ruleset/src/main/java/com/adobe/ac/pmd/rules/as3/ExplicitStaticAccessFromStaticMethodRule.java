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

import com.adobe.ac.pmd.nodes.utils.ClassUtils;
import com.adobe.ac.pmd.rules.core.AbstractAstFlexRule;
import com.adobe.ac.pmd.rules.core.ViolationPriority;

import de.bokelberg.flex.parser.KeyWords;
import de.bokelberg.flex.parser.Node;

public class ExplicitStaticAccessFromStaticMethodRule
      extends AbstractAstFlexRule
{
   private String className;
   private boolean isInNewExpression = false;

   @Override
   protected ViolationPriority getDefaultPriority()
   {
      return ViolationPriority.INFO;
   }

   @Override
   protected void visitClass(
         final Node ast )
   {
      className = ClassUtils.getClassNameFromClassNode( ast );

      super.visitClass( ast );
   }

   @Override
   protected void visitExpression(
         final Node statement )
   {
      if ( statement != null
            && className != null )
      {
         detectArgument( statement );
         detectViolation( statement );
         detectNewOperator( statement );
      }
      super.visitExpression( statement );
      isInNewExpression = false;
   }

   private void detectArgument(
         final Node statement )
   {
      if ( statement.children != null
            && statement.is( Node.ARGUMENTS ) )
      {
         for ( final Node child : statement.children )
         {
            visitExpression( child );
         }
      }
   }

   private void detectNewOperator(
         final Node statement )
   {
      if ( statement.is( KeyWords.NEW ) )
      {
         isInNewExpression = true;
      }
   }

   private void detectViolation(
         final Node statement )
   {

      if ( !isInNewExpression
            && statement.stringValue != null
            && !statement.stringValue.equals( "" )
            && statement.stringValue.equals( className ) )
      {
         addViolation(
               statement, statement );
      }
   }
}
