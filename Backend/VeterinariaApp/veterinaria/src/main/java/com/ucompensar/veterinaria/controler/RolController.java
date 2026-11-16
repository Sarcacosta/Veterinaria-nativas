/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucompensar.veterinaria.controler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucompensar.veterinaria.DTO.RolDTO;
import com.ucompensar.veterinaria.model.entity.Rol;
import com.ucompensar.veterinaria.service.RolService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/roles")
public class RolController extends HttpServlet {

    private RolService rolService;
    private ObjectMapper mapper;

    @Override
    public void init() {
        rolService = new RolService();
        mapper = new ObjectMapper();
    }

    // =========================
    // GET → LISTAR / BUSCAR
    // =========================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String idParam = request.getParameter("id");

        // GET /api/roles
        if (idParam == null) {
            List<RolDTO> roles = rolService.listarTodosDTO();
            mapper.writeValue(response.getWriter(), roles);
            return;
        }

        // GET /api/roles?id=1
        Rol rol = rolService.buscarPorId(Long.parseLong(idParam));

        if (rol == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        mapper.writeValue(response.getWriter(), rol);
    }

    // =========================
    // POST → CREAR
    // =========================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Rol rol = mapper.readValue(request.getInputStream(), Rol.class);
        rolService.crear(rol);

        response.setStatus(HttpServletResponse.SC_CREATED);
    }

    // =========================
    // PUT → ACTUALIZAR
    // =========================
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Rol rol = mapper.readValue(request.getInputStream(), Rol.class);

        if (rol.getIdRol() == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        rolService.actualizar(rol);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    // =========================
    // DELETE → ELIMINAR
    // =========================
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");

        if (idParam == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        rolService.eliminar(Long.parseLong(idParam));
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
