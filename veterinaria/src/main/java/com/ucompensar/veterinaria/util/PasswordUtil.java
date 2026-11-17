/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucompensar.veterinaria.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    public static String cifrar(String passwordPlano) {
        return BCrypt.hashpw(passwordPlano, BCrypt.gensalt());
    }

    public static boolean verificar(String passwordPlano, String hash) {
        if (passwordPlano == null || hash == null) return false;
        try {
            return BCrypt.checkpw(passwordPlano, hash);
        } catch (IllegalArgumentException incorrecto) {
            return false;
        }
    }
    public static void main(String[] args) {
    // 1. Probar el cifrado
    String passwordOriginal = "12345678";
    String hashGenerado = cifrar(passwordOriginal);
    
    System.out.println("Contraseña en texto plano: " + passwordOriginal);
    System.out.println("Hash generado: " + hashGenerado);
    
    // 2. Probar la verificación con la contraseña correcta
    boolean esValida = verificar(passwordOriginal, hashGenerado);
    System.out.println("¿La contraseña es válida?: " + esValida);
    
    // 3. Probar la verificación con una contraseña incorrecta
    boolean esInvalida = verificar("ClaveErronea", hashGenerado);
    System.out.println("¿Contraseña errónea es válida?: " + esInvalida);
}
}

