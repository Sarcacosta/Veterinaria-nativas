/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucompensar.veterinaria.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class BackupService {

    //Para windows
    //private static final String MYSQL_PATH = "C:\\xampp\\mysql\\bin\\mysqldump.exe";
    
    //Para linux primero verifico donde esta mysqldump y si tiene permiso de exe
    //find /opt/lampp -name mysqldump -type f 2>/dev/null
    //ls -l /opt/lampp/bin/mysqldump
    //debe salir: -rwxr-xr-x 1 root root ... /opt/lampp/bin/mysqldump
    private static final String MYSQL_PATH = "/opt/lampp/bin/mysqldump";

    private static final String DB_NAME = "veterinaria";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";
    private static final String DB_HOST = "localhost"; // MUY IMPORTANTE

    public File generarBackup() throws IOException {

        String fecha = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        //Para windows
        //File backup = new File("backup_" + fecha + ".sql");
        
        //Para linux creo un directorio exclusivo
        File directorioBackup = new File("/home/cahuner/backups/veterinaria");

        if (!directorioBackup.exists()) {
            directorioBackup.mkdirs();
        }

        File backup = new File(directorioBackup,"backup_" + fecha + ".sql");

        ProcessBuilder pb = new ProcessBuilder(
                MYSQL_PATH,
                "--host=" + DB_HOST,
                "--user=" + DB_USER,
                "--password=" + DB_PASS,
                DB_NAME
        );

        // Redirige salida SQL al archivo
        pb.redirectOutput(backup);

        // Captura errores
        pb.redirectError(ProcessBuilder.Redirect.PIPE);

        try {
            Process process = pb.start();

            String error;
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()))) {

                error = br.lines().collect(Collectors.joining("\n"));
            }

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException(
                        "mysqldump falló (" + exitCode + "):\n" + error
                );
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Proceso interrumpido", e);
        }

        return backup;
    }
}