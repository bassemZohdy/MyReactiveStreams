package demo.reactive.stream.simple;

import java.util.function.Supplier;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class SimplePublisher<T> implements Publisher<T> {
	private final Supplier<T> supplier;

	private SimplePublisher(Supplier<T> supplier) {
		this.supplier = supplier;
	}

	public static <T> SimplePublisher<T> of(Supplier<T> supplier) {
		return new SimplePublisher<T>(supplier);
	}

	@Override
	public void subscribe(Subscriber<? super T> subscriber) {
		new SimpleSubscription<T>(subscriber, supplier);
	}

}
