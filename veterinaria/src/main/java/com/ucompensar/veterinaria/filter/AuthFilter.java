/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucompensar.veterinaria.filter;

import com.ucompensar.veterinaria.DTO.UsuarioDTO;
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

        String context = req.getContextPath();
        String ruta = req.getServletPath();
        boolean swaggerResource = path.equals(context + "/swagger/index.html")
                || path.equals(context + "/swagger/openapi.json")
                || path.equals(context + "/swagger/init.js")
                || path.equals(context + "/webjars/swagger-ui/5.34.1/swagger-ui.css")
                || path.equals(context + "/webjars/swagger-ui/5.34.1/swagger-ui-bundle.js");

        // Recursos públicos
        if (swaggerResource || path.equals(context + "/api/register") ||
            path.equals(context + "/api/login") ||
            (!ruta.startsWith("/api/") && (ruta.equals("/index.html")
                || ruta.startsWith("/css/") || ruta.startsWith("/js/")
                || ruta.startsWith("/images/") || ruta.startsWith("/img/")))) {

            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("usuario") == null) {

            // Si es una llamada API responde 401
            if (path.contains("/api/")) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } 
            // Si es una página web redirecciona al login
            else {
                resp.sendRedirect(req.getContextPath() + "/index.html");
            }

            return;
        }

        UsuarioDTO usuario = (UsuarioDTO) session.getAttribute("usuario");
        String metodo = req.getMethod();
        boolean administrador = usuario.getRoles().contains("Administrador");
        boolean recepcion = usuario.getRoles().contains("Recepcion")
                || usuario.getRoles().contains("Recepcionista");
        boolean veterinario = usuario.getRoles().contains("Veterinario");
        boolean propietario = usuario.getRoles().contains("Propietario");
        boolean comun = ruta.equals("/api/perfil") || ruta.equals("/api/logout")
                || ruta.equals("/api/estadisticas");
        boolean gestion = administrador || recepcion;
        boolean leer = metodo.equals("GET");

        if (ruta.startsWith("/api/") && !comun) {
            boolean permitido;
            if (ruta.equals("/api/horarios-atencion")) {
                permitido = (gestion || veterinario) && (leer || metodo.equals("PUT"));
            } else if (ruta.equals("/api/agenda-veterinario")) {
                permitido = veterinario && leer;
            } else if (ruta.equals("/api/citas") || ruta.startsWith("/api/citas/")) {
                permitido = gestion;
            } else if (ruta.equals("/api/mis-citas") || ruta.equals("/api/mis-citas/disponibles")
                    || ruta.equals("/api/mis-citas/veterinarios")) {
                permitido = propietario && (leer || (ruta.equals("/api/mis-citas")
                        && (metodo.equals("POST") || metodo.equals("PUT") || metodo.equals("DELETE"))));
            } else if (ruta.equals("/api/mascotas/editar")) {
                permitido = metodo.equals("PUT") && (gestion || (propietario && !veterinario));
            } else if (ruta.equals("/api/mascotas")) {
                permitido = gestion || (leer && veterinario)
                        || (propietario && !veterinario && (leer || metodo.equals("POST")));
            } else if (ruta.equals("/api/usuarios") || ruta.equals("/api/roles")
                    || ruta.equals("/api/usuarios-roles") || ruta.equals("/api/auditoria")
                    || ruta.equals("/api/backup")) {
                permitido = administrador;
            } else {
                permitido = gestion || (veterinario && (leer || ruta.equals("/api/historiales")));
            }
            if (!permitido) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }
        boolean escribeCita = metodo.equals("POST") || metodo.equals("PUT") || metodo.equals("DELETE");
        if (ruta.equals("/api/citas/disponibles") || ruta.equals("/api/mis-citas/disponibles")
                || (escribeCita && (ruta.equals("/api/mis-citas") || ruta.equals("/api/citas")
                        || ruta.startsWith("/api/citas/")))) {
            req.setAttribute("ruta.reservas", ruta);
            req.getRequestDispatcher("/interno/reservas").forward(request, response);
            return;
        }
        chain.doFilter(request, response);
    }
}