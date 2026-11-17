package com.ucompensar.veterinaria.controler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucompensar.veterinaria.DTO.CitaDTO;
import com.ucompensar.veterinaria.DTO.UsuarioDTO;

import com.ucompensar.veterinaria.model.entity.Usuario;
import com.ucompensar.veterinaria.model.entity.Veterinario;
import com.ucompensar.veterinaria.service.CitaService;
import com.ucompensar.veterinaria.util.JPAUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;

@WebServlet(urlPatterns = {"/api/mis-citas", "/api/mis-citas/disponibles", "/api/mis-citas/veterinarios"})
public class CitasPropietarioController extends HttpServlet {
    private final ObjectMapper mapper = new ObjectMapper();
    private final CitaService citas = new CitaService();

    public record VeterinarioResumen(Long id, String nombres, String apellidos, String especialidad) {}

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!cuentaVinculada(req)) { resp.sendError(403); return; }
        switch (req.getServletPath()) {
            case "/api/mis-citas/veterinarios" -> json(resp, 200, veterinarios());
            default -> {
                var resultado = new ArrayList<CitaDTO>();
                for (Long id : mascotasPropias(req)) resultado.addAll(citas.obtenerCitasPorMascota(id));
                resultado.sort(java.util.Comparator.comparing(CitaDTO::getFecha,
                        java.util.Comparator.nullsLast(String::compareTo)).thenComparing(CitaDTO::getHora,
                        java.util.Comparator.nullsLast(String::compareTo)));
                json(resp, 200, resultado);
            }
        }
    }

    private Long usuarioId(HttpServletRequest req) {
        return ((UsuarioDTO) req.getSession(false).getAttribute("usuario")).getId();
    }

    private boolean cuentaVinculada(HttpServletRequest req) {
        try (var em = JPAUtil.getEntityManager()) {
            Usuario usuario = em.find(Usuario.class, usuarioId(req));
            return usuario != null && Boolean.TRUE.equals(usuario.getEstado()) && usuario.getPropietario() != null;
        }
    }

    private java.util.List<Long> mascotasPropias(HttpServletRequest req) {
        try (var em = JPAUtil.getEntityManager()) {
            return em.createQuery("SELECT m.idMascota FROM Mascota m, Usuario u "
                    + "WHERE u.idUsuario = :id AND u.estado = true AND m.propietario = u.propietario", Long.class)
                    .setParameter("id", usuarioId(req)).getResultList();
        }
    }

    private java.util.List<VeterinarioResumen> veterinarios() {
        try (var em = JPAUtil.getEntityManager()) {
            return em.createQuery("SELECT v FROM Veterinario v ORDER BY v.nombres, v.apellidos", Veterinario.class)
                    .getResultList().stream().map(v -> new VeterinarioResumen(
                            v.getIdVeterinario(), v.getNombres(), v.getApellidos(), v.getEspecialidad())).toList();
        }
    }

    private void json(HttpServletResponse resp, int status, Object data) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), data);
    }
}

