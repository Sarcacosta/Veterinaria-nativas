/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucompensar.veterinaria.controler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ucompensar.veterinaria.DTO.UsuarioDTO;
import com.ucompensar.veterinaria.model.entity.Mascota;
import com.ucompensar.veterinaria.service.MascotaService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/api/mascotas")
public class MascotaController extends HttpServlet {

    private final MascotaService service = new MascotaService();
    private final ObjectMapper mapper;
    
    public MascotaController() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        UsuarioDTO usuario = usuario(req);
        mapper.writeValue(resp.getWriter(), service.listarDTO(esPropietario(usuario) ? usuario.getId() : null));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        UsuarioDTO usuario = usuario(req);
        final Mascota mascota;
        try { mascota = mapper.readValue(req.getInputStream(), Mascota.class); }
        catch (IOException e) { resp.sendError(400); return; }

        if (mascota == null || mascota.getIdMascota() != null || mascota.getHistoriales() != null
                || !textoValido(mascota.getNombre()) || !textoValido(mascota.getEspecie())
                || demasiadoLargo(mascota.getRaza()) || demasiadoLargo(mascota.getSexo())
                || demasiadoLargo(mascota.getColor()) || demasiadoLargo(mascota.getEstado())
                || (mascota.getFechaNacimiento() != null
                    && mascota.getFechaNacimiento().isAfter(java.time.LocalDate.now()))
                || (mascota.getPesoActual() != null
                    && (!Double.isFinite(mascota.getPesoActual()) || mascota.getPesoActual() <= 0))) {
            resp.sendError(400);
            return;
        }
        mascota.setNombre(mascota.getNombre().trim());
        mascota.setEspecie(mascota.getEspecie().trim());
        if (esPropietario(usuario)) mascota.setEstado("Activo");
        try {
            service.crear(mascota, usuario.getId(), esPropietario(usuario));
            resp.setStatus(HttpServletResponse.SC_CREATED);
        } catch (SecurityException e) {
            resp.sendError(403);
        } catch (IllegalArgumentException e) {
            resp.sendError(400);
        }
    }

    private static boolean demasiadoLargo(String texto) {
        return texto != null && texto.length() > 255;
    }

    private static boolean textoValido(String texto) {
        return texto != null && !texto.isBlank() && !demasiadoLargo(texto);
    }

    private static UsuarioDTO usuario(HttpServletRequest req) {
        return (UsuarioDTO) req.getSession(false).getAttribute("usuario");
    }

    private static boolean esPropietario(UsuarioDTO usuario) {
        var roles = usuario.getRoles();
        return roles.contains("Propietario") && !roles.contains("Administrador")
                && !roles.contains("Recepcion") && !roles.contains("Recepcionista")
                && !roles.contains("Veterinario");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
        throws IOException {

        try {

            Mascota mascota = mapper.readValue(
                    req.getInputStream(),
                    Mascota.class
            );

            if (mascota.getPropietario() != null) {
                System.out.println(
                        "Propietario: " +
                        mascota.getPropietario().getIdPropietario()
                );
            } else {
                System.out.println("Propietario: NULL");
            }

            HttpSession session = req.getSession(false);
            UsuarioDTO usuario =
                    (UsuarioDTO) session.getAttribute("usuario");

            service.actualizar(
                    mascota,
                    usuario.getId()
            );

            resp.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception e) {

            e.printStackTrace();

            resp.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            mapper.writeValue(
                    resp.getWriter(),
                    "Error al actualizar mascota: " + e.getMessage()
            );
        }
    }

    @Override
    protected void doDelete(
            HttpServletRequest req,
            HttpServletResponse resp) throws IOException {

        try {

            String id = req.getParameter("id");
            Long idMascota = Long.parseLong(id);

            HttpSession session = req.getSession(false);
            UsuarioDTO usuario =
                    (UsuarioDTO) session.getAttribute("usuario");

            service.eliminar(
                    idMascota,
                    usuario.getId()
            );

            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);

        } catch (Exception e) {

            e.printStackTrace();

            resp.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            mapper.writeValue(
                    resp.getWriter(),
                    "Error al eliminar mascota: " + e.getMessage()
            );
        }
    }
}
