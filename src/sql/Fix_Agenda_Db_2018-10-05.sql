ALTER TABLE root.imagen DROP COLUMN secuencia;
ALTER TABLE root.imagen ADD COLUMN thumbnail BLOB;
RENAME COLUMN root.imagen.Imagen TO imagePath;
UPDATE root.imagen I SET thumbnail = (SELECT Thumbnail FROM root.Thumbnail T WHERE T.clave = I.clave AND T.version = I.version);
ALTER TABLE root.imagen ALTER COLUMN thumbnail NOT NULL;
DROP TABLE root.Thumbnail;
UPDATE root.control SET FechaActualizacion = CURRENT_TIMESTAMP;
