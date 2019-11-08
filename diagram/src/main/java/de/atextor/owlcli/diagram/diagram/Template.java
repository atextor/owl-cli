package de.atextor.owlcli.diagram.diagram;

import java.util.Map;
import java.util.function.Function;

public class Template implements Function<Map<String, Object>, String> {
    final private String template;

    public Template( final String template ) {
        this.template = template;
    }

    @Override
    public String apply( final Map<String, Object> context ) {
        String result = template;
        for ( final Map.Entry<String, Object> entry : context.entrySet() ) {
            result = result.replace( "${" + entry.getKey() + "}", entry.getValue().toString() );
        }
        return result;
    }
}
