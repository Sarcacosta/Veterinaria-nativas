package com.huella.acceso;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.*;
import java.time.LocalDate;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

public final class SolicitarCitaActivity extends CitasActivity {
    private JSONObject existing;
    private Spinner pets, vets, hours;
    private EditText reason;
    private Button dateButton, save;
    private LocalDate date;
    private long checkedVet;
    private LocalDate checkedDate;
    private final ArrayList<Long> petIds = new ArrayList<>(), vetIds = new ArrayList<>();

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        existing = incoming();
        if (!ready(existing == null ? "Solicitar cita" : "Reprogramar cita")) return;
        if (!canManage() && !ownerMode()) { message("Tu cuenta no puede agendar citas."); return; }
        date = LocalDate.now();
        if (existing == null) {
            pets = selector("Mascota");
            vets = selector("Veterinario");
            action("Cargar mascotas y veterinarios", this::loadOptions);
        } else {
            label("Mascota #" + existing.optLong("mascotaId") + " · Veterinario #" + existing.optLong("veterinarioId"));
        }
        dateButton = action("Fecha · " + date, this::pickDate);
        hours = selector("Horario disponible");
        action("Consultar horarios", this::loadHours);
        reason = new EditText(this);
        reason.setHint("Motivo de consulta");
        if (existing != null) reason.setText(existing.optString("motivo"));
        add(reason);
        save = action("Guardar cita", this::save);
        if (existing == null) loadOptions();
    }

    private Spinner selector(String title) {
        label(title);
        Spinner spinner = new Spinner(this);
        spinner.setMinimumHeight((int)(48 * getResources().getDisplayMetrics().density));
        add(spinner);
        return spinner;
    }

    private void options(Spinner spinner, ArrayList<String> values) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void loadOptions() {
        VetApi api = app().api;
        request(() -> new JSONArray[]{api.pets(), api.vets(ownerMode())}, result -> {
            populate(pets, result[0], petIds, false);
            populate(vets, result[1], vetIds, true);
            long selectedVet = getIntent().getLongExtra("veterinarioId", 0);
            int index = vetIds.indexOf(selectedVet);
            if (index >= 0) vets.setSelection(index + 1);
            if (petIds.isEmpty() || vetIds.isEmpty()) message("Se requiere una mascota y un veterinario.");
        }, () -> {});
    }

    private void populate(Spinner spinner, JSONArray rows, ArrayList<Long> ids, boolean vet) {
        ids.clear();
        var names = new ArrayList<String>();
        names.add(vet ? "Selecciona un veterinario" : "Selecciona una mascota");
        for (int i = 0; i < rows.length(); i++) {
            var row = rows.optJSONObject(i);
            if (row == null || row.optLong("id") <= 0) continue;
            ids.add(row.optLong("id"));
            names.add(vet ? (row.optString("nombres") + " " + row.optString("apellidos")).trim()
                    : row.optString("nombre"));
        }
        options(spinner, names);
    }

    private long selected(Spinner spinner, ArrayList<Long> ids) {
        int index = spinner.getSelectedItemPosition() - 1;
        return index >= 0 && index < ids.size() ? ids.get(index) : 0;
    }

    private long vetId() {
        return existing == null ? selected(vets, vetIds) : existing.optLong("veterinarioId");
    }

    private void pickDate() {
        DatePickerDialog dialog = new DatePickerDialog(this, (picker, year, month, day) -> {
            date = LocalDate.of(year, month + 1, day);
            dateButton.setText("Fecha · " + date);
            checkedDate = null;
            hours.setAdapter(null);
        }, date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
        dialog.getDatePicker().setMinDate(System.currentTimeMillis());
        dialog.show();
    }

    private void loadHours() {
        long vet = vetId();
        if (vet <= 0) { message("Selecciona un veterinario."); return; }
        LocalDate requestedDate = date;
        checkedDate = null;
        hours.setAdapter(null);
        request(() -> app().api.slots(vet, requestedDate.toString(), ownerMode()), result -> {
            var values = new ArrayList<String>();
            for (int i = 0; i < result.length(); i++) values.add(result.optString(i));
            options(hours, values);
            checkedDate = requestedDate;
            checkedVet = vet;
            if (values.isEmpty()) message("No hay horarios para esta fecha.");
        }, () -> {});
    }

    private void save() {
        long pet = existing == null ? selected(pets, petIds) : existing.optLong("mascotaId");
        long vet = vetId();
        if (pet <= 0 || vet <= 0) { message("Elige mascota y veterinario."); return; }
        if (!date.equals(checkedDate) || checkedVet != vet || hours.getSelectedItem() == null) {
            message("Consulta los horarios del veterinario y elige uno."); return;
        }
        String hour = hours.getSelectedItem().toString();
        String motive = reason.getText().toString().trim();
        if (motive.length() > 255) { reason.setError("Máximo 255 caracteres."); return; }
        String day = date.toString();
        request(() -> {
            var data = new JSONObject().put("mascotaId", pet).put("veterinarioId", vet)
                    .put("fecha", day).put("hora", hour);
            if (!motive.isEmpty()) data.put("motivo", motive);
            if (existing != null) data.put("id", existing.optLong("id"));
            app().api.saveAppointment(data, existing != null, ownerMode());
            return true;
        }, ok -> {
            Toast.makeText(this, "Cita guardada.", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        }, () -> {});
    }
}
