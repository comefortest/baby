package util;

public class JavaUtil {
	public static interface Worker<T> {
		public T work();
	}

	public static <T> T sync(Object lock, Worker<T> worker) {
		synchronized (lock) {
			return worker.work();
		}
	}
	
}
