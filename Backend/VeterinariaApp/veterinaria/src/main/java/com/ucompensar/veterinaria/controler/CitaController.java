package com.ucompensar.veterinaria.controler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucompensar.veterinaria.DTO.CitaDTO;
import com.ucompensar.veterinaria.service.CitaService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/api/citas", "/api/citas/mias", "/api/citas/disponibles", "/api/citas/veterinario"})
public class CitaController extends HttpServlet {

    private final CitaService service = new CitaService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String path = req.getServletPath();
        String fecha = req.getParameter("fecha");
        String mascotaIdParam = req.getParameter("mascotaId");

        if ("/api/citas/disponibles".equals(path)) {
            // Ruta: /api/citas/disponibles?veterinarioId=1&fecha=2026-11-18
            Long vetId = Long.parseLong(req.getParameter("veterinarioId"));
            mapper.writeValue(resp.getWriter(), service.obtenerHorasDisponibles(vetId, fecha));
            
        } else if ("/api/citas/veterinario".equals(path)) {
            // Ruta: /api/citas/veterinario?veterinarioId=1&fecha=2026-11-18
            Long vetId = Long.parseLong(req.getParameter("veterinarioId"));
            if (fecha != null && !fecha.isEmpty()) {
                mapper.writeValue(resp.getWriter(), service.obtenerCitasPorVeterinarioYFecha(vetId, fecha));
            } else {
                mapper.writeValue(resp.getWriter(), service.obtenerCitasPorVeterinario(vetId));
            }
            
        } else {
            // Ruta: /api/citas/mias o /api/citas
            if (mascotaIdParam != null && !mascotaIdParam.isEmpty()) {
                // Filtro por mascota (y opcionalmente fecha)
                Long mascotaId = Long.parseLong(mascotaIdParam);
                if (fecha != null && !fecha.isEmpty()) {
                    mapper.writeValue(resp.getWriter(), service.obtenerCitasPorMascotaYFecha(mascotaId, fecha));
                } else {
                    mapper.writeValue(resp.getWriter(), service.obtenerCitasPorMascota(mascotaId));
                }
            } else if (fecha != null && !fecha.isEmpty()) {
                // Filtro solo por fecha
                mapper.writeValue(resp.getWriter(), service.obtenerCitasPorFecha(fecha));
            } else {
                // Sin filtros (todas las citas)
                mapper.writeValue(resp.getWriter(), service.obtenerMisCitas());
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            CitaDTO citaDTO = mapper.readValue(req.getInputStream(), CitaDTO.class);
            CitaDTO creada = service.agendarCita(citaDTO);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            mapper.writeValue(resp.getWriter(), creada);
            
        } catch (IllegalStateException e) {
            // Manejo de reglas de negocio (Doble reserva o mascota con cita)
            if ("DOBLE_RESERVA".equals(e.getMessage())) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT); // 409
                resp.getWriter().write("{\"error\":\"El veterinario ya tiene una cita agendada en esa fecha y hora.\"}");
            } else if ("MASCOTA_CON_CITA".equals(e.getMessage())) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT); // 409
                resp.getWriter().write("{\"error\":\"Esta mascota ya tiene una cita activa. Por favor, cancélala antes de agendar una nueva.\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Error al procesar la solicitud\"}");
            }
        } catch (Exception e) {
            // Error interno del servidor
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Error interno del servidor: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            CitaDTO citaDTO = mapper.readValue(req.getInputStream(), CitaDTO.class);
            CitaDTO actualizada = service.reprogramarCita(citaDTO);

            resp.setStatus(HttpServletResponse.SC_OK);
            mapper.writeValue(resp.getWriter(), actualizada);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Error al reprogramar la cita\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idParam = req.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            try {
                Long id = Long.parseLong(idParam);
                service.cancelarCita(id);
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } catch (Exception e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}