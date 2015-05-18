package demo.reactive.stream.of.supplier;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class SubscriptionOfSupplier<T> implements Subscription {
	final Subscriber<? super T> subscriber;
	private AtomicLong demand = new AtomicLong(1);
	private final ExecutorService executor;
	private final AtomicBoolean stop = new AtomicBoolean(false);
	private final BlockingQueue<T> queue;

	SubscriptionOfSupplier(Subscriber<? super T> s) {
		queue = new LinkedBlockingQueue<T>();
		executor = Executors.newSingleThreadExecutor();
		this.subscriber = s;
		subscriber.onSubscribe(this);
	}

	public static <T> SubscriptionOfSupplier<T> of(
			Subscriber<? super T> subscriber) {
		return new SubscriptionOfSupplier<T>(subscriber);
	}

	@Override
	public void cancel() {
		stop.set(true);
		subscriber.onComplete();
	}

	@Override
	public void request(long n) {
		demand.addAndGet(n);
		executor.submit(() -> start());
	}

	private void start() {
		try {
			T t;
			while (!stop.get() && demand.decrementAndGet() > 0) {
				t = queue.take();
				subscriber.onNext(t);
			}
			subscriber.onComplete();
		} catch (Throwable t) {
			subscriber.onError(t);
		}
	}

	public void send(T t) {
		try {
			queue.put(t);
		} catch (Throwable e) {
			subscriber.onError(e);
		}
	}
}
