package ch.fhnw.cemcloudbackend.repository;

import ch.fhnw.cemcloudbackend.configuration.ApplicationProperties;
import ch.fhnw.cemcloudbackend.entity.Installation;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WizardSetRepository {

    private static final String IMAGES_FOLDER_NAME = "images";
    private static final String SETS_FOLDER_NAME = "sets";
    private final Path uploadDirPath;

    public WizardSetRepository(ApplicationProperties applicationProperties) {
        uploadDirPath = Paths.get(applicationProperties.uploadDirectory())
                .toAbsolutePath()
                .normalize()
                .resolve(SETS_FOLDER_NAME);
    }

    public void delete(String filename) throws IOException {
        if (filename == null) {
            throw new IllegalArgumentException("filename can not be null");
        }

        Path filepath = uploadDirPath.resolve(filename);
        Files.deleteIfExists(filepath);
    }

    public void deleteImage(String filename) throws IOException {
        if (filename == null) {
            throw new IllegalArgumentException("filename can not be null");
        }

        Path images = uploadDirPath.resolve(IMAGES_FOLDER_NAME);

        Path filepath = images.resolve(filename);
        Files.deleteIfExists(filepath);
    }

    public Optional<String> find(String setFilename) throws IOException {
        if (setFilename == null) {
            throw new IllegalArgumentException("setFilename can not be null");
        }

        Files.createDirectories(uploadDirPath);

        return find(setFilename, uploadDirPath);
    }

    public Iterable<String> findAll() throws IOException {
        Files.createDirectories(uploadDirPath);

        try (Stream<Path> stream = Files.list(uploadDirPath)) {
            return stream
                    .filter(file -> !Files.isDirectory(file) && file.toString().toLowerCase().endsWith(".yaml"))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toSet());
        }
    }

    public Iterable<String> findAllImages() throws IOException {
        Path images = uploadDirPath.resolve(IMAGES_FOLDER_NAME);
        Files.createDirectories(images);

        try (Stream<Path> stream = Files.list(images)) {
            return stream
                    .filter(file -> !Files.isDirectory(file) && fileIsImage(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toSet());
        }
    }

    public Optional<String> findImage(String imageFilename) throws IOException {
        if (imageFilename == null) {
            throw new IllegalArgumentException("imageFilename can not be null");
        }

        Path images = uploadDirPath.resolve(IMAGES_FOLDER_NAME);
        Files.createDirectories(images);

        return find(imageFilename, images);
    }

    public void save(String setFilename, InputStream fileStream) throws IOException {
        if (setFilename == null) {
            throw new IllegalArgumentException("setFilename can not be null");
        }

        Files.createDirectories(uploadDirPath);
        Files.copy(fileStream, uploadDirPath.resolve(setFilename));
    }

    public void saveImage(String imageFilename, InputStream fileStream) throws IOException {
        if (imageFilename == null) {
            throw new IllegalArgumentException("imageFilename can not be null");
        }

        Path images = uploadDirPath.resolve(IMAGES_FOLDER_NAME);
        Files.createDirectories(images);
        Files.copy(fileStream, images.resolve(imageFilename));
    }

    private boolean fileIsImage(Path file) {
        String normalizedName = file.toString().toLowerCase();
        return normalizedName.endsWith(".png") ||
               normalizedName.endsWith(".jpg") ||
               normalizedName.endsWith(".jpeg");
    }

    private Optional<String> find(String filename, Path dirPath) throws IOException {
        try (Stream<Path> stream = Files.list(dirPath)) {
            return stream
                    .filter(file -> !Files.isDirectory(file) && file.endsWith(filename))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .findFirst();
        }
    }

    public Optional<byte[]> loadFile(String filename, boolean isImage) throws IOException {
        if (filename == null) {
            throw new IllegalArgumentException("filename can not be null");
        }
        Path dir = uploadDirPath;
        if (isImage) {
            dir = dir.resolve(IMAGES_FOLDER_NAME);
        }

        Path filePath = dir.resolve(filename);
        if (Files.exists(filePath)) {
            return Optional.of(Files.readAllBytes(filePath));
        }

        return Optional.empty();
    }
}
