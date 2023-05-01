# CEM-Cloud

## Development
Per Docker Compose kann eine Entwicklungsumgebung bestehend aus einem Keycloak Authentifizierungsserver, einem MySQL Datenbankserver und einem HiveMQ MQTT Broker gestartet werden. Das CEM-Cloud Backend kann als [Spring Applikation](src/cem-cloud-backend/src/main/java/ch/fhnw/cemcloudbackend/CemCloudBackendApplication.java) lokal gestartet werden. Das CEM-Cloud Backend kann als [React Applikation](src/cem-cloud-frontend/package.json) lokal gestartet werden.

Mit dem folgenden Befehl kann die Docker Compose Entwicklungsumgebung gestartet werden:
```shell
docker-compose -f .\docker-compose-dev.yml up
```

| Application | URL |
| ----------- | ----------- |
| Frontend | http://localhost:3000/ |
| Backend | http://localhost:8080/api |
| Keycloak | http://localhost:2000/ |
| HiveMQ | http://localhost:1883/ |
| MySQL | localhost:3306 |


### Keycloak
Der Keycloak Server ist im Browser erreichbar und kann mit dem User `admin` und Passwort `admin` konfiguriert werden. Damit das React Frontend verwendet werden kann müssen im Keycloak folgende Einstellungen gemacht werden:

1. Realm mit dem Namen `cem-cloud` erstellen.
2. Den `react-app` Client erstellen:
    - General:
        - client-ID: `react-app`
    - Capability config:
        - Client authentication: off
		- Authorization: off
		- Standard flow: checked
		- Direct access grants: checked
    - Login settings:
        - Root URL: http://localhost:3000/
		- Valid redirect URIs: http://localhost:3000
		- Web origins: http://localhost:3000
3. Einen User erstellen:
    - Username: max
    	- email: max.muster@students.fhnw.ch
	    - Email verified: Yes
	    - First name: Max
	    - Last name: Muster
    - User Details:
	    - Credentials:
		    - Password ***
		    - Temporary: Off