package com.ucompensar.veterinaria.controler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucompensar.veterinaria.DTO.CitaDTO;
import com.ucompensar.veterinaria.DTO.UsuarioDTO;
import com.ucompensar.veterinaria.model.entity.Usuario;
import com.ucompensar.veterinaria.service.ReservasService;
import com.ucompensar.veterinaria.util.JPAUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

@WebServlet(name = "ReservasSeguras", urlPatterns = "/interno/reservas")
public class ReservasController extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(ReservasController.class.getName());
    private final ObjectMapper json = new ObjectMapper();
    private final ReservasService reservas = new ReservasService();

    @Override protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String ruta = (String) req.getAttribute("ruta.reservas");
        if (ruta == null) { resp.sendError(404); return; }
        UsuarioDTO sesion = (UsuarioDTO) req.getSession(false).getAttribute("usuario");
        try {
            Long propietario = ruta.startsWith("/api/mis-citas") ? propietario(sesion.getId()) : null;
            switch (req.getMethod()) {
                case "GET" -> responder(resp, 200, reservas.disponibles(
                        Long.parseLong(req.getParameter("veterinarioId")), req.getParameter("fecha")));
                case "POST", "PUT" -> {
                    CitaDTO dto = json.readValue(req.getInputStream(), CitaDTO.class);
                    boolean editar = "PUT".equals(req.getMethod());
                    responder(resp, editar ? 200 : 201, reservas.guardar(dto, propietario, editar));
                }
                case "DELETE" -> {
                    reservas.cancelar(Long.parseLong(req.getParameter("id")), propietario);
                    resp.setStatus(204);
                }
                default -> resp.sendError(405);
            }
        } catch (JsonProcessingException | IllegalArgumentException error) {
            responder(resp, 400, Map.of("error", "Revisa la fecha, la hora y los datos de la cita."));
        } catch (ReservasService.Denegada error) {
            responder(resp, 403, Map.of("error", "Tu cuenta no puede acceder a esta cita."));
        } catch (ReservasService.NoEncontrada error) {
            responder(resp, 404, Map.of("error", "El registro solicitado no existe."));
        } catch (ReservasService.Conflicto error) {
            responder(resp, 409, Map.of("error", "El turno ya no está disponible o la cita no admite este cambio."));
        } catch (RuntimeException error) {
            LOG.warning("Operación de cita fallida (" + error.getClass().getSimpleName() + ")");
            responder(resp, 500, Map.of("error", "No se pudo completar la operación."));
        }
    }

    private Long propietario(Long usuarioId) {
        try (var em = JPAUtil.getEntityManager()) {
            Usuario usuario = em.find(Usuario.class, usuarioId);
            if (usuario == null || !Boolean.TRUE.equals(usuario.getEstado()) || usuario.getPropietario() == null) {
                throw new ReservasService.Denegada();
            }
            return usuario.getPropietario().getIdPropietario();
        }
    }

    private void responder(HttpServletResponse resp, int status, Object body) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json;charset=UTF-8");
        json.writeValue(resp.getWriter(), body);
    }
}
