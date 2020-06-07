  ! GLOBALS.H
  ! Variables globales comunes a todos los sistemas
xxcontr    file,name(ctrlfile),pre(xxc) ! Archivo de control del sistema
	       Owner('Sauron'),Encrypt
record	       record
empresa	       string(60)
sistema	       string(60)
datadisk       string(1)
datapath       string(64)
PassWord       string(10)
	     . .
ACTION	     SHORT	 !0 = NO ACTION
						 !1 = ADD RECORD
						 !2 = CHANGE RECORD
						 !3 = DELETE RECORD
						 !4 = LOOKUP FIELD
						 !99= Reconstruir archivos
ctrlfile     string(64)	  ! Especificaci¢n del archivo de control
oldpath	     string(64)	  ! Guadar Path actual para poder regresar
nombre_meses STRING('EneFebMarAbrMayJunJulAgoSepOctNovDic')
nombre_mes   STRING(3),DIM(12),OVER(nombre_meses)
nombre_dias  STRING('DomLunMarMieJueVieSab')
nombre_dia   STRING(3),DIM(7),OVER(nombre_dias)
salir	     short
background   Screen	  Pre(BCK),Hue(1,3)
	       Row(1,1)	  String('ÚÄ{78}¿')
	       Row(2,1)	  Repeat(2);String('³<0{78}>³') .
	       Row(4,1)	  String('ÀÄ{78}Ù')
	       Row(5,1)	  Repeat(21);String('°{80}'),Hue(3,1) .
	 .
 ! esto hay que ver como se usa desde el designer
 ! Controles	  group			!Controles de Impresora
 ! Normal	string('<18>')
 ! Comprimido	string('<15>')
 ! Expandido	string('<27>W1')
 !	      .
