package bg.sofia.uni.fmi.mjt.photoalbum;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

public class ImageProcessor implements Runnable {
    private final Path imagePath;
    private final String outputDirectory;

    public ImageProcessor(Path imagePath, String outputDirectory) {
        this.imagePath = imagePath;
        this.outputDirectory = outputDirectory;
    }

    public Image loadImage(Path imagePath) {
        try {
            BufferedImage imageData = ImageIO.read(imagePath.toFile());
            return new Image(imagePath.getFileName().toString(), imageData);
        } catch (IOException e) {
            throw new UncheckedIOException(String.format("Failed to load image %s", imagePath), e);
        }
    }

    private Image convertToBlackAndWhite(Image image) {
        BufferedImage processedData = new BufferedImage(image.data.getWidth(),
                image.data.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        processedData.getGraphics().drawImage(image.data, 0, 0, null);

        return new Image(image.name, processedData);
    }

    private void saveImage(Image image, String outputDirectory) {
        try {
            String outputFileName = image.name.substring(0, image.name.lastIndexOf('.')) + "BWCOPY.png";
            File outputFile = new File(outputDirectory, outputFileName);

            ImageIO.write(image.data, "png", outputFile);
        } catch (IOException e) {
            throw new UncheckedIOException("Did not save image", e);
        }
    }

    @Override
    public void run() {
        Image result = convertToBlackAndWhite(loadImage(imagePath));
        saveImage(result, outputDirectory);

    }
}

