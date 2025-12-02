package ch.fhnw.cemcloudbackend.repository;

import ch.fhnw.cemcloudbackend.configuration.ApplicationProperties;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public class SmartGridReadyXmlRepository {

    private final Path uploadDirPath;

    public SmartGridReadyXmlRepository(ApplicationProperties applicationProperties) {
        this.uploadDirPath = Paths.get(applicationProperties.getUploadDirectory())
                .toAbsolutePath()
                .normalize()
                .resolve("smartgridready");
    }

    public void delete(String filename) throws IOException {
        if (filename == null) {
            throw new IllegalArgumentException("filename can not be null");
        }

        Path xmlFile = uploadDirPath.resolve(filename);

        Files.deleteIfExists(xmlFile);
    }

    public Optional<byte[]> load(String filename) throws IOException {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("filename can not be null or empty.");
        }

        Path xmlFile = uploadDirPath.resolve(filename);
        if (Files.exists(xmlFile)) {
            return Optional.of(Files.readAllBytes(xmlFile));
        }

        return Optional.empty();
    }

    public void save(String filename, InputStream fileStream) throws IOException {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("filename can not be null or empty.");
        }

        Files.createDirectories(uploadDirPath);
        Files.copy(fileStream, uploadDirPath.resolve(filename));
    }
}
