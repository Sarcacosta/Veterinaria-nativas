/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucompensar.veterinaria.controler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucompensar.veterinaria.DTO.UsuarioCompletoDTO;
import com.ucompensar.veterinaria.model.entity.Usuario;
import com.ucompensar.veterinaria.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/usuarios")
public class UsuarioController extends HttpServlet {

    private UsuarioService usuarioService;
    private ObjectMapper mapper;

    @Override
    public void init() {
        usuarioService = new UsuarioService();
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

        // GET /api/usuarios
        if (idParam == null) {
            List<UsuarioCompletoDTO> usuarios = usuarioService.listarTodosDTO();
            mapper.writeValue(response.getWriter(), usuarios);
            return;
        }

        // GET /api/usuarios?id=1
        Long id = Long.parseLong(idParam);
        Usuario usuario = usuarioService.buscarPorId(id);

        if (usuario == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        mapper.writeValue(response.getWriter(), usuario);
    }

    // =========================
    // POST → CREAR
    // =========================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Usuario usuario = mapper.readValue(request.getInputStream(), Usuario.class);
        usuarioService.crear(usuario);

        response.setStatus(HttpServletResponse.SC_CREATED);
    }

    // =========================
    // PUT → ACTUALIZAR
    // =========================
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Usuario usuario = mapper.readValue(request.getInputStream(), Usuario.class);

        if (usuario.getIdUsuario() == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        usuarioService.actualizar(usuario);
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

        Long id = Long.parseLong(idParam);
        usuarioService.eliminar(id);

        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}