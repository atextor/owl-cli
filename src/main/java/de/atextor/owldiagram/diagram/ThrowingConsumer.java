package de.atextor.owldiagram.diagram;

/**
 * See {@link java.util.function.Consumer}
 *
 * @param <T> Thr processed type
 * @param <E> The exception type
 */
@FunctionalInterface
public interface ThrowingConsumer<T, E extends Throwable> {

    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     */
    void accept( T t ) throws E;
}
