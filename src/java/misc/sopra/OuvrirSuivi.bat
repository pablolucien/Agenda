@echo off
echo Hago esto porque eres t£, pero deber¡as usar Ant o Maven u otra cosa.
javac misc\sopra\OuvrirSuivi.java
if errorlevel 1 goto error
REM jar cfm OuvrirSuivi.jar OuvrirSuivi.mf OuvrirSuivi*.*

REM jar cfm misc\sopra\OuvrirSuivi.jar misc\sopra\OuvrirSuivi.mf misc dialog-question.png

jar cfm OuvrirSuivi.jar OuvrirSuivi.mf misc dialog-question.png

goto ok
:error
echo ****************************************************************************
echo Ha habido errores de compilaci¢n. Corregirlos y volver a ejecutar el comando
echo ****************************************************************************
pause
goto fin
:ok
echo Yast 
:fin
