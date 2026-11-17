/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucompensar.veterinaria.controler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucompensar.veterinaria.DTO.UsuarioRolDTO;
import com.ucompensar.veterinaria.service.UsuarioRolService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/usuarios-roles")
public class UsuarioRolController extends HttpServlet {

    private UsuarioRolService usuarioRolService;
    private ObjectMapper mapper;

    @Override
    public void init() {
        usuarioRolService = new UsuarioRolService();
        mapper = new ObjectMapper();
    }

    // =========================
    // GET → LISTAR RELACIONES
    // =========================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        List<UsuarioRolDTO> relaciones = usuarioRolService.listarTodosDTO();
        mapper.writeValue(response.getWriter(), relaciones);
    }

    // =========================
    // POST → ASIGNAR ROL
    // =========================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        UsuarioRolDTO dto = mapper.readValue(request.getInputStream(), UsuarioRolDTO.class);

        if (dto.getIdUsuario() == null || dto.getIdRol() == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        usuarioRolService.asignarRol(dto.getIdUsuario(), dto.getIdRol());
        response.setStatus(HttpServletResponse.SC_CREATED);
    }

    // =========================
    // DELETE → ELIMINAR RELACIÓN
    // =========================
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");

        if (idParam == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        usuarioRolService.eliminar(Long.parseLong(idParam));
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}