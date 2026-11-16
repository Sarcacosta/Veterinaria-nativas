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

        mapper.writeValue(resp.getWriter(), service.listarTodosDTO());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        Mascota mascota = mapper.readValue(
                req.getInputStream(),
                Mascota.class
        );

        HttpSession session = req.getSession(false);
        UsuarioDTO usuario = (UsuarioDTO) session.getAttribute("usuario");

        service.crear(mascota, usuario.getId());

        resp.setStatus(HttpServletResponse.SC_CREATED);
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
