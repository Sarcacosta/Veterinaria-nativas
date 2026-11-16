/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucompensar.veterinaria.filter;


import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getRequestURI();

        // 1. Recursos públicos (Login, CSS, JS, Imágenes)
        if (path.endsWith("/login") ||
            path.endsWith("/index.html") ||
            path.contains("/css/") ||
            path.contains("/js/") ||
            path.contains("/images/")) {

            chain.doFilter(request, response);
            return;
        }

        // 2. ¡NUEVO! Permitir el paso a las rutas de la API para la App Android
                // 2. Permitir el paso a las rutas de la API para la App Android
        if (path.contains("/api/mascotas") || 
            path.contains("/api/veterinarios") || 
            path.contains("/api/citas") || 
            path.contains("/api/usuarios")) {

            chain.doFilter(request, response);
            return;
        }

        // 3. Validar sesión para el resto (el panel web)
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("usuario") == null) {

            // Si es una llamada API no permitida, responde 401
            if (path.contains("/api/")) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } 
            // Si es una página web, redirecciona al login
            else {
                resp.sendRedirect(req.getContextPath() + "/index.html");
            }

            return;
        }

        chain.doFilter(request, response);
    }
}