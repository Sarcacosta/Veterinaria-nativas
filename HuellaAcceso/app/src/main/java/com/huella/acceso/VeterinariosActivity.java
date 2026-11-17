package com.huella.acceso;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import org.json.JSONArray;

public final class VeterinariosActivity extends CitasActivity {
    private LinearLayout list;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (!ready("Veterinarios")) return;
        action("Actualizar", this::load);
        list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        add(list);
        load();
    }

    private void load() { request(() -> app().api.vets(ownerMode()), this::showVets, () -> {}); }

    private void showVets(JSONArray vets) {
        list.removeAllViews();
        if (vets.length() == 0) { message("No hay veterinarios registrados."); return; }
        LinearLayout container = content;
        content = list;
        for (int i = 0; i < vets.length(); i++) {
            var vet = vets.optJSONObject(i);
            if (vet == null || vet.optLong("id") <= 0) continue;
            long id = vet.optLong("id");
            String name = (vet.optString("nombres") + " " + vet.optString("apellidos")).trim();
            label(name + " · " + vet.optString("especialidad"));
            if (!ownerMode()) action("Ver sus citas", () -> startActivity(new Intent(this, MisCitasActivity.class)
                    .putExtra("veterinarioId", id)));
            if (canManage() || ownerMode()) action("Consultar disponibilidad", () ->
                    startActivity(new Intent(this, SolicitarCitaActivity.class).putExtra("veterinarioId", id)));
        }
        content = container;
    }
}
