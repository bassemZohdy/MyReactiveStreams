package demo.reactive.stream.of.supplier;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class PublisherOfSupplier<T> implements Publisher<T>, Runnable {
	final private AtomicInteger keyGenrator = new AtomicInteger(0);
	final private Map<Integer, SubscriptionOfSupplier<T>> subscriptionMap = new HashMap<>();
	final private Map<Integer, Predicate<T>> predicateMap = new HashMap<>();
	final private Iterator<T> iterator;

	private PublisherOfSupplier(Supplier<T> supplier) {
		this.iterator = IterableOfSupplier.of(supplier).iterator();
	}

	public static <T> PublisherOfSupplier<T> of(Supplier<T> supplier) {
		return new PublisherOfSupplier<T>(supplier);
	}

	public void complete() {
		subscriptionMap.values().forEach(s -> s.cancel());
		subscriptionMap.clear();
	}

	@Override
	public void subscribe(Subscriber<? super T> s) {
		int key = keyGenrator.getAndIncrement();
		subscriptionMap.put(key, SubscriptionOfSupplier.of(s));
		predicateMap.put(key, (t) -> true);

	}

	public void subscribe(Predicate<T> predicate, Subscriber<? super T> s) {
		int key = keyGenrator.getAndIncrement();
		subscriptionMap.put(key, SubscriptionOfSupplier.of(s));
		predicateMap.put(key, predicate);
	}

	@Override
	public void run() {
		while (iterator.hasNext()) {
			T next = iterator.next();
			subscriptionMap.entrySet().stream()
					.filter(e -> predicateMap.get(e.getKey()).test(next))
					.forEach(e -> e.getValue().send(next));
		}
	}

}
