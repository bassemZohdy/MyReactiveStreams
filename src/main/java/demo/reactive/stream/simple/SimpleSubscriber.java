package demo.reactive.stream.simple;

import java.util.function.Consumer;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class SimpleSubscriber<T> implements Subscriber<T> {
	private final Consumer<T> onNext;
	private final Consumer<Throwable> onError;
	private final Runnable onComplete;
	private SimpleSubscription<T> subscription;
	private volatile boolean completed = false;

	private SimpleSubscriber(Consumer<T> onNext) {
		this.onNext = onNext;
		this.onComplete = null;
		this.onError = null;
	}

	private SimpleSubscriber(Consumer<T> onNext, Consumer<Throwable> onError,
			Runnable onComplete) {
		this.onNext = onNext;
		this.onComplete = onComplete;
		this.onError = onError;
	}

	public static <T> SimpleSubscriber<T> of(Consumer<T> onNext) {
		return new SimpleSubscriber<T>(onNext);
	}

	public static <T> SimpleSubscriber<T> of(Consumer<T> onNext,
			Consumer<Throwable> onError, Runnable onComplete) {
		return new SimpleSubscriber<T>(onNext, onError, onComplete);
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
			onNext.accept(t);
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
