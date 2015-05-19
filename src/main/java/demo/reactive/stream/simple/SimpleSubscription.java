package demo.reactive.stream.simple;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class SimpleSubscription<T> implements Subscription {
	private final Supplier<T> supplier;
	private final Predicate<T> predicate = t -> t != null;
	private final Subscriber<? super T> subscriber;
	private final ExecutorService executor = Executors
			.newSingleThreadExecutor();

	public SimpleSubscription(Subscriber<? super T> subscriber,
			Supplier<T> supplier) {
		this.subscriber = subscriber;
		this.supplier = supplier;
		subscriber.onSubscribe(this);
	}

	@Override
	public void request(long n) {
		executor.submit(() -> {
			try {
				for (int i = 0; i < n; i++) {
					T t = supplier.get();
					if (!predicate.test(t)) {
						cancel();
						break;
					}
					subscriber.onNext(t);
				}
			} catch (Throwable t) {
				subscriber.onError(t);
			}
		});
	}

	@Override
	public void cancel() {
		subscriber.onComplete();
	}

}
