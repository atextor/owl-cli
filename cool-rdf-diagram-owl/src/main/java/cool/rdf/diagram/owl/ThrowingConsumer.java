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

package cool.rdf.diagram.owl;

/**
 * See {@link java.util.function.Consumer}
 *
 * @param <T> The processed type
 * @param <E> The exception type
 */
@FunctionalInterface
public interface ThrowingConsumer<T, E extends Throwable> {

    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     * @throws E some exception
     */
    void accept( T t ) throws E;
}
