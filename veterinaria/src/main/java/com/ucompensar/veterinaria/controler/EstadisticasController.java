/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ucompensar.veterinaria.controler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucompensar.veterinaria.dao.MascotaDAO;
import com.ucompensar.veterinaria.dao.PropietarioDAO;
import com.ucompensar.veterinaria.dao.VeterinarioDAO;
import com.ucompensar.veterinaria.util.JPAUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/estadisticas")
public class EstadisticasController extends HttpServlet {

    private final MascotaDAO mascotaDAO = new MascotaDAO(JPAUtil.getEntityManager());
    private final PropietarioDAO propietarioDAO = new PropietarioDAO(JPAUtil.getEntityManager());
    private final VeterinarioDAO veterinarioDAO = new VeterinarioDAO(JPAUtil.getEntityManager());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Consultar la cantidad de registros de cada entidad
        long mascotasCount = mascotaDAO.count();
        long propietariosCount = propietarioDAO.count();
        long veterinariosCount = veterinarioDAO.count();

        // Crear un objeto con las estadísticas
        Map<String, Long> estadisticas = new HashMap<>();
        estadisticas.put("mascotas", mascotasCount);
        estadisticas.put("propietarios", propietariosCount);
        estadisticas.put("veterinarios", veterinariosCount);

        // Enviar las estadísticas como respuesta JSON
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        new ObjectMapper().writeValue(resp.getWriter(), estadisticas);
    }
}

