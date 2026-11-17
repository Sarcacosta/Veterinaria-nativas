package com.huella.acceso;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import java.time.LocalDate;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

public final class RegistrarMascotaActivity extends BaseActivity {
    private final ArrayList<Long> ownerIds = new ArrayList<>();
    private boolean chooseOwner;
    private JSONObject existing;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_registrar_mascota);
        if (app().api == null || app().user == null) { sessionEnded(); return; }
        findViewById(R.id.back).setOnClickListener(v -> { if (!busy) finish(); });
        findViewById(R.id.save).setOnClickListener(v -> save());
        findViewById(R.id.pick_date).setOnClickListener(v -> pickDate());
        JSONArray roles = app().user.optJSONArray("roles");
        if (roles != null) for (int i = 0; i < roles.length(); i++) {
            String role = roles.optString(i);
            if ("Administrador".equals(role) || "Recepcion".equals(role) || "Recepcionista".equals(role)) {
                chooseOwner = true;
            }
        }
        String payload = getIntent().getStringExtra("mascota");
        if (payload != null) {
            try { existing = new JSONObject(payload); }
            catch (org.json.JSONException error) { finish(); return; }
            fill(R.id.name, "nombre");
            fill(R.id.species, "especie");
            fill(R.id.breed, "raza");
            fill(R.id.sex, "sexo");
            fill(R.id.color, "color");
            fill(R.id.birth, "fechaNacimiento");
            fill(R.id.weight, "pesoActual");
            ((android.widget.TextView) findViewById(R.id.pet_form_title)).setText("Editar mascota");
            chooseOwner = false;
        }
        findViewById(R.id.owner_panel).setVisibility(chooseOwner ? View.VISIBLE : View.GONE);
        findViewById(R.id.reload).setOnClickListener(v -> loadOwners());
        if (chooseOwner) loadOwners();
    }

    private void fill(int field, String key) {
        ((EditText) findViewById(field)).setText(existing.isNull(key) ? "" : existing.optString(key, ""));
    }

    private void loadOwners() {
        request(app().api::owners, list -> {
            ownerIds.clear();
            ArrayList<String> names = new ArrayList<>();
            names.add("Selecciona un propietario");
            for (int i = 0; i < list.length(); i++) {
                JSONObject owner = list.optJSONObject(i);
                if (owner != null && owner.optLong("id") > 0) {
                    ownerIds.add(owner.optLong("id"));
                    names.add((owner.optString("nombres") + " " + owner.optString("apellidos")).trim());
                }
            }
            Spinner spinner = findViewById(R.id.owner);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            if (ownerIds.isEmpty()) message("Registra primero un propietario.");
        }, () -> {});
    }

    private void pickDate() {
        LocalDate date = LocalDate.now();
        if (!value(R.id.birth).isEmpty()) date = LocalDate.parse(value(R.id.birth));
        DatePickerDialog dialog = new DatePickerDialog(this, (picker, year, month, day) ->
                ((EditText) findViewById(R.id.birth)).setText(LocalDate.of(year, month + 1, day).toString()),
                date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    private String value(int id) {
        return ((EditText) findViewById(id)).getText().toString().trim();
    }

    private void save() {
        if (busy) return;
        String name = value(R.id.name), species = value(R.id.species), birth = value(R.id.birth);
        if (name.isEmpty()) { invalid(R.id.name, "Escribe el nombre de la mascota."); return; }
        if (species.isEmpty()) { invalid(R.id.species, "Escribe la especie."); return; }
        Double weight = null;
        try {
            if (!value(R.id.weight).isEmpty()) {
                weight = Double.valueOf(value(R.id.weight).replace(',', '.'));
                if (!Double.isFinite(weight) || weight <= 0) throw new NumberFormatException();
            }
        } catch (NumberFormatException e) { invalid(R.id.weight, "Escribe un peso mayor que cero."); return; }
        Long owner = null;
        if (chooseOwner) {
            int selection = ((Spinner) findViewById(R.id.owner)).getSelectedItemPosition() - 1;
            if (selection < 0 || selection >= ownerIds.size()) { message("Selecciona un propietario."); return; }
            owner = ownerIds.get(selection);
        }
        JSONObject data = new JSONObject();
        try {
            data.put("nombre", name).put("especie", species)
                    .put("raza", value(R.id.breed)).put("color", value(R.id.color))
                    .put("sexo", value(R.id.sex));
            if (existing != null) data.put("id", existing.getLong("id"));
            else data.put("estado", "Activo");
            if (!birth.isEmpty()) data.put("fechaNacimiento", birth);
            if (weight != null) data.put("pesoActual", weight);
            if (owner != null) data.put("propietario", new JSONObject().put("idPropietario", owner));
        } catch (org.json.JSONException e) { message("No se pudo preparar el formulario."); return; }
        VetApi api = app().api;
        request(() -> {
            if (existing == null) api.createPet(data);
            else api.editPet(data);
            return true;
        }, result -> {
            Toast.makeText(this, existing == null ? "Mascota registrada." : "Ficha actualizada.", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        }, () -> {});
    }

    @Override protected String failureMessage(Exception error) {
        if (error instanceof java.net.SocketTimeoutException) {
            return "No llegó la confirmación. Revisa la lista de mascotas antes de volver a guardar.";
        }
        return super.failureMessage(error);
    }
}
