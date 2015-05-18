package demo.reactive.stream.of.supplier;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		ModelSupplier supplier = new ModelSupplier();
		PublisherOfSupplier<Model> publisher = PublisherOfSupplier.of(supplier);
		Predicate<Model> p1 = (m) -> {
			return m.getNumber() % 2 == 0;
		};
		Predicate<Model> p2 = (m) -> {
			return m.getNumber() % 2 == 1;
		};
		publisher.subscribe(p1, new ModelSubscriber("Subscriber1 "));
		publisher.subscribe(p2, new ModelSubscriber("Subscriber2 "));
		Executors.newSingleThreadExecutor().submit(publisher);
		Thread.sleep(1000);
		publisher.complete();
	}
}

class ModelSubscriber implements Subscriber<Model> {
	private Subscription subscription;
	private final String name;

	public ModelSubscriber(String name) {
		this.name = name;
	}

	@Override
	public void onComplete() {
		System.out.println(name + " complete");
	}

	@Override
	public void onError(Throwable t) {
		System.err.println(name + " error: " + t.getMessage());
	}

	@Override
	public void onNext(Model model) {
		System.out.println(name + " next: " + model);
		// request another one
		subscription.request(1);
	}

	@Override
	public void onSubscribe(Subscription s) {
		System.out.println(name + " Subscription: " + s);
		if (subscription != null) {
			s.cancel();
		} else {
			subscription = s;
			s.request(1);
		}
	}

}

class ModelSupplier implements Supplier<Model> {

	private final AtomicInteger counter = new AtomicInteger(0);

	@Override
	public Model get() {
		if (counter.get() < 100) {
			int i = counter.incrementAndGet();
			return new Model("name" + i, i);
		}
		return null;
	}
}

class Model {
	private String name;
	private int number;

	public Model(String name, int number) {
		this.name = name;
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Model [" + (name != null ? "name=" + name : "") + "]";
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

}
