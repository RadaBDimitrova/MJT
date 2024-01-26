package bg.sofia.uni.fmi.mjt.photoalbum;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicLong;

public class ParallelMonochromeAlbumCreator implements MonochromeAlbumCreator {
    private final int imageProcessorsCount;
    private final BlockingQueue<Runnable> taskQueue;

    public ParallelMonochromeAlbumCreator(int imageProcessorsCount) {
        this.imageProcessorsCount = imageProcessorsCount;
        this.taskQueue = new BlockingQueue<>();
    }

    @Override
    public void processImages(String sourceDirectory, String outputDirectory) {
        createOutputDirectory(outputDirectory);
        try {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(sourceDirectory))) {
                for (Path file : stream) {
                    if (isImageFile(file)) {
                        taskQueue.put(new ImageProcessor(file, outputDirectory));
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new UncheckedIOException("Error processing images",
                    e instanceof IOException ? (IOException) e : new IOException(e));
        }

        Thread producerThread = new Thread(() -> {
            try {
                traverseDirectory(Paths.get(sourceDirectory), outputDirectory);
            } catch (IOException | InterruptedException e) {
                throw new UncheckedIOException("Error processing images",
                        e instanceof IOException ? (IOException) e : new IOException(e));
            }
        });

        producerThread.start();
        AtomicLong count = initializeCount(sourceDirectory);
        startConsumerThreads(count);
        awaitTermination(producerThread);
    }

    private void awaitTermination(Thread producerThread) {
        try {
            producerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private AtomicLong initializeCount(String sourceDirectory) {
        try {
            return new AtomicLong(countFilesInDirectory(Paths.get(sourceDirectory)));
        } catch (IOException e) {
            throw new UncheckedIOException("Empty or nonexistent directory", e);
        }
    }

    private void startConsumerThreads(AtomicLong countOfFiles) {
        int imageProcessors = Math.min(imageProcessorsCount, countOfFiles.intValue());
        for (int i = 0; i < imageProcessorsCount; i++) {
            new Thread(new ImageConsumer(taskQueue, countOfFiles)).start();
        }
    }

    private void traverseDirectory(Path directory, String outputDirectory) throws IOException, InterruptedException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path file : stream) {
                if (Files.isDirectory(file)) {
                    traverseDirectory(file, outputDirectory);
                } else if (isImageFile(file)) {
                    taskQueue.put(new ImageProcessor(file, outputDirectory));
                }
            }
        }
    }

    private long countFilesInDirectory(Path directory) throws IOException {
        try (var stream = Files.list(directory)) {
            return stream.filter(ParallelMonochromeAlbumCreator::isImageFile).count();
        }
    }

    private static boolean isImageFile(Path file) {
        String fileName = file.getFileName().toString().toLowerCase();
        return fileName.endsWith(".jpeg") || fileName.endsWith(".jpg") || fileName.endsWith(".png");
    }

    private void createOutputDirectory(String outputDirectory) {
        try {
            Files.createDirectories(Paths.get(outputDirectory));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
