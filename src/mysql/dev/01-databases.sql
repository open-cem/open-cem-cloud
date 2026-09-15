# create databases
CREATE DATABASE IF NOT EXISTS `cemcloud`;
CREATE DATABASE IF NOT EXISTS `keycloak`;

CREATE USER IF NOT EXISTS 'cemcloud'@'%' IDENTIFIED BY 'rx9gfJWU$5aLe-tq';
GRANT ALL PRIVILEGES ON cemcloud.* TO 'cemcloud'@'%';

CREATE USER IF NOT EXISTS 'keycloak'@'%' IDENTIFIED BY 'LfjtZ2h-kdP68@5M';
GRANT ALL PRIVILEGES ON keycloak.* TO 'keycloak'@'%';
