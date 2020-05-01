package de.atextor.owlcli;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class ResourceArgumentsProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments( final ExtensionContext context ) {
        return Arrays.stream( Objects
            .requireNonNull( Path.of( System.getProperty( "user.dir" ), "src", "test", "resources" ).toFile()
                .listFiles( ( dir, name ) -> name.endsWith( ".ttl" ) ) ) )
            .map( File::getName )
            .map( filename -> filename.replace( ".ttl", "" ) )
            .sorted()
            .map( FilenameArguments::new );
    }

    public static class FilenameArguments implements Arguments {
        String filename;

        public FilenameArguments( final String filename ) {
            this.filename = filename;
        }

        @Override
        public Object[] get() {
            return new Object[]{ filename };
        }
    }
}
