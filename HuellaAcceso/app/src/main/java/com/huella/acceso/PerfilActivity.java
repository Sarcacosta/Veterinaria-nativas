package com.huella.acceso;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Patterns;
import org.json.JSONObject;

public final class PerfilActivity extends BaseActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_perfil);
        if (app().api == null || app().user == null) { sessionEnded(); return; }
        findViewById(R.id.back).setOnClickListener(v -> finish());
        findViewById(R.id.reload).setOnClickListener(v -> load());
        findViewById(R.id.save).setOnClickListener(v -> save());
        load();
    }

    private void load() {
        findViewById(R.id.form).setVisibility(View.GONE);
        request(app().api::profile, this::showProfile, () -> {});
    }

    private void showProfile(JSONObject data) {
        var roles = data.optJSONArray("roles");
        java.util.ArrayList<String> labels = new java.util.ArrayList<>();
        if (roles != null) for (int i = 0; i < roles.length(); i++) labels.add(roles.optString(i));
        ((TextView) findViewById(R.id.account)).setText(data.optString("username")
                + (labels.isEmpty() ? "\nSin rol asignado" : "\n" + String.join(" · ", labels)));
        ((EditText) findViewById(R.id.name)).setText(data.isNull("nombre") ? "" : data.optString("nombre"));
        ((EditText) findViewById(R.id.email)).setText(data.isNull("email") ? "" : data.optString("email"));
        findViewById(R.id.form).setVisibility(View.VISIBLE);
    }

    private void save() {
        String name = ((EditText) findViewById(R.id.name)).getText().toString().trim();
        String email = ((EditText) findViewById(R.id.email)).getText().toString().trim();
        if (name.isEmpty() || name.length() > 100) { invalid(R.id.name, "Escribe tu nombre, máximo 100 caracteres."); return; }
        if (email.length() > 254 || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            invalid(R.id.email, "Escribe un correo válido.");
            return;
        }
        VetApi api = app().api;
        request(() -> api.updateProfile(new JSONObject().put("nombre", name).put("email", email)),
                data -> { showProfile(data); successMessage("Perfil actualizado."); }, () -> {});
    }
}
