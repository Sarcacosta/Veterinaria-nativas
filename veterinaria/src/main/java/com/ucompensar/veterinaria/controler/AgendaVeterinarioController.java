package com.ucompensar.veterinaria.controler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucompensar.veterinaria.DTO.UsuarioDTO;
import com.ucompensar.veterinaria.model.entity.Usuario;
import com.ucompensar.veterinaria.service.CitaService;
import com.ucompensar.veterinaria.util.JPAUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/api/agenda-veterinario")
public class AgendaVeterinarioController extends HttpServlet {
    private final ObjectMapper mapper = new ObjectMapper();
    private final CitaService citas = new CitaService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UsuarioDTO sesion = (UsuarioDTO) req.getSession(false).getAttribute("usuario");
        Long veterinarioId;
        try (var em = JPAUtil.getEntityManager()) {
            Usuario usuario = em.find(Usuario.class, sesion.getId());
            if (usuario == null || !Boolean.TRUE.equals(usuario.getEstado())) {
                req.getSession(false).invalidate();
                resp.sendError(401);
                return;
            }
            if (usuario.getVeterinario() == null) {
                resp.sendError(422);
                return;
            }
            veterinarioId = usuario.getVeterinario().getIdVeterinario();
        }
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), citas.obtenerCitasPorVeterinario(veterinarioId));
    }
}
