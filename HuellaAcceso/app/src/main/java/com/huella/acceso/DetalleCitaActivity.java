package com.huella.acceso;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public final class DetalleCitaActivity extends CitasActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (!ready("Detalle de cita")) return;
        var cita = incoming();
        if (cita == null || cita.optLong("id") <= 0) { message("La cita no está disponible."); return; }
        label(cita.optString("fecha") + " · " + cita.optString("hora"));
        label("Motivo: " + cita.optString("motivo"));
        label("Estado: " + cita.optString("estado"));
        label("Mascota #" + cita.optLong("mascotaId") + " · Veterinario #" + cita.optLong("veterinarioId"));
        if ((!canManage() && !ownerMode()) || MisCitasActivity.pasadaOCancelada(cita)) return;
        action("Reprogramar", () -> startActivityForResult(new Intent(this, SolicitarCitaActivity.class)
                .putExtra("cita", cita.toString()), 1));
        action("Cancelar cita", () -> new AlertDialog.Builder(this).setMessage("¿Cancelar esta cita?")
                .setNegativeButton("Volver", null).setPositiveButton("Cancelar cita", (dialog, which) ->
                    request(() -> { app().api.cancelAppointment(cita.optLong("id"), ownerMode()); return true; },
                        ok -> { Toast.makeText(this, "Cita cancelada.", Toast.LENGTH_SHORT).show(); finish(); },
                        () -> {})).show());
    }
    @Override protected void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);
        if (request == 1 && result == RESULT_OK) finish();
    }
}
