/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucompensar.veterinaria.controler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucompensar.veterinaria.DTO.UsuarioDTO;
import com.ucompensar.veterinaria.model.entity.Propietario;
import com.ucompensar.veterinaria.service.PropietarioService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/api/propietarios")
public class PropietarioController extends HttpServlet {

    private final PropietarioService service = new PropietarioService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp) throws IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        mapper.writeValue(
                resp.getWriter(),
                service.listarTodosDTO()
        );
    }

    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp) throws IOException {

        Propietario propietario =
                mapper.readValue(
                        req.getInputStream(),
                        Propietario.class
                );

        HttpSession session = req.getSession(false);

        UsuarioDTO usuario =
                (UsuarioDTO) session.getAttribute("usuario");

        service.crear(
                propietario,
                usuario.getId()
        );

        resp.setStatus(
                HttpServletResponse.SC_CREATED
        );
    }

    @Override
    protected void doPut(
            HttpServletRequest req,
            HttpServletResponse resp) throws IOException {

        Propietario propietario =
                mapper.readValue(
                        req.getInputStream(),
                        Propietario.class
                );

        System.out.println(
                "===== ACTUALIZAR PROPIETARIO ====="
        );

        System.out.println(
                "ID recibido: "
                + propietario.getIdPropietario()
        );

        System.out.println(
                "Nombre: "
                + propietario.getNombres()
        );

        System.out.println(
                "Documento: "
                + propietario.getNumeroDocumento()
        );

        HttpSession session = req.getSession(false);

        UsuarioDTO usuario =
                (UsuarioDTO) session.getAttribute("usuario");

        service.actualizar(
                propietario,
                usuario.getId()
        );

        resp.setStatus(
                HttpServletResponse.SC_OK
        );
    }

    @Override
    protected void doDelete(
            HttpServletRequest req,
            HttpServletResponse resp) {

        String id = req.getParameter("id");

        Long idPropietario =
                Long.parseLong(id);

        HttpSession session = req.getSession(false);

        UsuarioDTO usuario =
                (UsuarioDTO) session.getAttribute("usuario");

        service.eliminar(
                idPropietario,
                usuario.getId()
        );

        resp.setStatus(
                HttpServletResponse.SC_NO_CONTENT
        );
    }
}