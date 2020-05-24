SISTEMA	     Program
 omit('fin')
    Modificaciones:
      crea fondo tipo NetWare
      archivo de control y rutina para modificarlo (F5)
      Hora
      Obtiene nombre del programa en ejecucion
      Confirma impresion
      Pide password
      Funciones sin Hacer
      Modificacion en form para no verificar modificaciones
      Nombres de los meses y dias
      Marca la opcion de menu escogida
      Reconstruccion de indices
      Salida por ALT-F10 y ESC desde el primer campo
      Calendario (F4)
      15-6-92 Procedimiento Rebuild (reindexar archivos)
      01-07-92 Modulo NETWARE.UTL
      08-07-92 InitProc y LastProc (Inicializacion y fin)
      17-12-93 MsgForm: despliega un mensaje y retorna
      07-10-94 Capitalize: Capitaliza un nombre
      21-12-94 G_CloseFiles: Cierra todos los archivos
      02-02-95 Modificadas LOOKUPS y LOOKUPSCROLL para poner '*' si not found
 fin

	     INCLUDE('c:\pablo\clarion\modelos\ALL_KEYS.CLA')
REJECT_KEY   EQUATE(CTRL_ESC)
ACCEPT_KEY   EQUATE(CTRL_ENTER)
TRUE	     EQUATE(1)
FALSE	     EQUATE(0)

	     MAP
	       PROC(G_OPENFILES)
	       PROC(G_CloseFILES)
	       Proc(InitProc)			 ! Inicializar
	       Proc(LastProc)			 ! Finalizar
	       Proc(Get_Pass)			 ! Obtener PassWord
	       Proc(Hora)			 ! Poner Fecha y Hora
	       Proc(ConfSalida)			 ! Confirmar la salida del sistema
	       Proc(Salida)			 ! Salida del sistema
	       Proc(xxcontr)			 ! Modificar archivo de control
	       Proc(Calendario)
	       Proc(Sin_Hacer)			 ! Procedimientos que no estan hechos
	       Proc(Capitalize)			 ! Capitaliza un nombre
	       Module('Sistem00')
		 Proc(Initius)			 !Inicializaci¢n y llama a menu
		 Proc(L_Telefonos)		 !Listado de Tel‚fonos
		 Proc(MoreControl)		 !Modificar archivo de control
		 Proc(GetRndRec)		 !Obtiene un registro al azar
		 Proc(CalcNotas)
		 Proc(L_Telefono2)		 !Listado para la laserjet
	       .
	       MODULE('getname'),BINARY		 ! Obtener el nombre del
		 FUNC(getname),STRING		 !    programa en ejecuci¢n
	       .
	       Module('SISTEMA4')
		 Proc(UPD_AGENDA)		 !Actualizacion de la agenda
		 Proc(SHO_F_DEBIDA)		 !Show Agenda By F_Debida
		 Proc(MENU_Inicial)		 !Men£ General
		 Proc(Rebuild)			 !Reconstruccion de Archivos
		 Proc(Shell)
		 Proc(Memoria)			 !Variables de Memoria
		 Proc(Tbl_Paises)		 !Pa¡ses del mundo mundial
		 Proc(Upd_paises)		 !Actualizaci¢n de paises
	       .
      area
      overlay
	       Module('SISTEMA1')
		 Proc(MENU_cumple)
		 Proc(P_NOMBRES)		 !Lista por nombres
		 Proc(P_FECHAS)			 !Lista por fechas
		 Proc(UPD_TABLE)		 !Actualizaci¢n de datos
		 Proc(ESPECIAL)			 !Lista por Marcas
		 Proc(UPD_TABLE_SP)		 !Modificaci¢n de la tabla
		 Proc(Menu_Telef)
		 Proc(Por_telefono)
		 Proc(Por_pais)			 !Personas por pa¡ses
	       .
      .
      overlay
	       Module('SISTEMA2')
		 Proc(MENU_INVENT)		 !Sistema de inventario
		 Proc(UPD_PIEZAS)		 !Update Piezas
		 Proc(SHO_CLAVE)		 !Show Piezas By Clave
		 Proc(RPT_CLAVE)		 !Print Piezas By Clave
		 Proc(SHO_TIPO)			 !Show Piezas por Tipo
		 Proc(RPT_TIPO)			 !Print Piezas By Codigo
		 Proc(TAB_TIPOS)		 !Tabla de Tipos de Componentes
		 Proc(UPD_TIPOS)		 !Actualizaci¢n de Tipos
	       .
      .
      overlay
	       Module('SISTEMA3')
		 Proc(Articulos)
		 Proc(Tabla_Rev)		 !Tabla de Revistas
		 Proc(UPD_TABLE_R)
		 Proc(UPD_Revista)
		 Proc(Tabla_Art)
		 Proc(L_REVISTA)
		 Proc(Tab_Temas)		 !Tabla de Temas
		 Proc(Upd_Temas)		 !Actualizar Temas
		 Proc(POR_TEMAS)
		 Proc(Por_Revista)		 !Listado de Articulos por Revis
		 Proc(Por_Titulo)		 !Listado de Articulos por Titul
	       .
      .
      overlay
	       Module('SISTEMA5')
		 Proc(Tbl_Fortunes)		 !Fortunes
		 Proc(Upd_Fortunes)		 !Fortunes
		 Proc(Tbl_Notas)		 !Notas
		 Proc(Upd_Notas)		 !Actualizaci¢n
		 Proc(upd_compacts)		 !Update Compacts
		 Proc(Mnu_Compacts)		 !Compacts
		 Proc(TBL_AUTOR)		 !Compacts por Autor
		 Proc(Rpt_Autor)		 !Reporte por autor
		 Proc(Tbl_Prestado)		 !Discos Prestados
	       .
		.
		.
	     .
	     EJECT('FILE LAYOUTS')
Fechas	     File,Pre(Dat),Create
	       Owner('Sauron'),Encrypt
POR_NOMBRE     Key(Dat:NOMBRE),Dup,Nocase
POR_FECHA      Key(Dat:MES,Dat:DIA),Dup,Nocase
POR_MARCA      Key(Dat:MARCA),Dup,Nocase,Opt
Por_telef      Key(Dat:Telefono_1),Dup,Nocase
Por_Pais       Key(Dat:Pais),Dup,Nocase
Notas	       Memo(150)
RECORD	       Record
Nombre		 String(20)
Apellido	 String(20)
Sexo		 String(1)			 !Femina, Maschio, Empresa
Pais		 String(3)			 !Pais donde est  esta persona
Telefono_1	 String(15)
Telefono_2	 String(15)
Telefono_3	 String(15)
email		 String(40)			 !Direccion de email
Fecha		 Group
Dia		   Byte
Mes		   Byte
Ano		   Short
		 .
Marca		 String(2)
F_Actualiz	 Long				 !Ultima actualizaci¢n
Listar		 String(1)			 !¨Sacar en el listado tefonico?
	     . .
	     Group,over(Dat:Notas)
Dat_MEMO_ROW   String(50),Dim(3)
	     .

Agenda	     File,Pre(AGE),Create,Reclaim
BY_F_Debida    Key(AGE:F_Debida),Dup,Nocase
Descrip	       Memo(250)
RECORD	       Record
F_Ingr		 Long
F_Debida	 Long
F_Real		 Long
Status		 String(1)
	     . .
	     Group,over(AGE:Descrip)
AGE_MEMO_ROW   String(50),Dim(5)
	     .

Tipos	     File,Pre(TIP),Create,Reclaim
por_tipo       Key(TIP:tipo),Nocase
RECORD	       Record
Tipo		 Byte				 !Tipo de componente
Descripcion	 String(20)			 !Descripcion del tipo
	     . .

Piezas	     File,Pre(PIE),Create,Reclaim
BY_clave       Key(PIE:clave),Nocase,Opt
por_tipo       Key(PIE:Tipo),Dup,Nocase
Ubicacion      Memo(50)				 !¨ Donde estan ?
RECORD	       Record
Clave		 String(13)
Tipo		 Byte
Descrip		 String(30)
Existen		 Short
	     . .
	     Group,over(PIE:Ubicacion)
PIE_MEMO_ROW   String(25),Dim(2)
	     .

Nombre	     File,Pre(NOM),Create,Reclaim
CLAVE_NOMBRE   Key(NOM:NOMBRE),Nocase,Opt
CLAVE_CODIGO   Key(NOM:CODIGO),Nocase
RECORD	       Record
Codigo		 Byte				 !Clave de la revista
Nombre		 String(20)			 !nombre de la revista
	     . .

Temas	     File,Pre(Tem),Create,Reclaim
Por_codigo     Key(Tem:Codigo),Nocase
Por_Nombre     Key(Tem:Descripcion),Nocase
RECORD	       Record
Codigo		 Byte				 !C¢digo del tema
Descripcion	 String(15)			 !Descripcion del Tema
	     . .

Articulo     File,Pre(ART),Create,Reclaim
CLAVE	       Key(ART:COD_REVISTA),Dup,Nocase
Por_Temas      Key(ART:Cod_Tema_1,ART:Cod_Tema_2,ART:Cod_Tema_3),Dup,Nocase
Por_Titulo     Index(ART:Titulo),Nocase,Opt
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

Fortune	     File,Pre(For),Create
Key	       Key(For:Number),Nocase,Opt
Fortune	       Memo(750)			 !An interesting sentence
RECORD	       Record
Number		 Short				 !Numero consecutivo
	     . .
	     Group,over(For:Fortune)
For_MEMO_ROW   String(60),Dim(12)
	     .

Una	     File,Pre(Una),Create,Reclaim
por_lapso      Key(Una:Lapso),Dup,Nocase,Opt
RECORD	       Record
Lapso		 String(4)			 !Lapso en que la curs‚
Materia		 String(20)			 !Nombre de la materia
Creditos	 Byte
nota		 Byte
	     . .

Compacts     File,Pre(Com),Create,Reclaim
por_autor      Key(Com:Autor,Com:Titulo),Dup,Nocase
RECORD	       Record
Autor		 String(30)
Titulo		 String(30)
Status		 String(1)
Costo		 Real
Moneda		 String(3)			 !Moneda con que se compro
Ubicacion	 String(15)			 !Donde esta (¨prestado?)
	     . .

Paises	     File,Pre(Pai),Create
	       Owner('Sauron'),Encrypt
Por_codigo     Key(Pai:Codigo),Nocase,Opt
RECORD	       Record
Codigo		 String(3)			 !Codigo telef¢nico del pa¡s
Nombre		 String(40)			 !Nombre del pa¡s
	     . .

	     EJECT('GLOBAL MEMORY VARIABLES')
	     INCLUDE('c:\pablo\clarion\modelos\globals.h')
	     Group,Pre(MEM)
MESSAGE	       String(30)			 !Global Message Area
PAGE	       Short				 !Report Page Number
LINE	       Short				 !Report Line Number
rpt_len96      Byte(96)				 !Longitud de reporte de telef
rpt_len90      Byte(90)
DEVICE	       String('CON {27}')
EMPRESA	       String(60)
Seleccion      String(20)			 !String a buscar en el archivo
Defaults       Group				 !Defaults en ingreso de articu.
Cod_Rev		 Byte				 !Codigo de la revista a ingres
Mes		 Byte
Ano		 Byte
Numero		 Short
	       .
Controles      Group				 !Controles de Impresora
Normal		 String('<18>')
Comprimido	 String('<15>')
Expandido	 String('<27>W1')
UnOctavo	 String('<27>0')		 !espacio entre lineas 1/8"
UnSexto		 String('<27>2')		 !espacio entre lineas 1/6"
	       .
HP_Control     Group				 !Controles de la LaserJet
HPReset		 String('<27>E')
HPComprimido	 String('<27>&k2S')
HPManual	 String('<27>&l2H')		 !Manual feed
HPLegal		 String('<27>&l3A')		 !Papel extraoficio
HPLineas	 String('<27>&l96P')		 !Numero de lineas por pagina
HPUnOctavo	 String('<27>&l8D')
HPSymbolSet	 String('<27>(10U')		 !Symbol set = PC-8
	       .
	     .
	     EJECT('CODE SECTION')
  CODE
  SETHUE(7,0)					 !SET WHITE ON BLACK
  BLANK						 !  AND BLANK
  SETHUE()					 !    THE SCREEN
  open(background)
  InitProc
  salir = 0
  loop until salir = 1
    INITIUS					 !Inicializaci¢n y llama a menu
    ConfSalida
  .
  LastProc
  RETURN					 !EXIT TO DOS

  INCLUDE('c:\pablo\clarion\modelos\netware.utl')

G_CLOSEFILES  PROCEDURE				  !Close FILES
  CODE
  savact# = action
  action = 98
  g_openfiles	     ! Llamar a G_openfiles con par metro de cierre
  action = savact#

G_OPENFILES  PROCEDURE				 !OPEN FILES & CHECK FOR ERROR
SCREEN	     Screen	  Window(1,80),At(25,1),Hue(0,7)
	     .
  CODE
  open(screen)	    !Salvar la Pantalla
  if ACTION = 99
				    ! Hay que reconstruir
      SHOW(25,1,CENTER('Reconstruyendo Fechas ('& records(Fechas) &' registros)',80))
      pack(Fechas)
      open(Fechas)
      BLANK(25,1,1,80)
  elsif action = 98	  ! Cerrar archivos
      SHOW(25,1,CENTER('Cerrando: ' & 'Fechas',80)) !DISPLAY FILE NAME
      CLOSE(Fechas)
  else
				    ! Procedimiento original
     SHOW(25,1,CENTER('Abriendo: ' & 'Fechas',80)) !DISPLAY FILE NAME
     OPEN(Fechas)				    !OPEN THE FILE
     IF ERROR()					    !OPEN RETURNED AN ERROR
       CASE ERRORCODE()				    ! CHECK FOR SPECIFIC ERROR
       OF 46					    !  KEYS NEED TO BE REBUILT
       orof 27					    ! nomatch_err
	 SHOW(25,1,CENTER('Reconstruyendo Fechas',80)) !INDICATE MSG
	 BUILD(Fechas)				    !  CALL THE BUILD PROCEDURE
	 BLANK(25,1,1,80)			    !  BLANK THE MESSAGE
       OF 2					 !IF NOT FOUND,
	 CREATE(Fechas)				 ! CREATE
       ELSE					    ! ANY OTHER ERROR
	 LOOP;STOP('Fechas: ' & ERROR()).	    !  STOP EXECUTION
     . .
  .
  if ACTION = 99
				    ! Hay que reconstruir
      SHOW(25,1,CENTER('Reconstruyendo Agenda ('& records(Agenda) &' registros)',80))
      pack(Agenda)
      open(Agenda)
      BLANK(25,1,1,80)
  elsif action = 98	  ! Cerrar archivos
      SHOW(25,1,CENTER('Cerrando: ' & 'Agenda',80)) !DISPLAY FILE NAME
      CLOSE(Agenda)
  else
				    ! Procedimiento original
     SHOW(25,1,CENTER('Abriendo: ' & 'Agenda',80)) !DISPLAY FILE NAME
     OPEN(Agenda)				    !OPEN THE FILE
     IF ERROR()					    !OPEN RETURNED AN ERROR
       CASE ERRORCODE()				    ! CHECK FOR SPECIFIC ERROR
       OF 46					    !  KEYS NEED TO BE REBUILT
       orof 27					    ! nomatch_err
	 SHOW(25,1,CENTER('Reconstruyendo Agenda',80)) !INDICATE MSG
	 BUILD(Agenda)				    !  CALL THE BUILD PROCEDURE
	 BLANK(25,1,1,80)			    !  BLANK THE MESSAGE
       OF 2					 !IF NOT FOUND,
	 CREATE(Agenda)				 ! CREATE
       ELSE					    ! ANY OTHER ERROR
	 LOOP;STOP('Agenda: ' & ERROR()).	    !  STOP EXECUTION
     . .
  .
  if ACTION = 99
				    ! Hay que reconstruir
      SHOW(25,1,CENTER('Reconstruyendo Tipos ('& records(Tipos) &' registros)',80))
      pack(Tipos)
      open(Tipos)
      BLANK(25,1,1,80)
  elsif action = 98	  ! Cerrar archivos
      SHOW(25,1,CENTER('Cerrando: ' & 'Tipos',80)) !DISPLAY FILE NAME
      CLOSE(Tipos)
  else
				    ! Procedimiento original
     SHOW(25,1,CENTER('Abriendo: ' & 'Tipos',80)) !DISPLAY FILE NAME
     OPEN(Tipos)				    !OPEN THE FILE
     IF ERROR()					    !OPEN RETURNED AN ERROR
       CASE ERRORCODE()				    ! CHECK FOR SPECIFIC ERROR
       OF 46					    !  KEYS NEED TO BE REBUILT
       orof 27					    ! nomatch_err
	 SHOW(25,1,CENTER('Reconstruyendo Tipos',80)) !INDICATE MSG
	 BUILD(Tipos)				    !  CALL THE BUILD PROCEDURE
	 BLANK(25,1,1,80)			    !  BLANK THE MESSAGE
       OF 2					 !IF NOT FOUND,
	 CREATE(Tipos)				 ! CREATE
       ELSE					    ! ANY OTHER ERROR
	 LOOP;STOP('Tipos: ' & ERROR()).	    !  STOP EXECUTION
     . .
  .
  if ACTION = 99
				    ! Hay que reconstruir
      SHOW(25,1,CENTER('Reconstruyendo Piezas ('& records(Piezas) &' registros)',80))
      pack(Piezas)
      open(Piezas)
      BLANK(25,1,1,80)
  elsif action = 98	  ! Cerrar archivos
      SHOW(25,1,CENTER('Cerrando: ' & 'Piezas',80)) !DISPLAY FILE NAME
      CLOSE(Piezas)
  else
				    ! Procedimiento original
     SHOW(25,1,CENTER('Abriendo: ' & 'Piezas',80)) !DISPLAY FILE NAME
     OPEN(Piezas)				    !OPEN THE FILE
     IF ERROR()					    !OPEN RETURNED AN ERROR
       CASE ERRORCODE()				    ! CHECK FOR SPECIFIC ERROR
       OF 46					    !  KEYS NEED TO BE REBUILT
       orof 27					    ! nomatch_err
	 SHOW(25,1,CENTER('Reconstruyendo Piezas',80)) !INDICATE MSG
	 BUILD(Piezas)				    !  CALL THE BUILD PROCEDURE
	 BLANK(25,1,1,80)			    !  BLANK THE MESSAGE
       OF 2					 !IF NOT FOUND,
	 CREATE(Piezas)				 ! CREATE
       ELSE					    ! ANY OTHER ERROR
	 LOOP;STOP('Piezas: ' & ERROR()).	    !  STOP EXECUTION
     . .
  .
  if ACTION = 99
				    ! Hay que reconstruir
      SHOW(25,1,CENTER('Reconstruyendo Nombre ('& records(Nombre) &' registros)',80))
      pack(Nombre)
      open(Nombre)
      BLANK(25,1,1,80)
  elsif action = 98	  ! Cerrar archivos
      SHOW(25,1,CENTER('Cerrando: ' & 'Nombre',80)) !DISPLAY FILE NAME
      CLOSE(Nombre)
  else
				    ! Procedimiento original
     SHOW(25,1,CENTER('Abriendo: ' & 'Nombre',80)) !DISPLAY FILE NAME
     OPEN(Nombre)				    !OPEN THE FILE
     IF ERROR()					    !OPEN RETURNED AN ERROR
       CASE ERRORCODE()				    ! CHECK FOR SPECIFIC ERROR
       OF 46					    !  KEYS NEED TO BE REBUILT
       orof 27					    ! nomatch_err
	 SHOW(25,1,CENTER('Reconstruyendo Nombre',80)) !INDICATE MSG
	 BUILD(Nombre)				    !  CALL THE BUILD PROCEDURE
	 BLANK(25,1,1,80)			    !  BLANK THE MESSAGE
       OF 2					 !IF NOT FOUND,
	 CREATE(Nombre)				 ! CREATE
       ELSE					    ! ANY OTHER ERROR
	 LOOP;STOP('Nombre: ' & ERROR()).	    !  STOP EXECUTION
     . .
  .
  if ACTION = 99
				    ! Hay que reconstruir
      SHOW(25,1,CENTER('Reconstruyendo Temas ('& records(Temas) &' registros)',80))
      pack(Temas)
      open(Temas)
      BLANK(25,1,1,80)
  elsif action = 98	  ! Cerrar archivos
      SHOW(25,1,CENTER('Cerrando: ' & 'Temas',80)) !DISPLAY FILE NAME
      CLOSE(Temas)
  else
				    ! Procedimiento original
     SHOW(25,1,CENTER('Abriendo: ' & 'Temas',80)) !DISPLAY FILE NAME
     OPEN(Temas)				    !OPEN THE FILE
     IF ERROR()					    !OPEN RETURNED AN ERROR
       CASE ERRORCODE()				    ! CHECK FOR SPECIFIC ERROR
       OF 46					    !  KEYS NEED TO BE REBUILT
       orof 27					    ! nomatch_err
	 SHOW(25,1,CENTER('Reconstruyendo Temas',80)) !INDICATE MSG
	 BUILD(Temas)				    !  CALL THE BUILD PROCEDURE
	 BLANK(25,1,1,80)			    !  BLANK THE MESSAGE
       OF 2					 !IF NOT FOUND,
	 CREATE(Temas)				 ! CREATE
       ELSE					    ! ANY OTHER ERROR
	 LOOP;STOP('Temas: ' & ERROR()).	    !  STOP EXECUTION
     . .
  .
  if ACTION = 99
				    ! Hay que reconstruir
      SHOW(25,1,CENTER('Reconstruyendo Articulo ('& records(Articulo) &' registros)',80))
      pack(Articulo)
      open(Articulo)
      BLANK(25,1,1,80)
  elsif action = 98	  ! Cerrar archivos
      SHOW(25,1,CENTER('Cerrando: ' & 'Articulo',80)) !DISPLAY FILE NAME
      CLOSE(Articulo)
  else
				    ! Procedimiento original
     SHOW(25,1,CENTER('Abriendo: ' & 'Articulo',80)) !DISPLAY FILE NAME
     OPEN(Articulo)				    !OPEN THE FILE
     IF ERROR()					    !OPEN RETURNED AN ERROR
       CASE ERRORCODE()				    ! CHECK FOR SPECIFIC ERROR
       OF 46					    !  KEYS NEED TO BE REBUILT
       orof 27					    ! nomatch_err
	 SHOW(25,1,CENTER('Reconstruyendo Articulo',80)) !INDICATE MSG
	 BUILD(Articulo)			    !  CALL THE BUILD PROCEDURE
	 BLANK(25,1,1,80)			    !  BLANK THE MESSAGE
       OF 2					 !IF NOT FOUND,
	 CREATE(Articulo)			 ! CREATE
       ELSE					    ! ANY OTHER ERROR
	 LOOP;STOP('Articulo: ' & ERROR()).	    !  STOP EXECUTION
     . .
  .
  if ACTION = 99
				    ! Hay que reconstruir
      SHOW(25,1,CENTER('Reconstruyendo Fortune ('& records(Fortune) &' registros)',80))
      pack(Fortune)
      open(Fortune)
      BLANK(25,1,1,80)
  elsif action = 98	  ! Cerrar archivos
      SHOW(25,1,CENTER('Cerrando: ' & 'Fortune',80)) !DISPLAY FILE NAME
      CLOSE(Fortune)
  else
				    ! Procedimiento original
     SHOW(25,1,CENTER('Abriendo: ' & 'Fortune',80)) !DISPLAY FILE NAME
     OPEN(Fortune)				    !OPEN THE FILE
     IF ERROR()					    !OPEN RETURNED AN ERROR
       CASE ERRORCODE()				    ! CHECK FOR SPECIFIC ERROR
       OF 46					    !  KEYS NEED TO BE REBUILT
       orof 27					    ! nomatch_err
	 SHOW(25,1,CENTER('Reconstruyendo Fortune',80)) !INDICATE MSG
	 BUILD(Fortune)				    !  CALL THE BUILD PROCEDURE
	 BLANK(25,1,1,80)			    !  BLANK THE MESSAGE
       OF 2					 !IF NOT FOUND,
	 CREATE(Fortune)			 ! CREATE
       ELSE					    ! ANY OTHER ERROR
	 LOOP;STOP('Fortune: ' & ERROR()).	    !  STOP EXECUTION
     . .
  .
  if ACTION = 99
				    ! Hay que reconstruir
      SHOW(25,1,CENTER('Reconstruyendo Una ('& records(Una) &' registros)',80))
      pack(Una)
      open(Una)
      BLANK(25,1,1,80)
  elsif action = 98	  ! Cerrar archivos
      SHOW(25,1,CENTER('Cerrando: ' & 'Una',80)) !DISPLAY FILE NAME
      CLOSE(Una)
  else
				    ! Procedimiento original
     SHOW(25,1,CENTER('Abriendo: ' & 'Una',80)) !DISPLAY FILE NAME
     OPEN(Una)					    !OPEN THE FILE
     IF ERROR()					    !OPEN RETURNED AN ERROR
       CASE ERRORCODE()				    ! CHECK FOR SPECIFIC ERROR
       OF 46					    !  KEYS NEED TO BE REBUILT
       orof 27					    ! nomatch_err
	 SHOW(25,1,CENTER('Reconstruyendo Una',80)) !INDICATE MSG
	 BUILD(Una)				    !  CALL THE BUILD PROCEDURE
	 BLANK(25,1,1,80)			    !  BLANK THE MESSAGE
       OF 2					 !IF NOT FOUND,
	 CREATE(Una)				 ! CREATE
       ELSE					    ! ANY OTHER ERROR
	 LOOP;STOP('Una: ' & ERROR()).		    !  STOP EXECUTION
     . .
  .
  if ACTION = 99
				    ! Hay que reconstruir
      SHOW(25,1,CENTER('Reconstruyendo Compacts ('& records(Compacts) &' registros)',80))
      pack(Compacts)
      open(Compacts)
      BLANK(25,1,1,80)
  elsif action = 98	  ! Cerrar archivos
      SHOW(25,1,CENTER('Cerrando: ' & 'Compacts',80)) !DISPLAY FILE NAME
      CLOSE(Compacts)
  else
				    ! Procedimiento original
     SHOW(25,1,CENTER('Abriendo: ' & 'Compacts',80)) !DISPLAY FILE NAME
     OPEN(Compacts)				    !OPEN THE FILE
     IF ERROR()					    !OPEN RETURNED AN ERROR
       CASE ERRORCODE()				    ! CHECK FOR SPECIFIC ERROR
       OF 46					    !  KEYS NEED TO BE REBUILT
       orof 27					    ! nomatch_err
	 SHOW(25,1,CENTER('Reconstruyendo Compacts',80)) !INDICATE MSG
	 BUILD(Compacts)			    !  CALL THE BUILD PROCEDURE
	 BLANK(25,1,1,80)			    !  BLANK THE MESSAGE
       OF 2					 !IF NOT FOUND,
	 CREATE(Compacts)			 ! CREATE
       ELSE					    ! ANY OTHER ERROR
	 LOOP;STOP('Compacts: ' & ERROR()).	    !  STOP EXECUTION
     . .
  .
  if ACTION = 99
				    ! Hay que reconstruir
      SHOW(25,1,CENTER('Reconstruyendo Paises ('& records(Paises) &' registros)',80))
      pack(Paises)
      open(Paises)
      BLANK(25,1,1,80)
  elsif action = 98	  ! Cerrar archivos
      SHOW(25,1,CENTER('Cerrando: ' & 'Paises',80)) !DISPLAY FILE NAME
      CLOSE(Paises)
  else
				    ! Procedimiento original
     SHOW(25,1,CENTER('Abriendo: ' & 'Paises',80)) !DISPLAY FILE NAME
     OPEN(Paises)				    !OPEN THE FILE
     IF ERROR()					    !OPEN RETURNED AN ERROR
       CASE ERRORCODE()				    ! CHECK FOR SPECIFIC ERROR
       OF 46					    !  KEYS NEED TO BE REBUILT
       orof 27					    ! nomatch_err
	 SHOW(25,1,CENTER('Reconstruyendo Paises',80)) !INDICATE MSG
	 BUILD(Paises)				    !  CALL THE BUILD PROCEDURE
	 BLANK(25,1,1,80)			    !  BLANK THE MESSAGE
       OF 2					 !IF NOT FOUND,
	 CREATE(Paises)				 ! CREATE
       ELSE					    ! ANY OTHER ERROR
	 LOOP;STOP('Paises: ' & ERROR()).	    !  STOP EXECUTION
     . .
  .
