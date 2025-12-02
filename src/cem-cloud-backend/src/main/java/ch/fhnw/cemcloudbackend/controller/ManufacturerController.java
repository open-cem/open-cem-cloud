package ch.fhnw.cemcloudbackend.controller;

import ch.fhnw.cemcloudbackend.dto.Manufacturer;
import ch.fhnw.cemcloudbackend.dto.Model;
import ch.fhnw.cemcloudbackend.repository.ManufacturerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

@RestController()
@RequestMapping("manufacturers")
public class ManufacturerController {

    private final ManufacturerRepository manufacturers;

    public ManufacturerController(ManufacturerRepository manufacturers) {
        this.manufacturers = manufacturers;
    }

    @GetMapping()
    public ResponseEntity<Iterable<Manufacturer>> getAll() {
        Iterable<ch.fhnw.cemcloudbackend.entity.Manufacturer> all = manufacturers.findAll();

        return ResponseEntity.ok(StreamSupport.stream(all.spliterator(), false)
                .map(manufacturer -> new Manufacturer(manufacturer.getId(), manufacturer.getName()))
                .toList());
    }

    @GetMapping("{id}/models")
    public ResponseEntity<Iterable<Model>> getModels(@PathVariable UUID id) {
        Optional<ch.fhnw.cemcloudbackend.entity.Manufacturer> manufacturer = manufacturers.findById(id);

        if (manufacturer.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(manufacturer.get().getModels().stream()
                .map(model -> new Model(model.getId(), model.getName()))
                .toList());
    }
}
