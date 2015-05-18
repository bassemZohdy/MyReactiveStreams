package demo.reactive.stream;

public class Event<T> {

	private T t;

	private Event(T t) {
		this.t = t;
	}

	private Event() {
	}

	public static <T> Event<T> of(T obj) {
		return new Event<T>(obj);
	}

	public T get() {
		return t;
	}

	@Override
	public String toString() {
		return "Event [ value = " + t + "]";
	}

}
