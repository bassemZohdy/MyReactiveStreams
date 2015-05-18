package demo.reactive.stream.of.supplier;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Supplier;

public class IterableOfSupplier<T> implements Iterable<T> {
	final private Supplier<T> supplier;

	private IterableOfSupplier(Supplier<T> supplier) {
		this.supplier = supplier;
	}

	public static <T> IterableOfSupplier<T> of(Supplier<T> supplier) {
		return new IterableOfSupplier<T>(supplier);
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			Optional<T> next = Optional.empty();

			@Override
			public boolean hasNext() {
				if (next.isPresent()) {
					return true;
				} else {
					next = Optional.ofNullable(supplier.get());
					return next.isPresent();
				}
			}

			@Override
			public T next() {
				if (next.isPresent() || hasNext()) {
					T result = next.get();
					next = Optional.empty();
					return result;
				} else {
					throw new NoSuchElementException();
				}
			}

		};
	}
}
