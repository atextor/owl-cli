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

package cool.rdf.ret.cli.graal.feature;

import com.oracle.svm.core.jni.JNIRuntimeAccess;
import org.graalvm.nativeimage.Platform;
import org.graalvm.nativeimage.Platforms;
import org.graalvm.nativeimage.hosted.RuntimeReflection;
import org.graalvm.nativeimage.hosted.RuntimeResourceAccess;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Platforms( Platform.HOSTED_ONLY.class )
public class Native {
    private final Class<?> clazz;

    Native( final Class<?> clazz ) {
        this.clazz = clazz;
    }

    public static Native forClass( final Class<?> clazz ) {
        return new Native( clazz );
    }

    public static Native forClass( final String fullyQualifiedJavaClass ) {
        final Class<?> clazz;
        try {
            clazz = Class.forName( fullyQualifiedJavaClass, false, Native.class.getClassLoader() );
        } catch ( final ClassNotFoundException exception ) {
            throw new RuntimeException( exception );
        }
        return forClass( clazz );
    }

    public Native initializeAtBuildTime() {
        org.graalvm.nativeimage.hosted.RuntimeClassInitialization.initializeAtBuildTime( clazz );
        return this;
    }

    public Native registerClass() {
        RuntimeReflection.register( clazz );
        return this;
    }

    public Native registerAllFields() {
        for ( final Field field : clazz.getDeclaredFields() ) {
            RuntimeReflection.register( field );
        }
        return this;
    }

    public Native registerFields( @Nonnull final String... fieldNames ) {
        for ( final Field field : clazz.getDeclaredFields() ) {
            for ( final String targetField : fieldNames ) {
                if ( field.getName().equals( targetField ) ) {
                    RuntimeReflection.register( field );
                }
            }
        }
        return this;
    }

    public Native registerAllConstructors() {
        for ( final Constructor<?> constructor : clazz.getDeclaredConstructors() ) {
            RuntimeReflection.register( constructor );
        }
        return this;
    }

    public Native registerConstructor( final Class<?>... args ) {
        try {
            RuntimeReflection.register( clazz.getDeclaredConstructor( args ) );
        } catch ( final NoSuchMethodException exception ) {
            throw new RuntimeException( exception );
        }
        return this;
    }

    public Native registerAllMethods() {
        for ( final Method method : clazz.getDeclaredMethods() ) {
            RuntimeReflection.register( method );
        }
        return this;
    }

    public Native registerMethod( final String name, final Class<?>... args ) {
        try {
            RuntimeReflection.register( clazz.getDeclaredMethod( name, args ) );
        } catch ( final NoSuchMethodException exception ) {
            throw new RuntimeException( exception );
        }
        return this;
    }

    public Native registerClassForJni() {
        JNIRuntimeAccess.register( clazz );
        return this;
    }

    public Native registerMethodForJni( final String name, final Class<?>... args ) {
        try {
            JNIRuntimeAccess.register( clazz.getDeclaredMethod( name, args ) );
        } catch ( final NoSuchMethodException exception ) {
            throw new RuntimeException( exception );
        }
        return this;
    }

    public static WithModule withModule( final Module module ) {
        return new WithModule( module );
    }

    public static WithModule withModule( final String moduleName ) {
        return new WithModule( moduleName );
    }

    static class WithModule {
        private final Module module;

        WithModule( final Module module ) {
            this.module = module;
        }

        WithModule( final String moduleName ) {
            this( ModuleLayer.boot().findModule( moduleName ).orElseThrow( RuntimeException::new ) );
        }

        public WithModule addResource( final String resource ) {
            RuntimeResourceAccess.addResource( module, resource );
            return this;
        }

        public WithModule addResourceBundle( final String resourceBundle ) {
            RuntimeResourceAccess.addResourceBundle( module, resourceBundle );
            return this;
        }
    }
}
