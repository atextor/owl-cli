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
