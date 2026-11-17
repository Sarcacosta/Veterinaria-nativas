package com.huella.acceso;

import android.os.Bundle;

public final class AgendaActivity extends CitasActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (!ready("Agenda")) return;
        label(ownerMode() ? "Agenda consultas para tus mascotas y revisa tus citas."
                : "Consulta las citas de la clínica o revisa los horarios de un veterinario.");
        action(ownerMode() ? "Mis citas" : "Ver citas de la clínica", () -> show(MisCitasActivity.class));
        action("Veterinarios y horarios", () -> show(VeterinariosActivity.class));
        if (canManage() || ownerMode()) action("Solicitar cita", () -> show(SolicitarCitaActivity.class));
    }
}
