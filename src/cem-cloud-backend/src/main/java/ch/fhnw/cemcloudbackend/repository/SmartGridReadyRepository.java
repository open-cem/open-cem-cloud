package ch.fhnw.cemcloudbackend.repository;

import ch.fhnw.cemcloudbackend.configuration.ApplicationProperties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

public class SmartGridReadyRepository {
    private final Path uploadDirPath;

    public SmartGridReadyRepository(ApplicationProperties applicationProperties) {
        uploadDirPath = Paths.get(applicationProperties.uploadDirectory()).toAbsolutePath().normalize().resolve("smartgridready");
    }

    public Optional<byte[]> load(UUID id) throws IOException {
        if (id == null) {
            throw new IllegalArgumentException("id can not be null");
        }

        String todoFilename = "SGr_HeatPump_Test.xml";
        Path xmlFile = uploadDirPath.resolve(todoFilename);
        if (Files.exists(xmlFile)) {
            return Optional.of(Files.readAllBytes(xmlFile));
        }

        return Optional.empty();
    }
}
