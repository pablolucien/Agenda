#-- Script de creacion de filedatabase
#   Para ser usado con DbUpdate
#
<NO_BACKUP>

# Base de datos a usar
C:/pclucien/FileDataBase.mdb

<DROP_TABLES>

-- Tabla Directorios
--DROP TABLE Directorios
CREATE TABLE Directorios (|
	Codigo  COUNTER CONSTRAINT PK_Directorios PRIMARY KEY,|
	Camino  VARCHAR(255) NOT NULL,|
	Padre	  INTEGER|
)

ALTER TABLE Directorios ADD CONSTRAINT UX_Directorios UNIQUE (Camino);
ALTER TABLE Directorios ADD CONSTRAINT FK_Directorios_Directorios FOREIGN KEY (Padre) REFERENCES Directorios(Codigo);


-- Tabla Extensiones
--DROP TABLE Extensiones
CREATE TABLE Extensiones (|
	Codigo     COUNTER CONSTRAINT PK_Extensiones PRIMARY KEY,|
	Extension  VARCHAR(128) NOT NULL|
)

ALTER TABLE Extensiones ADD CONSTRAINT UX_Extensiones UNIQUE (Extension);


-- Tabla Archivos
--DROP TABLE Archivos
CREATE TABLE Archivos (|
	Camino             INTEGER NOT NULL,|
	Nombre             VARCHAR(255) NOT NULL,|
	Tamanho            INTEGER NOT NULL,|
	FechaModificacion  DATETIME  NOT NULL,|
	Extension          INTEGER NOT NULL,|
	Digest             VARCHAR(40) NOT NULL,|
	FechaIngreso       DATETIME NOT NULL|
)

ALTER TABLE Archivos ADD CONSTRAINT PK_Archivos PRIMARY KEY (Camino, Nombre, Tamanho, FechaModificacion);
ALTER TABLE Archivos ADD CONSTRAINT FK_Archivos_Directorios FOREIGN KEY (Camino) REFERENCES Directorios(Codigo);
ALTER TABLE Archivos ADD CONSTRAINT FK_Archivos_Extensiones FOREIGN KEY (Extension) REFERENCES Extensiones(Codigo);


-- Tabla Series
--DROP TABLE Series
CREATE TABLE Series (|
	Directorio   INTEGER NOT NULL,|
	NombreSerie  varchar(255) NOT NULL,|
	Thumbnail    varchar(20)|
)


#SHOW TABLES;
#DESC Archivos;
#DESC Directorios;
#DESC Extensiones;
#DESC Series;

-- Creacion del usuario de esta base de datos.
#GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP
#    ON FileDatabase.*
#    TO 'CompDel'@'localhost'
#    IDENTIFIED BY 'obscure';


/*
Ojo con esto: la podemos cagar

CREATE USER CompDel obscure
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP TO CompDel
*/

/*
GRANT (Instrucción)
Otorga privilegios específicos a un grupo o a un usuario.

Sintaxis
GRANT {privilegio[, privilegio, …]} ON
    {TABLE tabla |    
    OBJECT objeto|

CONTAINER contenedor } TO {nombreAutorización[, nombreAutorización, …]}


--------------------------------------------------------------------------------

La instrucción GRANT consta de estos apartados:

Apartado Descripción 
privilegio El privilegio o los privilegios que se van a otorgar. Los privilegios se especifican mediante las siguientes palabras clave: 
SELECT, DELETE, INSERT, UPDATE, DROP, SELECTSECURITY, UPDATESECURITY, DBPASSWORD, UPDATEIDENTITY, CREATE, SELECTSCHEMA, SCHEMA y UPDATEOWNER.
 
nombreTabla Cualquier nombre válido de tabla. 
objeto Puede incluir cualquier objeto que no sea una tabla. Por ejemplo, una consulta almacenada (vista o procedimiento). 
contenedor El nombre de un contenedor válido. 
nombreAutorización Un nombre de grupo o de usuario. 
*/