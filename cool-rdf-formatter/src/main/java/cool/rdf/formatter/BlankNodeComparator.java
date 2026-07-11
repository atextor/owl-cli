/*
 * Copyright Cool RDF project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cool.rdf.formatter;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shared.PrefixMapping;

import cool.rdf.formatter.blanknode.BlankNodeMetadata;

final class BlankNodeComparator implements Comparator<Resource> {

   private static final int MAX_DEPTH = 3;
   private static final int CACHE_SIZE = 100;

   private final Model model;
   private final PrefixMapping prefixMapping;
   private final BlankNodeMetadata blankNodeMetadata;
   private final boolean preserveBlankNodeLabelsAndOrdering;
   private final Map<Resource, String[]> keys = new LinkedHashMap<>( CACHE_SIZE, 0.75f, true ) {
      @Override
      protected boolean removeEldestEntry( final Map.Entry<Resource, String[]> eldest ) {
         return size() > CACHE_SIZE;
      }
   };

   BlankNodeComparator( final Model model, final PrefixMapping prefixMapping,
         final BlankNodeMetadata blankNodeMetadata, final boolean preserveBlankNodeLabelsAndOrdering ) {
      this.model = model;
      this.prefixMapping = prefixMapping;
      this.blankNodeMetadata = blankNodeMetadata;
      this.preserveBlankNodeLabelsAndOrdering = preserveBlankNodeLabelsAndOrdering;
   }

   @Override
   public int compare( final Resource left, final Resource right ) {
      if ( left.equals( right ) ) {
         return 0;
      }
      if ( preserveBlankNodeLabelsAndOrdering ) {
         final Long leftOrder = blankNodeMetadata.getOrder( left.asNode() );
         final Long rightOrder = blankNodeMetadata.getOrder( right.asNode() );
         if ( leftOrder != null || rightOrder != null ) {
            return Optional.ofNullable( leftOrder ).orElse( Long.MAX_VALUE )
                  .compareTo( Optional.ofNullable( rightOrder ).orElse( Long.MAX_VALUE ) );
         }
      }
      for ( int depth = 1; depth <= MAX_DEPTH; depth++ ) {
         final int result = key( left, depth ).compareTo( key( right, depth ) );
         if ( result != 0 ) {
            return result;
         }
      }
      return 0;
   }

   private String key( final Resource resource, final int depth ) {
      final String[] keysByDepth = keys.computeIfAbsent( resource, ignored -> new String[MAX_DEPTH] );
      String key = keysByDepth[depth - 1];
      if ( key == null ) {
         key = blankNodeKey( resource, depth, new HashSet<>() );
         keysByDepth[depth - 1] = key;
      }
      return key;
   }

   private String rdfNodeKey( final RDFNode node, final int depth, final Set<Resource> visitedBlankNodes ) {
      if ( node.isURIResource() ) {
         return "0U:" + prefixMapping.shortForm( node.asResource().getURI() );
      }
      if ( node.isLiteral() ) {
         final Literal literal = node.asLiteral();
         return "2L:" + literal.getDatatypeURI() + "|" + literal.getLanguage() + "|" + literal.getLexicalForm();
      }
      if ( node.isAnon() ) {
         return blankNodeKey( node.asResource(), depth, visitedBlankNodes );
      }
      return "3N:";
   }

   private String blankNodeKey( final Resource resource, final int depth,
         final Set<Resource> visitedBlankNodes ) {
      if ( depth <= 0 ) {
         return "1B";
      }
      if ( visitedBlankNodes.contains( resource ) ) {
         return "1BCYCLE";
      }

      final Set<Resource> nextVisitedBlankNodes = new HashSet<>( visitedBlankNodes );
      nextVisitedBlankNodes.add( resource );
      final List<String> outgoing = model.listStatements( resource, null, (RDFNode) null )
            .toList()
            .stream()
            .map( statement -> "S:" + predicateKey( statement.getPredicate() ) + "="
                  + rdfNodeKey( statement.getObject(), depth - 1, nextVisitedBlankNodes ) )
            .sorted()
            .toList();
      final List<String> incoming = model.listStatements( null, null, resource )
            .toList()
            .stream()
            .map( statement -> "O:" + rdfNodeKey( statement.getSubject(), depth - 1,
                  nextVisitedBlankNodes ) + "=" + predicateKey( statement.getPredicate() ) )
            .sorted()
            .toList();
      return "1B[" + String.join( ";", outgoing ) + "][" + String.join( ";", incoming ) + "]";
   }

   private String predicateKey( final Property predicate ) {
      return prefixMapping.shortForm( predicate.getURI() );
   }
}
