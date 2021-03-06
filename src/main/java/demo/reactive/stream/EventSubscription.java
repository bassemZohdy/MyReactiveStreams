package demo.reactive.stream;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class EventSubscription<T> implements Subscription {
	final Subscriber<? super Event<T>> subscriber;
	private AtomicLong demand = new AtomicLong(1);
	private final BlockingQueue<Event<T>> queue;
	private final ExecutorService executor;
	private final AtomicBoolean stop = new AtomicBoolean(false);

	EventSubscription(Subscriber<? super Event<T>> s) {
		queue = new LinkedBlockingQueue<Event<T>>();
		executor = Executors.newSingleThreadExecutor();
		this.subscriber = s;
		subscriber.onSubscribe(this);
	}

	public static <T> EventSubscription<T> of(
			Subscriber<? super Event<T>> subscriber) {
		return new EventSubscription<T>(subscriber);
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
			Event<T> e;
			while (!stop.get() && demand.decrementAndGet() > 0) {
				e = queue.take();
				subscriber.onNext(e);
			}
			subscriber.onComplete();
		} catch (Throwable t) {
			subscriber.onError(t);
		}
	}

	public void send(Event<T> event) {
		try {
			queue.put(event);
		} catch (Throwable e) {
			subscriber.onError(e);
		}
	}
}
