-- Script de creacion de DateTimeAdjusterDB

DROP DATABASE IF EXISTS DateTimeAdjusterDB;
COMMIT;
CREATE DATABASE DateTimeAdjusterDB;
USE DateTimeAdjusterDB;

-- Tabla Directorios
CREATE TABLE Directorios (
	Codigo  INT(11) NOT NULL,
	Camino  VARCHAR(255) NOT NULL
) TYPE=InnoDB;

ALTER TABLE Directorios ADD CONSTRAINT PK_Directorios PRIMARY KEY (Codigo);
ALTER TABLE Directorios MODIFY Codigo INT(11) AUTO_INCREMENT;
ALTER TABLE Directorios ADD CONSTRAINT UX_Directorios UNIQUE (Camino);

-- Tabla Archivos
CREATE TABLE Archivos (
	Camino             INT(11) NOT NULL,
	Nombre             VARCHAR(255) NOT NULL,
	FechaModificacion  DATETIME  NOT NULL
) TYPE=InnoDB;

ALTER TABLE Archivos ADD CONSTRAINT PK_Archivos PRIMARY KEY (Camino, Nombre);
ALTER TABLE Archivos ADD CONSTRAINT FK_Archivos_Directorios FOREIGN KEY (Camino) REFERENCES Directorios(Codigo);

SHOW TABLES;
DESC Archivos;
DESC Directorios;

-- Creacion del usuario de esta base de datos.
CREATE USER DateTimeAdjuster IDENTIFIED BY 'obscure';

GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP
    ON datetimeadjusterdb.*
    TO 'DateTimeAdjuster'@'localhost'
    IDENTIFIED BY 'obscure';
