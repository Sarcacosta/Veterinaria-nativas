package com.ucompensar.veterinaria.controler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucompensar.veterinaria.DTO.UsuarioDTO;
import com.ucompensar.veterinaria.model.entity.Usuario;
import com.ucompensar.veterinaria.model.entity.Veterinario;
import com.ucompensar.veterinaria.service.HorarioService;
import com.ucompensar.veterinaria.util.JPAUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@WebServlet("/api/horarios-atencion")
public class HorarioController extends HttpServlet {
    private final ObjectMapper mapper = new ObjectMapper();
    private final HorarioService service = new HorarioService();

    public record Cambio(List<String> horas) {}

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            long vet = veterinarioId(req);
            String fecha = fecha(req);
            try (var em = JPAUtil.getEntityManager()) {
                if (em.find(Veterinario.class, vet) == null) { resp.sendError(404); return; }
                responder(resp, service.jornada(em, vet, fecha));
            }
        } catch (SinFicha error) { resp.sendError(422); }
        catch (IllegalArgumentException | java.time.DateTimeException error) { resp.sendError(400); }
    }

    @Override protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Cambio cambio;
        try { cambio = mapper.readValue(req.getInputStream(), Cambio.class); }
        catch (IOException error) { resp.sendError(400); return; }
        if (cambio == null || cambio.horas() == null) { resp.sendError(400); return; }
        try {
            long vet = veterinarioId(req);
            responder(resp, service.guardar(vet, fecha(req), cambio.horas()));
        } catch (SinFicha error) {
            resp.sendError(422);
        } catch (IllegalArgumentException | java.time.DateTimeException error) {
            resp.sendError(400);
        } catch (IllegalStateException error) {
            if (!"HORARIO_CON_CITAS".equals(error.getMessage())) throw error;
            resp.sendError(409);
        }
    }

    private static class SinFicha extends RuntimeException {}

    private long veterinarioId(HttpServletRequest req) {
        UsuarioDTO sesion = (UsuarioDTO) req.getSession(false).getAttribute("usuario");
        var roles = sesion.getRoles();
        if (roles.contains("Administrador") || roles.contains("Recepcion") || roles.contains("Recepcionista")) {
            return Long.parseLong(req.getParameter("veterinarioId"));
        }
        try (var em = JPAUtil.getEntityManager()) {
            Usuario usuario = em.find(Usuario.class, sesion.getId());
            if (usuario == null || !Boolean.TRUE.equals(usuario.getEstado()) || usuario.getVeterinario() == null) {
                throw new SinFicha();
            }
            return usuario.getVeterinario().getIdVeterinario();
        }
    }

    private String fecha(HttpServletRequest req) {
        String valor = req.getParameter("fecha");
        if (valor == null || !valor.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")
                || LocalDate.parse(valor).isBefore(LocalDate.now())) throw new IllegalArgumentException();
        return valor;
    }

    private void responder(HttpServletResponse resp, List<String> horas) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), Map.of("horas", horas));
    }
}
