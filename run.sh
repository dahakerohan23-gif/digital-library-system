#!/usr/bin/env bash
echo "========================================================"
echo "  Digital Library Management System - Launch Script"
echo "========================================================"
echo ""

echo "[1/2] Compiling StandaloneServer.java..."
javac StandaloneServer.java
if [ $? -ne 0 ]; then
    echo "[ERROR] Compilation failed. Please ensure JDK is installed."
    exit 1
fi

echo "[2/2] Starting Digital Library Server on http://localhost:8080 ..."
echo ""
echo "Admin Portal:     http://localhost:8080/admin.html"
echo "Member Dashboard: http://localhost:8080/user.html"
echo "Book Catalogue:   http://localhost:8080/index.html"
echo ""

java StandaloneServer
