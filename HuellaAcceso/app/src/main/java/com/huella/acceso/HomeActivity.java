package com.huella.acceso;

import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;

public final class HomeActivity extends BaseActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_home);
        if (app().user == null || app().api == null) { sessionEnded(); return; }
        ((TextView) findViewById(R.id.welcome)).setText(
                "Hola, " + app().user.optString("username", "equipo"));
        findViewById(R.id.refresh).setOnClickListener(view -> loadStats());
        findViewById(R.id.logout).setOnClickListener(view -> logout());
        findViewById(R.id.pets).setOnClickListener(view -> startActivity(new Intent(this, MascotasActivity.class)));
        findViewById(R.id.profile).setOnClickListener(view -> startActivity(new Intent(this, PerfilActivity.class)));
        var locale = new java.util.Locale("es", "CO");
        var dateFormat = java.time.format.DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", locale);
        String today = java.time.LocalDate.now().format(dateFormat);
        ((TextView) findViewById(R.id.today)).setText(Character.toUpperCase(today.charAt(0)) + today.substring(1));
        boolean gestion = hasRole("Administrador") || hasRole("Recepcion") || hasRole("Recepcionista");
        boolean veterinarian = hasRole("Veterinario") && !gestion;
        boolean owner = hasRole("Propietario") && !veterinarian;
        ((TextView) findViewById(R.id.panel_label)).setText(veterinarian ? "PANEL VETERINARIO"
                : owner && !gestion ? "PANEL DE PROPIETARIO" : "PANEL DE LA CLÍNICA");
        link(R.id.vets, VeterinariosActivity.class, gestion || owner);
        link(R.id.my_appointments, MisCitasActivity.class, gestion || owner || veterinarian);
        link(R.id.book_appointment, SolicitarCitaActivity.class, gestion || owner);
        link(R.id.agenda, AgendaActivity.class, gestion);
        link(R.id.attention_hours, HorariosActivity.class, gestion || veterinarian);
        link(R.id.owners, PropietariosActivity.class, gestion || veterinarian);
        loadStats();
    }

    private boolean hasRole(String role) {
        var roles = app().user.optJSONArray("roles");
        if (roles != null) for (int i = 0; i < roles.length(); i++) {
            if (role.equals(roles.optString(i))) return true;
        }
        return false;
    }

    private void link(int id, Class<?> activity, boolean visible) {
        var button = findViewById(id);
        button.setVisibility(visible ? android.view.View.VISIBLE : android.view.View.GONE);
        button.setOnClickListener(view -> startActivity(new Intent(this, activity)));
    }

    private void loadStats() {
        VetApi client = app().api;
        request(client::stats, stats -> {
            ((TextView) findViewById(R.id.pet_count)).setText(String.valueOf(stats.optLong("mascotas")));
            ((TextView) findViewById(R.id.owner_count)).setText(String.valueOf(stats.optLong("propietarios")));
        }, () -> {});
    }

    private void logout() {
        VetApi client = app().api;
        request(() -> { client.logout(); return true; }, ok -> sessionEnded(), () -> {});
    }


}
