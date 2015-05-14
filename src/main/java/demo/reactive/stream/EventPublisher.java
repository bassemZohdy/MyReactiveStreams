package demo.reactive.stream;

import java.util.HashSet;
import java.util.Set;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class EventPublisher<T> implements Publisher<Event<T>> {
	final private Set<EventSubscription<T>> subscriptionSet = new HashSet<>();

	public void send(T t) {
		subscriptionSet.forEach(s -> s.send(Event.of(t)));
	}

	public void complete() {
		subscriptionSet.forEach(s -> s.cancel());
		subscriptionSet.clear();
	}

	@Override
	public void subscribe(Subscriber<? super Event<T>> s) {
		subscriptionSet.add(EventSubscription.of(s));
	}

}
