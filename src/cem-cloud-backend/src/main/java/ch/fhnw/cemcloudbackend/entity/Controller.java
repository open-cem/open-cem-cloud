package ch.fhnw.cemcloudbackend.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CONTROLLER")
public class Controller extends Component {
}
