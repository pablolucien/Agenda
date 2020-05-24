-- Script de creacion de filedatabase

DROP DATABASE IF EXISTS FileDatabase;
COMMIT;
CREATE DATABASE FileDatabase;
USE FileDatabase;

-- Tabla Directorios
CREATE TABLE Directorios (
	Codigo  INT(11) NOT NULL,
	Camino  VARCHAR(255) NOT NULL,
	Padre	INT(11)
) TYPE=InnoDB;

ALTER TABLE Directorios ADD CONSTRAINT PK_Directorios PRIMARY KEY (Codigo);
ALTER TABLE Directorios MODIFY Codigo INT(11) AUTO_INCREMENT;
ALTER TABLE Directorios ADD CONSTRAINT UX_Directorios UNIQUE (Camino);
ALTER TABLE Directorios ADD CONSTRAINT FK_Directorios_Directorios FOREIGN KEY (Padre) REFERENCES Directorios(Codigo);


-- Tabla Extensiones
CREATE TABLE Extensiones (
	Codigo     INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
	Extension  VARCHAR(128) NOT NULL
) TYPE=InnoDB;
ALTER TABLE Extensiones ADD CONSTRAINT UX_Extensiones UNIQUE (Extension);


-- Tabla Archivos
CREATE TABLE Archivos (
	Camino             INT(11) NOT NULL,
	Nombre             VARCHAR(255) NOT NULL,
	Tamanho            INT(11) NOT NULL,
	FechaModificacion  DATETIME  NOT NULL,
	Extension          INT(11) NOT NULL,
	Digest             VARCHAR(40) NOT NULL,
	FechaIngreso       DATETIME NOT NULL
) TYPE=InnoDB;

ALTER TABLE Archivos ADD CONSTRAINT PK_Archivos PRIMARY KEY (Camino, Nombre, Tamanho, FechaModificacion);
ALTER TABLE Archivos ADD CONSTRAINT FK_Archivos_Directorios FOREIGN KEY (Camino) REFERENCES Directorios(Codigo);
ALTER TABLE Archivos ADD CONSTRAINT FK_Archivos_Extensiones FOREIGN KEY (Extension) REFERENCES Extensiones(Codigo);


-- Tabla Series
CREATE TABLE Series (
	Directorio   int(11) NOT NULL,
	NombreSerie  varchar(255) NOT NULL,
	Thumbnail    varchar(20)
) TYPE=InnoDB;


SHOW TABLES;
DESC Archivos;
DESC Directorios;
DESC Extensiones;
DESC Series;

-- Creacion del usuario de esta base de datos.
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP
    ON FileDatabase.*
    TO 'CompDel'@'localhost'
    IDENTIFIED BY 'obscure';
