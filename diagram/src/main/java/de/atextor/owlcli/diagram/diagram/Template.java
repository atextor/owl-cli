/*
 * Copyright 2021 Andreas Textor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.atextor.owlcli.diagram.diagram;

import java.util.Map;
import java.util.function.Function;

/**
 * Minimalistic String template. The template string can contain references to (e.g. ${foo}) that are replaced
 * with values on calling {@link #apply(Map)}.
 */
public class Template implements Function<Map<String, Object>, String> {
    final private String template;

    public Template( final String template ) {
        this.template = template;
    }

    /**
     * Apply the context to the template and return the resulting string
     *
     * @param context the context
     * @return the resulting string
     */
    @Override
    public String apply( final Map<String, Object> context ) {
        String result = template;
        for ( final Map.Entry<String, Object> entry : context.entrySet() ) {
            result = result.replace( "${" + entry.getKey() + "}", entry.getValue().toString() );
        }
        return result;
    }
}
