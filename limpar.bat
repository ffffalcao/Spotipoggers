@echo off
echo Limpando arquivos compilados...
echo.

echo Removendo arquivos .class...
del /s /q *.class 2>nul
del /s /q src\passatempo\*.class 2>nul

echo Limpeza concluida!
echo.
pause 