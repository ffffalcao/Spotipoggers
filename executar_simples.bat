@echo off
echo ========================================
echo         SPOTIPOGGERS - Player de Musica
echo ========================================
echo.

echo Compilando projeto...
javac -cp "lib/*;src" src/passatempo/*.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERRO: Falha na compilacao!
    pause
    exit /b 1
)

echo.
echo Iniciando Spotipoggers...
echo.
java -cp "lib/*;src" passatempo.Passatempo

echo.
pause 