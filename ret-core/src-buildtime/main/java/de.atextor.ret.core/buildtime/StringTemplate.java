/*
 * Copyright 2024 Andreas Textor
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

package de.atextor.ret.core.buildtime;

import java.util.Map;

public class StringTemplate {
    private final String template;

    public StringTemplate( final String template ) {
        this.template = template;
    }

    public String render( final Map<String, Object> values ) {
        String result = template;
        for ( final Map.Entry<String, Object> entry : values.entrySet() ) {
            result = result.replace( "${" + entry.getKey() + "}", entry.getValue().toString() );
        }
        return result;
    }
}
