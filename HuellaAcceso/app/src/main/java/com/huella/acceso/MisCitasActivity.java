package com.huella.acceso;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import org.json.JSONArray;

public final class MisCitasActivity extends CitasActivity {
    private LinearLayout list;
    private boolean history;

    static boolean pasadaOCancelada(org.json.JSONObject cita) {
        if ("CANCELADA".equalsIgnoreCase(cita.optString("estado"))) return true;
        try {
            var dateTime = java.time.LocalDateTime.parse(cita.optString("fecha") + "T" + cita.optString("hora"));
            return !dateTime.isAfter(java.time.LocalDateTime.now());
        } catch (java.time.DateTimeException error) { return true; }
    }

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (!ready("Citas")) return;
        ((android.widget.TextView) findViewById(R.id.appointment_title))
                .setText(veterinarianMode() || ownerMode() ? "Mis citas" : "Citas de la clínica");
        action("Próximas citas", () -> { history = false; load(); });
        action("Historial", () -> { history = true; load(); });
        action("Actualizar", this::load);
        list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        add(list);
    }

    @Override protected void onResume() {
        super.onResume();
        if (list != null && app().api != null) load();
    }

    private void load() {
        long vet = getIntent().getLongExtra("veterinarioId", 0);
        String query = vet > 0 ? "/veterinario?veterinarioId=" + vet : "/mias";
        VetApi api = app().api;
        boolean veterinarian = veterinarianMode();
        boolean owner = ownerMode();
        request(() -> veterinarian ? api.veterinarianAppointments() : api.appointments(query, owner),
                this::showCitas, () -> {});
    }

    private void showCitas(JSONArray citas) {
        list.removeAllViews();
        ((android.widget.TextView) findViewById(R.id.appointment_title))
                .setText(history ? "Historial de citas" : "Próximas citas");
        boolean found = false;
        LinearLayout container = content;
        content = list;
        for (int i = 0; i < citas.length(); i++) {
            var cita = citas.optJSONObject(i);
            if (cita == null || pasadaOCancelada(cita) != history) continue;
            found = true;
            action(cita.optString("fecha") + " · " + cita.optString("hora") + "\n"
                    + cita.optString("motivo") + " · " + cita.optString("estado"),
                    () -> startActivity(new Intent(this, DetalleCitaActivity.class)
                            .putExtra("cita", cita.toString())));
        }
        content = container;
        if (!found) message(history ? "El historial está vacío." : "No hay próximas citas.");
    }
}
