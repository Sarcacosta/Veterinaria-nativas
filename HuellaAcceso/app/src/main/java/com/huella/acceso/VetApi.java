package com.huella.acceso;

import org.json.JSONObject;
import org.json.JSONArray;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

final class VetApi {
    private final CookieManager cookies = new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    private final String base;

    VetApi(String base) {
        URI uri = URI.create(base.trim());
        if (uri.getHost() == null || uri.getUserInfo() != null || uri.getQuery() != null
                || uri.getFragment() != null || !("http".equals(uri.getScheme()) || "https".equals(uri.getScheme()))) {
            throw new IllegalArgumentException("Escribe una URL HTTP o HTTPS válida.");
        }
        this.base = uri.toString().replaceAll("/+$", "");
    }

    JSONObject login(String username, String password) throws Exception {
        return new JSONObject(request("POST", "/login",
                new JSONObject().put("username", username).put("password", password)));
    }

    JSONObject stats() throws Exception { return new JSONObject(request("GET", "/estadisticas", null)); }
    // El servidor recibe la contraseña y genera su hash BCrypt.
    void register(JSONObject data) throws Exception { request("POST", "/register", data); }

    JSONObject profile() throws Exception { return new JSONObject(request("GET", "/perfil", null)); }
    JSONObject updateProfile(JSONObject data) throws Exception {
        return new JSONObject(request("PUT", "/perfil", data));
    }
    JSONArray pets() throws Exception { return new JSONArray(request("GET", "/mascotas", null)); }
    JSONArray owners() throws Exception { return new JSONArray(request("GET", "/propietarios", null)); }
    void editPet(JSONObject data) throws Exception { request("PUT", "/mascotas/editar", data); }
    void createPet(JSONObject data) throws Exception { request("POST", "/mascotas", data); }

    JSONArray veterinarianAppointments() throws Exception {
        return new JSONArray(request("GET", "/agenda-veterinario", null));
    }

    JSONArray appointments(String query, boolean owner) throws Exception {
        return new JSONArray(request("GET", owner ? "/mis-citas" : "/citas" + query, null));
    }
    JSONArray vets(boolean owner) throws Exception {
        return new JSONArray(request("GET", owner ? "/mis-citas/veterinarios" : "/veterinarios", null));
    }
    JSONArray slots(long vet, String date, boolean owner) throws Exception {
        String base = owner ? "/mis-citas" : "/citas";
        return new JSONArray(request("GET", base + "/disponibles?veterinarioId=" + vet + "&fecha=" + date, null));
    }
    void saveAppointment(JSONObject data, boolean edit, boolean owner) throws Exception {
        request(edit ? "PUT" : "POST", owner ? "/mis-citas" : "/citas", data);
    }
    void cancelAppointment(long id, boolean owner) throws Exception {
        request("DELETE", (owner ? "/mis-citas" : "/citas") + "?id=" + id, null);
    }

    JSONArray attentionHours(long vet, String date, JSONArray changes) throws Exception {
        String path = "/horarios-atencion?veterinarioId=" + vet + "&fecha=" + date;
        JSONObject body = changes == null ? null : new JSONObject().put("horas", changes);
        return new JSONObject(request(changes == null ? "GET" : "PUT", path, body)).getJSONArray("horas");
    }

    void logout() throws Exception {
        try { request("POST", "/logout", null); }
        finally { cookies.getCookieStore().removeAll(); }
    }

    private String request(String method, String path, JSONObject body) throws Exception {
        URI uri = URI.create(base + "/api" + path);
        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        try {
            conn.setInstanceFollowRedirects(false);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(path.equals("/register") ? 60000 : 30000);
            conn.setRequestMethod(method);
            conn.setRequestProperty("Accept", "application/json");
            for (Map.Entry<String, List<String>> header : cookies.get(uri, java.util.Collections.emptyMap()).entrySet()) {
                for (String value : header.getValue()) conn.addRequestProperty(header.getKey(), value);
            }
            if (body != null) {
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                try (var output = conn.getOutputStream()) {
                    output.write(body.toString().getBytes(StandardCharsets.UTF_8));
                }
            }
            int code = conn.getResponseCode();
            cookies.put(uri, conn.getHeaderFields());
            if (code < 200 || code >= 300) {
                if (code == 409 && path.startsWith("/horarios-atencion?")) {
                    throw new HttpFailure(code, "Reprograma las citas activas antes de quitar sus horarios.");
                }
                if (code == 409 && (path.startsWith("/citas") || path.startsWith("/mis-citas"))) {
                    throw new HttpFailure(code, "El turno ya no está disponible o la cita ya no admite esta operación.");
                }
                throw new HttpFailure(code, path.equals("/login"));
            }
            // Para guardar basta la confirmación HTTP. Un fallo al leer un cuerpo
            // opcional no debe transformar un 201 confirmado en un error de registro.
            if (path.equals("/register") || path.equals("/logout") || method.equals("DELETE")
                    || (path.equals("/mascotas") && method.equals("POST"))) return "";
            try (InputStream stream = conn.getInputStream()) {
                var buffer = new java.io.ByteArrayOutputStream();
                byte[] chunk = new byte[4096];
                int read;
                while ((read = stream.read(chunk)) != -1) buffer.write(chunk, 0, read);
                return buffer.toString("UTF-8");
            }
        } finally {
            conn.disconnect();
        }
    }

    static final class HttpFailure extends IOException {
        final int code;
        HttpFailure(int code, String message) { super(message); this.code = code; }

        HttpFailure(int code, boolean login) {
            super(code == 401 ? (login ? "Revisa tu usuario y contraseña." : "Tu sesión terminó. Ingresa de nuevo.")
                    : code == 409 ? "Ese usuario ya existe."
                    : code == 400 ? "Revisa los datos del formulario."
                    : code == 422 ? "Pide al administrador que vincule tu cuenta con tu ficha de veterinario."
                    : code == 403 ? "Tu cuenta no tiene permiso para esta operación."
                    : code == 404 ? "No se encontró esta función. Revisa la dirección del servidor y su despliegue."
                    : code >= 500 ? "La clínica no pudo procesar la solicitud. Intenta más tarde."
                    : "El servidor respondió HTTP " + code + ".");
            this.code = code;
        }
    }
}

