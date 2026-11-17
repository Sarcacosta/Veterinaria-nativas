package com.huella.acceso;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;

public final class MascotasActivity extends BaseActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_mascotas);
        if (app().api == null || app().user == null) { sessionEnded(); return; }
        findViewById(R.id.back).setOnClickListener(v -> finish());
        findViewById(R.id.reload).setOnClickListener(v -> load());
        findViewById(R.id.add_pet).setOnClickListener(v ->
                startActivityForResult(new Intent(this, RegistrarMascotaActivity.class), 1));
        boolean canCreate = hasRole("Propietario") || hasRole("Administrador")
                || hasRole("Recepcion") || hasRole("Recepcionista");
        findViewById(R.id.add_pet).setVisibility(canCreate ? View.VISIBLE : View.GONE);
        load();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) load();
    }

    private boolean hasRole(String role) {
        JSONArray roles = app().user.optJSONArray("roles");
        if (roles != null) for (int i = 0; i < roles.length(); i++) {
            if (role.equals(roles.optString(i))) return true;
        }
        return false;
    }

    private void load() {
        request(app().api::pets, this::showPets, () -> {});
    }

    private void showPets(JSONArray pets) {
        LinearLayout list = findViewById(R.id.pet_list);
        list.removeAllViews();
        if (pets.length() == 0) { message("Aún no hay mascotas registradas."); return; }
        for (int i = 0; i < pets.length(); i++) {
            JSONObject pet = pets.optJSONObject(i);
            if (pet == null) continue;
            View card = getLayoutInflater().inflate(R.layout.pet_card, list, false);
            ((TextView) card.findViewById(R.id.pet_name)).setText(pet.optString("nombre"));
            String species = pet.optString("especie");
            if (!pet.isNull("raza") && !pet.optString("raza").trim().isEmpty()) species += " · " + pet.optString("raza");
            String detail = species;
            if (!pet.isNull("propietario")) detail += "\nPropietario: " + pet.optString("propietario");
            if (!pet.isNull("fechaNacimiento")) detail += "\nFecha de nacimiento: " + pet.optString("fechaNacimiento");
            if (!pet.isNull("pesoActual")) detail += "\nPeso: " + pet.optString("pesoActual") + " kg";
            ((TextView) card.findViewById(R.id.pet_details)).setText(detail);
            if (hasRole("Administrador") || hasRole("Recepcion") || hasRole("Recepcionista")
                    || (hasRole("Propietario") && !hasRole("Veterinario"))) {
                card.setContentDescription("Editar ficha de " + pet.optString("nombre"));
                card.setFocusable(true);
                card.setOnClickListener(v -> startActivityForResult(new Intent(this, RegistrarMascotaActivity.class)
                        .putExtra("mascota", pet.toString()), 1));
            }
            list.addView(card);
        }
    }
}
