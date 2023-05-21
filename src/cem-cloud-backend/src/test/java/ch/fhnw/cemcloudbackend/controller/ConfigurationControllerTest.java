package ch.fhnw.cemcloudbackend.controller;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationControllerTest {

    @Test
    void getConfiguration() throws IOException {
        // Load file from ressources folder "example.yaml"
        String expected = Files.readString(Paths.get("src/test/resources/example.yaml"));

        // Call getConfiguration() method
        ConfigurationController controller = new ConfigurationController();
        String actual = controller.getConfiguration();

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