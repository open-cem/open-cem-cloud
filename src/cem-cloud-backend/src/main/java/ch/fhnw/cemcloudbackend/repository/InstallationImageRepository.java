package ch.fhnw.cemcloudbackend.repository;

import ch.fhnw.cemcloudbackend.configuration.ApplicationProperties;
import ch.fhnw.cemcloudbackend.entity.Installation;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class InstallationImageRepository {
    private final Path uploadDirPath;

    public InstallationImageRepository(ApplicationProperties applicationProperties) {
        uploadDirPath = Paths.get(applicationProperties.uploadDirectory()).toAbsolutePath().normalize();
    }

    public void save(Installation installation, String filename, InputStream imageStream) throws IOException {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("filename can not be null or empty.");
        }

        Path installationUploadDir = uploadDirPath.resolve(installation.getId().toString());
        if (installation.getFilename() != null && !installation.getFilename().isBlank()) {
            Files.deleteIfExists(installationUploadDir.resolve(installation.getFilename()));
        }

        Files.createDirectories(installationUploadDir);
        Files.copy(imageStream, installationUploadDir.resolve(filename));

        installation.setFilename(filename);
    }

    public Optional<byte[]> load(Installation installation) throws IOException {
        if (installation == null) {
            throw new IllegalArgumentException("installation can not be null");
        }

        Path installationUploadDir = uploadDirPath.resolve(installation.getId().toString());
        if (installation.getFilename() != null && !installation.getFilename().isBlank()) {
            Path imageFilePath = installationUploadDir.resolve(installation.getFilename());
            if (Files.exists(imageFilePath)) {
                return Optional.of(Files.readAllBytes(imageFilePath));
            }
        }

        return Optional.empty();
    }
}
