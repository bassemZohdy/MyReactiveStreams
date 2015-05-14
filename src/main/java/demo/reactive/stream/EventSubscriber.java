package demo.reactive.stream;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class EventSubscriber<T> implements Subscriber<Event<T>> {
	private Subscription subscription;
	@Override
	public void onComplete() {
		System.out.println(this.getClass().getName() + " complete");
	}

	@Override
	public void onError(Throwable t) {
		System.err.println(this.getClass().getName() + " error: "
				+ t.getMessage());
	}

	@Override
	public void onNext(Event<T> event) {
		System.out.println(this.getClass().getName() + " next: " + event);
		//request another one
		subscription.request(1);
	}

	@Override
	public void onSubscribe(Subscription s) {
		System.out.println(this.getClass().getName() + " Subscription: " + s);
		if (subscription != null) {
			s.cancel();
		} else {
			subscription = s;
			s.request(1);
		}
	}
}
