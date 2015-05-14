package demo.reactive.stream;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		EventPublisher<String> myPublisher = new EventPublisher<String>();
		EventSubscriber<String> s1 = new EventSubscriber<String>();
		EventSubscriber<String> s2 = new EventSubscriber<String>();
		myPublisher.subscribe(s1);
		myPublisher.subscribe(s2);
		myPublisher.send("Hi 1 !");
		myPublisher.send("Hi 2 !");
		myPublisher.send("Hi 3 !");
		myPublisher.send("Hi 4 !");
		myPublisher.send("Hi 5 !");
		myPublisher.complete();
		myPublisher.send("Hi after complete !");
		Thread.sleep(5000);

	}
}
