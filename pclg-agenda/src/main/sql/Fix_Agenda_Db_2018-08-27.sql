ALTER TABLE Root.ContactoGrupo ADD CONSTRAINT CONTACTOGRUPO_GRUPO_FK FOREIGN KEY(ClaveGrupo) References Root.GRUPO(Clave);

-- El problema con estas es que no permiten crear el dato hasta que exista el contacto. :'(
-- ALTER TABLE Root.ContactoGrupo ADD CONSTRAINT CONTACTOGRUPO_CONTACTO_FK FOREIGN KEY(Clave, Version) References Root.CONTACTO(Clave, Version);
-- ALTER TABLE Root.DIRECCION ADD CONSTRAINT DIRECCION_CONTACTO_FK 	FOREIGN KEY(Clave, Version) References Root.CONTACTO(Clave, Version);
-- ALTER TABLE Root.EMAIL ADD CONSTRAINT EMAIL_CONTACTO_FK 		FOREIGN KEY(Clave, Version) References Root.CONTACTO(Clave, Version);
-- ALTER TABLE Root.IMAGEN ADD CONSTRAINT IMAGEN_CONTACTO_FK 		FOREIGN KEY(Clave, Version) References Root.CONTACTO(Clave, Version);
-- ALTER TABLE Root.THUMBNAIL ADD CONSTRAINT THUMBNAIL_CONTACTO_FK 	FOREIGN KEY(Clave, Version) References Root.CONTACTO(Clave, Version);
-- ALTER TABLE Root.NOTA ADD CONSTRAINT NOTA_CONTACTO_FK 			FOREIGN KEY(Clave, Version) References Root.CONTACTO(Clave, Version);

