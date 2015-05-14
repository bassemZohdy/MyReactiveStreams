package demo.reactive.stream;

public class Event<T> {

	private boolean killBill = false;
	private T t;

	private Event(T t) {
		this.t = t;
	}

	private Event(boolean kill) {
		this.killBill = kill;
	}

	public static <T> Event<T> of(T obj) {
		return new Event<T>(obj);
	}

	public static <T> Event<T> kill() {
		return new Event<T>(true);
	}

	public boolean isKillBill() {
		return killBill;
	}

	public T get() {
		return t;
	}

	@Override
	public String toString() {
		return "Event [ value = " + t + "]";
	}

}
