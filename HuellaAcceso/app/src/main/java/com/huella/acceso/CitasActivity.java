package com.huella.acceso;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import org.json.JSONObject;

abstract class CitasActivity extends BaseActivity {
    protected LinearLayout content;
    protected boolean ready(String title) {
        setContentView(R.layout.activity_citas);
        if (app().api == null || app().user == null) { sessionEnded(); return false; }
        content = findViewById(R.id.appointment_content);
        ((TextView) findViewById(R.id.appointment_title)).setText(title);
        action("‹ Volver", () -> { if (!busy) finish(); });
        return true;
    }
    protected void show(Class<?> destination) {
        startActivity(new Intent(this, destination));
    }
    protected void add(View view) {
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(-1, -2);
        layout.bottomMargin = (int) (8 * getResources().getDisplayMetrics().density);
        content.addView(view, layout);
    }
    protected Button action(String label, Runnable click) {
        Button button = new Button(this);
        button.setAllCaps(false);
        button.setTextColor(getColor(R.color.purple));
        button.setBackgroundResource(R.drawable.secondary);
        button.setText(label);
        button.setOnClickListener(v -> { if (!busy) click.run(); });
        add(button);
        return button;
    }
    protected void label(String value) {
        TextView text = new TextView(this);
        text.setTextColor(getColor(R.color.ink));
        text.setTextSize(16);
        text.setText(value);
        add(text);
    }
    protected boolean hasRole(String role) {
        var roles = app().user.optJSONArray("roles");
        if (roles != null) for (int i = 0; i < roles.length(); i++) {
            if (role.equals(roles.optString(i))) return true;
        }
        return false;
    }

    protected boolean veterinarianMode() {
        return hasRole("Veterinario") && !hasRole("Administrador")
                && !hasRole("Recepcion") && !hasRole("Recepcionista");
    }

    protected boolean canManage() {
        var roles = app().user.optJSONArray("roles");
        if (roles != null) for (int i = 0; i < roles.length(); i++) {
            String role = roles.optString(i);
            if (role.equals("Administrador") || role.equals("Recepcion") || role.equals("Recepcionista")
                   ) return true;
        }
        return false;
    }
    protected boolean ownerMode() {
        if (canManage() || veterinarianMode()) return false;
        var roles = app().user.optJSONArray("roles");
        if (roles != null) for (int i = 0; i < roles.length(); i++) {
            if ("Propietario".equals(roles.optString(i))) return true;
        }
        return false;
    }

    protected JSONObject incoming() {
        try { return new JSONObject(getIntent().getStringExtra("cita")); }
        catch (Exception invalid) { return null; }
    }
}
