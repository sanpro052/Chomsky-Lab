@echo off
cd /d "%~dp0"
if not exist "out" mkdir "out"
javac -encoding UTF-8 -d out src\*.java
if errorlevel 1 (
    echo.
    echo ERROR: No se pudo compilar. Verifica las clases Java dentro de src.
    pause
    exit /b 1
)
java -cp out Main
if errorlevel 1 (
    echo.
    echo ERROR: No se pudo ejecutar la aplicacion.
    pause
)
