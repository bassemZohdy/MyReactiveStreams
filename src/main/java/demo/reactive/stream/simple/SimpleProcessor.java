package demo.reactive.stream.simple;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.reactivestreams.Processor;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class SimpleProcessor<T> implements Processor<T, T> {

	private final Supplier<T> supplier;
	private final Consumer<T> onNext;
	private final Consumer<Throwable> onError;
	private final Runnable onComplete;
	private SimpleSubscription<T> subscription;
	private volatile boolean completed = false;

//	private final ExecutorService executor = Executors
//			.newSingleThreadExecutor();

	@Override
	public void subscribe(Subscriber<? super T> subscriber) {
		new SimpleSubscription<T>(subscriber, supplier);
	}

	private SimpleProcessor(Supplier<T> supplier, Consumer<T> onNext) {
		this.supplier = supplier;
		this.onNext = onNext;
		this.onComplete = null;
		this.onError = null;
		this.subscribe(this);
	}

	private SimpleProcessor(Supplier<T> supplier, Consumer<T> onNext,
			Consumer<Throwable> onError, Runnable onComplete) {
		this.supplier = supplier;
		this.onNext = onNext;
		this.onComplete = onComplete;
		this.onError = onError;
		this.subscribe(this);
	}

	public static <T> SimpleProcessor<T> of(Supplier<T> supplier,
			Consumer<T> onNext) {
		return new SimpleProcessor<T>(supplier, onNext);
	}

	public static <T> SimpleProcessor<T> of(Supplier<T> supplier,
			Consumer<T> onNext, Consumer<Throwable> onError, Runnable onComplete) {
		return new SimpleProcessor<T>(supplier, onNext, onError, onComplete);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onSubscribe(Subscription subscription) {
		this.subscription = (SimpleSubscription<T>) subscription;
		subscription.request(1);
	}

	@Override
	public void onNext(T t) {
		if (onNext != null)
//			executor.submit(() -> 
			onNext.accept(t);
//			);
		if (!completed)
			subscription.request(1);
	}

	@Override
	public void onError(Throwable t) {
		if (onError != null)
			onError.accept(t);
	}

	@Override
	public void onComplete() {
		completed = true;
		if (onComplete != null)
			onComplete.run();

	}
}
