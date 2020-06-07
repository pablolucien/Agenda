Utility	  Program	 !conjunto de utilities que pueden servir en algun
			 ! momento, mutatis mutandi
			 ! 23-03-94
	  map
	     proc(BatchUpd)	! Update de co¤azo un viaje de registros
	  .



BatchUpd  Procedure

  ! Archivo a updatear
Articulo     File,Pre(ART),Create,Reclaim, name('c:\clarion\datos\articulo')
  !CLAVE       Key(ART:COD_REVISTA),Dup,Nocase
  !Por_Temas   Key(ART:Cod_Tema_1,ART:Cod_Tema_2,ART:Cod_Tema_3),Dup,Nocase,Opt
RECORD	       Record
Cod_Revista	 Byte				 !Codigo del la revista
Titulo		 String(60)
Cod_Tema_1	 Byte
Cod_Tema_2	 Byte
Cod_Tema_3	 Byte
Seccion		 String(20)
Mes		 Byte
Ano		 Byte
Numero		 Short				 !N£mero de la revista
Pagina		 String(4)
	     . .

registro  byte

  code
     SHOW(25,1,CENTER('Abriendo: ' & 'Articulo',80)) !DISPLAY FILE NAME
     OPEN(Articulo)				    !OPEN THE FILE
     IF ERROR()					    !OPEN RETURNED AN ERROR
       CASE ERRORCODE()				    ! CHECK FOR SPECIFIC ERROR
       OF 46					    !  KEYS NEED TO BE REQUILT
	 SHOW(25,1,CENTER('Reconstruyendo Articulo',80)) !INDICATE MSG
	 BUILD(Articulo)			    !  CALL THE BUILD PROCEDURE
	 BLANK(25,1,1,80)			    !  BLANK THE MESSAGE
       ELSE					    ! ANY OTHER ERROR
	 LOOP;STOP('Articulo: ' & ERROR()).	    !  STOP EXECUTION
     . .

  set(articulo)					 ! Go top
  LOOP UNTIL EOF(articulo)			 !  READ UNTIL END OF FILE
    NEXT(articulo)				 !  READ NEXT RECORD
    if art:cod_tema_1 = 0 then
       art:cod_tema_1 = art:cod_tema_2
       art:cod_tema_2 = art:cod_tema_3
       art:cod_tema_3 = 0
       put(articulo)
       registro += 1
    .
    show(25, 1, 'Registro ' & registro & ' de ' & records(articulo))
  .
  ask
