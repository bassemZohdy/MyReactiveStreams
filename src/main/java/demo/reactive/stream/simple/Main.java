package demo.reactive.stream.simple;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		SimpleProcessor.of(new ModelSupplier() // Supplier
				, System.out::println // onNext
				, System.out::println // onError
				, () -> System.out.println("Complete!")); // onComplete
	}

}

class ModelSupplier implements Supplier<Model> {

	private final AtomicInteger counter = new AtomicInteger(0);

	@Override
	public Model get() {
		if (counter.get() < Integer.MAX_VALUE) {
			int i = counter.incrementAndGet();
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return new Model("name" + i, i, i);
		}
		return null;
	}
}

class Model {
	private String name;
	private long size;
	private double value;

	public Model(String name, long size, double value) {
		super();
		this.name = name;
		this.size = size;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public long getSize() {
		return size;
	}

	public double getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "Model [name=" + name + ", size=" + size + ", value=" + value
				+ "]";
	}

}