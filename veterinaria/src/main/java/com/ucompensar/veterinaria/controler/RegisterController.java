package com.ucompensar.veterinaria.controler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucompensar.veterinaria.model.entity.Usuario;
import com.ucompensar.veterinaria.model.entity.Propietario;
import com.ucompensar.veterinaria.model.entity.Rol;
import com.ucompensar.veterinaria.model.entity.UsuarioRol;
import com.ucompensar.veterinaria.util.JPAUtil;
import com.ucompensar.veterinaria.util.PasswordUtil;
import jakarta.persistence.EntityManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@WebServlet("/api/register")
public class RegisterController extends HttpServlet {
    private final ObjectMapper mapper = new ObjectMapper();

    // El DTO acepta únicamente los datos que el usuario puede introducir.
    public static class RegisterRequest {
        public String nombre;
        public String email;
        public String username;
        public String password;
        public String rol;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        final RegisterRequest data;
        try {
            data = mapper.readValue(req.getInputStream(), RegisterRequest.class);
        } catch (IOException error) {
            resp.setStatus(400);
            mapper.writeValue(resp.getWriter(), Map.of("error", "JSON inválido"));
            return;
        }
        if (data == null || !valid(data)) {
            resp.setStatus(400);
            mapper.writeValue(resp.getWriter(), Map.of("error", "Datos de registro inválidos"));
            return;
        }
        String rolSolicitado = data.rol == null ? "Propietario" : data.rol;
        if (!"Propietario".equals(rolSolicitado) && !"Veterinario".equals(rolSolicitado)) {
            resp.setStatus(400);
            mapper.writeValue(resp.getWriter(), Map.of("error", "Rol de registro inválido"));
            return;
        }
        boolean propietarioActivo = "Propietario".equals(rolSolicitado);
        String username = data.username.trim();
        // BCrypt se ejecuta en el servidor. Android envía password, no passwordHash.
        String hash = PasswordUtil.cifrar(data.password);
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            long count = em.createQuery(
                    "SELECT COUNT(u) FROM Usuario u WHERE u.username = :username", Long.class)
                    .setParameter("username", username).getSingleResult();
            if (count > 0) {
                em.getTransaction().rollback();
                resp.setStatus(409);
                mapper.writeValue(resp.getWriter(), Map.of("error", "Usuario existente"));
                return;
            }
            Usuario user = new Usuario();
            user.setNombre(data.nombre.trim());
            user.setEmail(data.email.trim());
            user.setUsername(username);
            user.setPasswordHash(hash);
            user.setEstado(propietarioActivo);
            Rol rol = em.createQuery("SELECT r FROM Rol r WHERE r.nombre = :nombre", Rol.class)
                    .setParameter("nombre", rolSolicitado).setMaxResults(1).getSingleResult();
            if (propietarioActivo) {
                Propietario propietario = new Propietario();
                propietario.setNombres(data.nombre.trim());
                propietario.setApellidos("");
                propietario.setEmail(data.email.trim());
                em.persist(propietario);
                user.setPropietario(propietario);
            }
            em.persist(user);

            UsuarioRol permiso = new UsuarioRol();
            permiso.setRol(rol);
            permiso.setUsuario(user);
            em.persist(permiso);
            em.getTransaction().commit();
            resp.setStatus(201);
            mapper.writeValue(resp.getWriter(), Map.of("mensaje", propietarioActivo ? "Cuenta creada" : "Cuenta pendiente de activación"));
        } catch (RuntimeException error) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            // La restricción UNIQUE de MySQL resuelve registros concurrentes.
            boolean duplicate = false;
            for (Throwable cause = error; cause != null; cause = cause.getCause()) {
                if (cause instanceof java.sql.SQLException
                        && ((java.sql.SQLException) cause).getErrorCode() == 1062) {
                    duplicate = true;
                }
            }
            resp.setStatus(duplicate ? 409 : 500);
            if (!duplicate) getServletContext().log("Error en registro", error);
            mapper.writeValue(resp.getWriter(), Map.of("error",
                    duplicate ? "Usuario existente" : "No se pudo crear la cuenta"));
        } finally {
            em.close();
        }
    }

    private static boolean valid(RegisterRequest data) {
        return data.nombre != null && !data.nombre.trim().isEmpty()
                && data.nombre.trim().length() <= 100
                && data.email != null && data.email.trim().length() <= 254
                && data.email.trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
                && data.username != null && data.username.trim().matches("[A-Za-z0-9_.-]{3,40}")
                && data.password != null && data.password.length() >= 8
                && data.password.getBytes(StandardCharsets.UTF_8).length <= 72;
    }
}
