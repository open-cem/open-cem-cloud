package ch.fhnw.cemcloudbackend.controller;

import ch.fhnw.cemcloudbackend.repository.InstallationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationControllerTest {

    @Mock
    private InstallationRepository installations;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getConfiguration() throws IOException {
        // Load file from ressources folder "example.yaml"
        String expected = Files.readString(Paths.get("src/test/resources/example.yaml"));

        // Call getConfiguration() method
        ConfigurationController controller = new ConfigurationController(installations);
        String actual = controller.getConfiguration("123");

        // Compare expected and actual
        assertEquals(ignoreDynamicFields(expected), ignoreDynamicFields(actual));
    }

    String ignoreDynamicFields(String s) {
        return ignoreUUID(ignoreCreationTimestamp(s));
    }

    String ignoreUUID(String s) {
        return s.replaceAll("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}", "00000000-0000-0000-0000-000000000000");
    }

    String ignoreCreationTimestamp(String s) {
        return s.replaceAll("creationTimestamp: .*", "creationTimestamp: 0");
    }
}