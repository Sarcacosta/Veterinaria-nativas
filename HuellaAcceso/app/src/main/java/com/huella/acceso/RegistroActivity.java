package com.huella.acceso;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.view.View;
import android.widget.TextView;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;

public final class RegistroActivity extends BaseActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_register);
        if (app().api == null) { sessionEnded(); return; }
        Spinner role = findViewById(R.id.registration_role);
        TextView roleHint = findViewById(R.id.registration_role_hint);
        role.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                roleHint.setText(position == 1
                        ? "Un administrador debe activar las cuentas de veterinario."
                        : "Podrás registrar tus mascotas y agendar citas.");
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {
                roleHint.setText("");
            }
        });
        passwordToggle(R.id.password, R.id.show_password);
        passwordToggle(R.id.confirm, R.id.show_confirm);
        findViewById(R.id.back).setOnClickListener(view -> { if (!busy) finish(); });
        findViewById(R.id.save).setOnClickListener(view -> register());
        ((TextView) findViewById(R.id.loading_text)).setText("Estamos creando tu cuenta…");
    }

    private String value(int id) {
        return ((EditText) findViewById(id)).getText().toString().trim();
    }

    private void register() {
        if (busy) return;
        message("");
        int[] fields = {R.id.name, R.id.email, R.id.username, R.id.password, R.id.confirm};
        for (int id : fields) ((EditText) findViewById(id)).setError(null);
        String name = value(R.id.name), email = value(R.id.email), username = value(R.id.username);
        String secret = ((EditText) findViewById(R.id.password)).getText().toString();
        String confirm = ((EditText) findViewById(R.id.confirm)).getText().toString();
        if (name.isEmpty() || name.length() > 100) {
            invalid(R.id.name, "Escribe tu nombre completo, máximo 100 caracteres."); return;
        }
        if (email.length() > 254 || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            invalid(R.id.email, "Escribe un correo válido, como sara@correo.com."); return;
        }
        if (!username.matches("[A-Za-z0-9_.-]{3,40}")) {
            invalid(R.id.username, "Usa de 3 a 40 letras, números, puntos o guiones."); return;
        }
        if (secret.length() < 8 || secret.getBytes(StandardCharsets.UTF_8).length > 72) {
            invalid(R.id.password, "Usa al menos 8 caracteres y hasta 72 bytes UTF-8."); return;
        }
        if (!secret.equals(confirm)) {
            invalid(R.id.confirm, "Esta contraseña debe coincidir con la anterior."); return;
        }
        Spinner role = findViewById(R.id.registration_role);
        boolean veterinarian = role.getSelectedItemPosition() == 1;
        final JSONObject data = new JSONObject();
        try {
            data.put("nombre", name).put("email", email).put("username", username).put("password", secret)
                    .put("rol", veterinarian ? "Veterinario" : "Propietario");
        } catch (org.json.JSONException error) { message("No se pudo preparar el registro."); return; }
        VetApi client = app().api;
        request(() -> { client.register(data); return true; }, result -> {
            // Solo se muestra éxito después de recibir una confirmación HTTP 2xx.
            setResult(RESULT_OK, new Intent().putExtra("username", username)
                    .putExtra("pendiente", veterinarian));
            finish();
        }, () -> {});
    }

    @Override protected String failureMessage(Exception error) {
        if (error instanceof VetApi.HttpFailure) return super.failureMessage(error);
        if (error instanceof java.net.SocketTimeoutException) {
            return "La confirmación tardó demasiado. Tu cuenta podría haberse creado. Vuelve al acceso e intenta iniciar sesión antes de repetir el registro.";
        }
        if (error instanceof java.io.IOException
                && !(error instanceof java.net.ConnectException)
                && !(error instanceof java.net.UnknownHostException)
                && !(error instanceof javax.net.ssl.SSLException)) {
            return "Se interrumpió la confirmación. Antes de volver a registrarte, intenta iniciar sesión con este usuario.";
        }
        return super.failureMessage(error);
    }
}
