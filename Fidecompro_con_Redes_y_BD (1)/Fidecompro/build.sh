#!/bin/bash
# Script de compilación y ejecución para Fidecompro (arquitectura cliente-servidor)
#
# Uso:
#   ./build.sh compilar        Compila todo el proyecto
#   ./build.sh servidor        Compila (si hace falta) y arranca el SERVIDOR
#   ./build.sh cliente         Compila (si hace falta) y arranca el CLIENTE (GUI)
#   ./build.sh                 Compila y arranca el cliente contra localhost (por defecto)

compilar() {
    mkdir -p out
    find src -name "*.java" > sources.txt
    javac -encoding UTF-8 -d out -sourcepath src @sources.txt
}

MODO="${1:-cliente}"

case "$MODO" in
    compilar)
        compilar && echo "Compilación exitosa."
        ;;
    servidor)
        compilar && java -cp out fidecompro.Main servidor "${2:-5050}"
        ;;
    cliente)
        compilar && java -cp out fidecompro.Main "${2:-localhost}" "${3:-5050}"
        ;;
    *)
        echo "Uso: ./build.sh [compilar|servidor|cliente] [host] [puerto]"
        exit 1
        ;;
esac
