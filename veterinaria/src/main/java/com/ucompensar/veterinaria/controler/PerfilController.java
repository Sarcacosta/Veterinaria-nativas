package com.ucompensar.veterinaria.controler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucompensar.veterinaria.DTO.UsuarioDTO;
import com.ucompensar.veterinaria.model.entity.Usuario;
import com.ucompensar.veterinaria.util.JPAUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/api/perfil")
public class PerfilController extends HttpServlet {
    private final ObjectMapper mapper = new ObjectMapper();

    public record Cambios(String nombre, String email) {}

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        procesar(req, resp, null);
    }

    @Override protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Cambios cambios;
        try { cambios = mapper.readValue(req.getInputStream(), Cambios.class); }
        catch (IOException e) { resp.sendError(400); return; }
        if (cambios == null || cambios.nombre() == null || cambios.nombre().isBlank()
                || cambios.nombre().trim().length() > 100 || cambios.email() == null
                || cambios.email().trim().length() > 254
                || !cambios.email().trim().matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            resp.sendError(400);
            return;
        }
        procesar(req, resp, cambios);
    }

    private void procesar(HttpServletRequest req, HttpServletResponse resp, Cambios cambios) throws IOException {
        UsuarioDTO sesion = (UsuarioDTO) req.getSession(false).getAttribute("usuario");
        var em = JPAUtil.getEntityManager();
        try {
            if (cambios != null) em.getTransaction().begin();
            Usuario usuario = em.find(Usuario.class, sesion.getId());
            if (usuario == null || !Boolean.TRUE.equals(usuario.getEstado())) {
                req.getSession(false).invalidate();
                resp.sendError(401);
                return;
            }
            if (cambios != null) {
                usuario.setNombre(cambios.nombre().trim());
                usuario.setEmail(cambios.email().trim());
                if (usuario.getPropietario() != null) {
                    usuario.getPropietario().setNombres(usuario.getNombre());
                    usuario.getPropietario().setEmail(usuario.getEmail());
                }
                em.getTransaction().commit();
            }
            Map<String, Object> perfil = new LinkedHashMap<>();
            perfil.put("id", usuario.getIdUsuario());
            perfil.put("username", usuario.getUsername());
            perfil.put("nombre", usuario.getNombre());
            perfil.put("email", usuario.getEmail());
            perfil.put("roles", sesion.getRoles());
            perfil.put("idPropietario", usuario.getPropietario() == null ? null
                    : usuario.getPropietario().getIdPropietario());
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            mapper.writeValue(resp.getWriter(), perfil);
        } finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            em.close();
        }
    }
}
