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
package com.adobe.ac.pmd.nodes.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.adobe.ac.pmd.nodes.IFunction;
import com.adobe.ac.pmd.nodes.IIdentifierNode;
import com.adobe.ac.pmd.nodes.IMetaData;
import com.adobe.ac.pmd.nodes.IParameter;
import com.adobe.ac.pmd.nodes.MetaData;
import com.adobe.ac.pmd.nodes.Modifier;
import com.adobe.ac.pmd.nodes.utils.MetaDataUtils;
import com.adobe.ac.pmd.parser.IParserNode;
import com.adobe.ac.pmd.parser.KeyWords;
import com.adobe.ac.pmd.parser.NodeKind;

class FunctionNode extends AbstractNode implements IFunction
{
   private IParserNode                              asDoc;
   private IParserNode                              body;
   private int                                      cyclomaticComplexity;
   private final Map< String, IParserNode >         localVariables;
   private final Map< MetaData, List< IMetaData > > metaDataList;
   private final Set< Modifier >                    modifiers;
   private IdentifierNode                           name;
   private final List< IParameter >                 parameters;
   private IIdentifierNode                          returnType;

   protected FunctionNode( final IParserNode node )
   {
      super( node );

      modifiers = new HashSet< Modifier >();
      metaDataList = new LinkedHashMap< MetaData, List< IMetaData > >();
      localVariables = new LinkedHashMap< String, IParserNode >();
      parameters = new ArrayList< IParameter >();
      name = null;
   }

   public void add( final IMetaData metaData )
   {
      final MetaData metaDataImpl = MetaData.create( metaData.getName() );

      if ( !metaDataList.containsKey( metaDataImpl ) )
      {
         metaDataList.put( metaDataImpl,
                           new ArrayList< IMetaData >() );
      }
      metaDataList.get( metaDataImpl ).add( metaData );
   }

   public void add( final Modifier modifier )
   {
      modifiers.add( modifier );
   }

   @Override
   public FunctionNode compute()
   {
      if ( getInternalNode().numChildren() != 0 )
      {
         for ( final IParserNode node : getInternalNode().getChildren() )
         {
            if ( node.is( NodeKind.BLOCK ) )
            {
               computeFunctionContent( node );
            }
            else if ( node.is( NodeKind.NAME ) )
            {
               name = IdentifierNode.create( node );
            }
            else if ( node.is( NodeKind.MOD_LIST ) )
            {
               computeModifierList( this,
                                    node );
            }
            else if ( node.is( NodeKind.PARAMETER_LIST ) )
            {
               computeParameterList( node );
            }
            else if ( node.is( NodeKind.TYPE ) )
            {
               returnType = IdentifierNode.create( node );
            }
            else if ( node.is( NodeKind.META_LIST ) )
            {
               MetaDataUtils.computeMetaDataList( this,
                                                  node );
            }
            else if ( node.is( NodeKind.AS_DOC ) )
            {
               asDoc = node;
            }
         }
      }
      return this;
   }

   public List< IParserNode > findPrimaryStatementInBody( final String[] primaryNames )
   {
      return body == null ? null
                         : body.findPrimaryStatementsFromNameInChildren( primaryNames );
   }

   public List< IParserNode > findPrimaryStatementsInBody( final String primaryName )
   {
      return body == null ? new ArrayList< IParserNode >()
                         : body.findPrimaryStatementsFromNameInChildren( new String[]
                         { primaryName } );
   }

   public IParserNode getAsDoc()
   {
      return asDoc;
   }

   public IParserNode getBody()
   {
      return body;
   }

   public int getCyclomaticComplexity()
   {
      return cyclomaticComplexity;
   }

   public Map< String, IParserNode > getLocalVariables()
   {
      return localVariables;
   }

   public List< IMetaData > getMetaData( final MetaData metaDataName )
   {
      if ( metaDataList.containsKey( metaDataName ) )
      {
         return metaDataList.get( metaDataName );
      }
      else
      {
         return new ArrayList< IMetaData >();
      }
   }

   public int getMetaDataCount()
   {
      return metaDataList.size();
   }

   public String getName()
   {
      return name.toString();
   }

   public List< IParameter > getParameters()
   {
      return parameters;
   }

   public IIdentifierNode getReturnType()
   {
      return returnType;
   }

   public int getStatementNbInBody()
   {
      return 1 + getStatementInNode( body );
   }

   public IParserNode getSuperCall()
   {
      if ( body != null
            && body.numChildren() != 0 )
      {
         for ( final IParserNode childContent : body.getChildren() )
         {
            if ( NodeKind.CALL.equals( childContent.getId() )
                  || NodeKind.DOT.equals( childContent.getId() ) )
            {
               for ( final IParserNode childCall : childContent.getChildren() )
               {
                  if ( KeyWords.SUPER.toString().equals( childCall.getStringValue() ) )
                  {
                     return childContent;
                  }
               }
            }
         }
      }
      return null;
   }

   public boolean is( final Modifier modifier ) // NOPMD
   {
      return modifiers.contains( modifier );
   }

   public boolean isGetter()
   {
      return getInternalNode().is( NodeKind.GET );
   }

   public boolean isOverriden()
   {
      return is( Modifier.OVERRIDE );
   }

   public boolean isPublic()
   {
      return is( Modifier.PUBLIC );
   }

   public boolean isSetter()
   {
      return getInternalNode().is( NodeKind.SET );
   }

   private void computeCyclomaticComplexity()
   {
      cyclomaticComplexity = 1 + body.computeCyclomaticComplexity();
   }

   private void computeFunctionContent( final IParserNode functionBodyNode )
   {
      body = functionBodyNode;

      computeCyclomaticComplexity();
      computeVariableList( body );
   }

   private void computeParameterList( final IParserNode node )
   {
      if ( node.numChildren() != 0 )
      {
         for ( final IParserNode parameterNode : node.getChildren() )
         {
            parameters.add( FormalNode.create( parameterNode ) );
         }
      }
   }

   private void computeVariableList( final IParserNode node )
   {
      if ( node.is( NodeKind.VAR_LIST ) )
      {
         localVariables.put( node.getChild( 0 ).getChild( 0 ).getStringValue(),
                             node );
      }
      else if ( node.numChildren() > 0 )
      {
         for ( final IParserNode child : node.getChildren() )
         {
            computeVariableList( child );
         }
      }
   }

   private int getStatementInNode( final IParserNode node )
   {
      int statementNb = 0;

      if ( node != null
            && node.numChildren() != 0 )
      {
         int lastLine = node.getChild( 0 ).getLine();
         for ( final IParserNode childContent : node.getChildren() )
         {
            if ( childContent.getLine() != lastLine )
            {
               lastLine = childContent.getLine();
               statementNb++;
            }
            statementNb += getStatementInNode( childContent );
         }
      }

      return statementNb;
   }
}
