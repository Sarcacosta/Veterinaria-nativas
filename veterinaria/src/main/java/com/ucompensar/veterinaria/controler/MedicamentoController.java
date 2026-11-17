/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucompensar.veterinaria.controler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucompensar.veterinaria.model.entity.Medicamento;
import com.ucompensar.veterinaria.service.MedicamentoService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/medicamentos")
public class MedicamentoController extends HttpServlet {

    private final MedicamentoService service = new MedicamentoService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        mapper.writeValue(resp.getWriter(), service.listarTodos());
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Medicamento medicamento = mapper.readValue(req.getInputStream(), Medicamento.class);
        service.crear(medicamento);
        resp.setStatus(HttpServletResponse.SC_CREATED);
    }
    
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Medicamento medicamento = mapper.readValue(req.getInputStream(), Medicamento.class);
        service.actualizar(medicamento);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        String id = req.getParameter("id");
        service.eliminar(Long.parseLong(id));
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}

