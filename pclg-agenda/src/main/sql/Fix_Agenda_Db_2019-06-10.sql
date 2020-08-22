-- clean old temporary data
-- ALTER TABLE root.Telefono DROP numero_old;
--
-- Clean invalid data
--DELETE FROM root.telefono WHERE clave = 118 AND version = 3;
--DELETE FROM root.telefono WHERE clave = 119 AND version IN (2, 3, 4);
--UPDATE root.contacto SET versionTelefono = 2 WHERE clave = 119 AND versionTelefono = 5;
--UPDATE root.contacto SET versionTelefono = 3 WHERE clave = 119 AND versionTelefono = 6;
--UPDATE root.telefono SET version = 2 WHERE clave = 119 AND version = 5;
--UPDATE root.telefono SET version = 3 WHERE clave = 119 AND version = 6;
--
-- add new field
ALTER TABLE root.Telefono ADD countryPrefix VARCHAR(3);
UPDATE root.Telefono T SET countryPrefix = (SELECT pais FROM root.contacto C WHERE C.clave = T.clave AND C.versionTelefono = T.version AND C.version = (SELECT MAX(version) FROM root.contacto C1 WHERE C1.clave = T.clave AND C1.versionTelefono = T.version));
ALTER TABLE root.Telefono ADD CONSTRAINT Telefono_Pais_FK FOREIGN KEY(countryPrefix) REFERENCES root.Pais(codigo);
ALTER TABLE root.Telefono ALTER COLUMN countryPrefix NOT NULL;
