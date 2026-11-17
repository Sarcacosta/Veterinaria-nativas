package com.ucompensar.veterinaria.controler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ucompensar.veterinaria.DTO.UsuarioDTO;
import com.ucompensar.veterinaria.model.entity.*;
import com.ucompensar.veterinaria.util.JPAUtil;
import jakarta.persistence.LockModeType;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.*;

@WebServlet("/api/mascotas/editar")
public class EditarMascotaController extends HttpServlet {
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public record Ficha(Long id, String nombre, String especie, String raza, String sexo, String color,
                        LocalDate fechaNacimiento, Double pesoActual) {}

    @Override protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Ficha datos;
        try { datos = mapper.readValue(req.getInputStream(), Ficha.class); }
        catch (IOException error) { resp.sendError(400); return; }
        if (datos == null || datos.id() == null || !requerido(datos.nombre()) || !requerido(datos.especie())
                || !longitud(datos.raza()) || !longitud(datos.sexo()) || !longitud(datos.color())
                || (datos.fechaNacimiento() != null && datos.fechaNacimiento().isAfter(LocalDate.now()))
                || (datos.pesoActual() != null && (!Double.isFinite(datos.pesoActual()) || datos.pesoActual() <= 0))) {
            resp.sendError(400);
            return;
        }
        UsuarioDTO sesion = (UsuarioDTO) req.getSession(false).getAttribute("usuario");
        var roles = sesion.getRoles();
        boolean gestion = roles.contains("Administrador") || roles.contains("Recepcion") || roles.contains("Recepcionista");
        try (var em = JPAUtil.getEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();
            try {
                Usuario usuario = em.find(Usuario.class, sesion.getId());
                Mascota mascota = em.find(Mascota.class, datos.id(), LockModeType.PESSIMISTIC_WRITE);
                if (mascota == null) { resp.sendError(404); return; }
                if (usuario == null || !Boolean.TRUE.equals(usuario.getEstado())
                        || (!gestion && (usuario.getPropietario() == null || mascota.getPropietario() == null
                        || !usuario.getPropietario().getIdPropietario().equals(mascota.getPropietario().getIdPropietario())))) {
                    resp.sendError(403);
                    return;
                }
                mascota.setNombre(datos.nombre().trim());
                mascota.setEspecie(datos.especie().trim());
                mascota.setRaza(datos.raza());
                mascota.setSexo(datos.sexo());
                mascota.setColor(datos.color());
                mascota.setFechaNacimiento(datos.fechaNacimiento());
                mascota.setPesoActual(datos.pesoActual());
                Auditoria auditoria = new Auditoria();
                auditoria.setUsuario(usuario);
                auditoria.setEntidad("Mascota");
                auditoria.setAccion("ACTUALIZAR");
                auditoria.setIdRegistro(mascota.getIdMascota());
                auditoria.setFechaHora(LocalDateTime.now());
                auditoria.setDescripcion("Actualización de ficha");
                em.persist(auditoria);
                tx.commit();
                resp.setStatus(204);
            } finally {
                if (tx.isActive()) tx.rollback();
            }
        }
    }

    private static boolean longitud(String s) { return s == null || s.length() <= 255; }
    private static boolean requerido(String s) { return s != null && !s.isBlank() && longitud(s); }
}
