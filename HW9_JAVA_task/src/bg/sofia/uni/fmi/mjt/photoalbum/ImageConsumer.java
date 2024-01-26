package bg.sofia.uni.fmi.mjt.photoalbum;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

public class ImageConsumer implements Runnable {
    private final BlockingQueue<Runnable> taskQueue;
    private final AtomicLong countOfFiles;

    public ImageConsumer(BlockingQueue<Runnable> taskQueue, AtomicLong countOfFiles) {
        this.taskQueue = taskQueue;
        this.countOfFiles = countOfFiles;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Runnable task = taskQueue.take();
                countOfFiles.decrementAndGet();
                task.run();
                if (countOfFiles.get() == BigDecimal.ZERO.intValue()) {
                    return;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
