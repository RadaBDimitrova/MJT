package bg.sofia.uni.fmi.mjt.photoalbum;

import java.util.LinkedList;
import java.util.Queue;

public class BlockingQueue<T> {

    private final Queue<T> queue;

    public BlockingQueue() {
        this.queue = new LinkedList<>();
    }

    public synchronized void put(T item) throws InterruptedException {
        queue.offer(item);
        notifyAll();
    }

    public synchronized T take() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        return queue.poll();
    }
}