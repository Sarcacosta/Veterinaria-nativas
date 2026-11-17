package com.huella.acceso;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import java.time.LocalDate;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

public final class HorariosActivity extends CitasActivity {
    private final ArrayList<Long> ids = new ArrayList<>();
    private Spinner vets;
    private Button dateButton, save;
    private EditText hours;
    private LocalDate date = LocalDate.now();
    private LocalDate loadedDate;
    private long loadedVet;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (!ready("Horarios de atención")) return;
        if (!canManage() && !veterinarianMode()) { message("Tu cuenta no puede cambiar horarios."); return; }

        label(veterinarianMode() ? "Elige la fecha de tu jornada." : "Selecciona un veterinario y una fecha.");
        vets = new Spinner(this);
        if (!veterinarianMode()) {
            add(vets);
            action("Actualizar veterinarios", this::loadVets);
        }
        dateButton = action("Fecha · " + date, this::chooseDate);
        action("Cargar jornada", this::loadSchedule);
        label("Horas separadas por comas. Ejemplo: 08:00, 09:30, 14:00.");
        hours = new EditText(this);
        hours.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
        hours.setHint("HH:mm, HH:mm");
        add(hours);
        save = action("Guardar jornada", this::saveSchedule);
        clearSchedule();
        vets.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (selectedVet() != loadedVet) clearSchedule();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { clearSchedule(); }
        });
        if (veterinarianMode()) loadSchedule();
        else loadVets();
    }

    private void clearSchedule() {
        loadedDate = null;
        hours.setText("");
        hours.setVisibility(View.GONE);
        save.setVisibility(View.GONE);
    }

    private long selectedVet() {
        if (veterinarianMode()) return -1;
        int index = vets.getSelectedItemPosition() - 1;
        return index >= 0 && index < ids.size() ? ids.get(index) : 0;
    }

    private void loadVets() {
        clearSchedule();
        request(() -> app().api.vets(false), rows -> {
            ids.clear();
            var names = new ArrayList<String>();
            names.add("Selecciona un veterinario");
            for (int i = 0; i < rows.length(); i++) {
                JSONObject row = rows.optJSONObject(i);
                if (row == null || row.optLong("id") <= 0) continue;
                ids.add(row.optLong("id"));
                names.add((row.optString("nombres") + " " + row.optString("apellidos")).trim());
            }
            var adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            vets.setAdapter(adapter);
            if (ids.isEmpty()) message("No hay veterinarios en el directorio.");
        }, () -> {});
    }

    private void chooseDate() {
        DatePickerDialog picker = new DatePickerDialog(this, (view, year, month, day) -> {
            date = LocalDate.of(year, month + 1, day);
            dateButton.setText("Fecha · " + date);
            clearSchedule();
        }, date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
        picker.getDatePicker().setMinDate(System.currentTimeMillis());
        picker.show();
    }

    private void loadSchedule() {
        long vet = selectedVet();
        if (vet == 0) { message("Selecciona un veterinario."); return; }
        LocalDate chosenDate = date;
        clearSchedule();
        request(() -> app().api.attentionHours(vet, chosenDate.toString(), null), data -> {
            loadedDate = chosenDate;
            loadedVet = vet;
            showHours(data);
            if (data.length() == 0) message("La jornada está cerrada para esta fecha.");
        }, () -> {});
    }

    private void showHours(JSONArray data) {
        var values = new ArrayList<String>();
        for (int i = 0; i < data.length(); i++) values.add(data.optString(i));
        hours.setText(String.join(", ", values));
        hours.setVisibility(View.VISIBLE);
        save.setVisibility(View.VISIBLE);
    }

    private void saveSchedule() {
        long vet = selectedVet();
        LocalDate chosenDate = date;
        if (vet != loadedVet || !chosenDate.equals(loadedDate)) {
            message("Carga la jornada de esta fecha primero.");
            return;
        }

        String input = hours.getText().toString().trim();
        JSONArray data = new JSONArray();
        if (!input.isEmpty()) for (String value : input.split(",", -1)) {
            String hour = value.trim();
            if (!hour.matches("(?:[01][0-9]|2[0-3]):[0-5][0-9]")) {
                hours.setError("Cada hora debe tener el formato HH:mm.");
                return;
            }
            data.put(hour);
        }
        if (data.length() > 24) {
            hours.setError("Puedes configurar hasta 24 horarios.");
            return;
        }
        if (data.length() == 0) {
            new AlertDialog.Builder(this).setMessage("¿Cerrar la atención para esta fecha?")
                    .setNegativeButton("Volver", null)
                    .setPositiveButton("Cerrar jornada", (dialog, which) -> persist(vet, chosenDate, data)).show();
        } else persist(vet, chosenDate, data);
    }

    private void persist(long vet, LocalDate chosenDate, JSONArray data) {
        request(() -> app().api.attentionHours(vet, chosenDate.toString(), data), saved -> {
            showHours(saved);
            successMessage("Horario guardado para " + chosenDate + ".");
        }, () -> {});
    }
}
