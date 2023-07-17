package ch.fhnw.cemcloudbackend.repository;

import ch.fhnw.cemcloudbackend.configuration.ApplicationProperties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WizardSetRepository {

    private final Path uploadDirPath;

    public WizardSetRepository(ApplicationProperties applicationProperties) {
        uploadDirPath = Paths.get(applicationProperties.uploadDirectory())
                .toAbsolutePath()
                .normalize()
                .resolve("sets");
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
        Path images = uploadDirPath.resolve("images");
        Files.createDirectories(images);

        try (Stream<Path> stream = Files.list(images)) {
            return stream
                    .filter(file -> !Files.isDirectory(file) && fileIsImage(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toSet());
        }
    }

    private boolean fileIsImage(Path file) {
        String normalizedName = file.toString().toLowerCase();
        return normalizedName.endsWith(".png") ||
               normalizedName.endsWith(".jpg") ||
               normalizedName.endsWith(".jpeg");
    }
}
