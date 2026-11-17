package com.huella.acceso;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public final class LoginActivity extends BaseActivity {
    private static final int CREATE_ACCOUNT = 10;
    private EditText server, username, password;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_login);
        server = findViewById(R.id.server);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        server.setText(getSharedPreferences("settings", MODE_PRIVATE)
                .getString("server", "http://10.0.2.2:8080/veterinaria"));
        passwordToggle(R.id.password, R.id.show_password);
        findViewById(R.id.configure).setOnClickListener(view -> showServer());
        findViewById(R.id.login).setOnClickListener(view -> signIn());
        findViewById(R.id.register).setOnClickListener(view -> openRegister());
    }

    private void showServer() {
        View panel = findViewById(R.id.server_panel);
        panel.setVisibility(panel.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    private VetApi client() {
        String url = server.getText().toString().trim();
        VetApi client = new VetApi(url);
        getSharedPreferences("settings", MODE_PRIVATE).edit().putString("server", url).apply();
        return client;
    }

    private void invalidServer() {
        findViewById(R.id.server_panel).setVisibility(View.VISIBLE);
        invalid(R.id.server, "Escribe una dirección HTTP o HTTPS válida.");
    }

    private void openRegister() {
        if (busy) return;
        try {
            app().api = client();
            startActivityForResult(new Intent(this, RegistroActivity.class), CREATE_ACCOUNT);
        } catch (IllegalArgumentException error) { invalidServer(); }
    }

    private void signIn() {
        if (busy) return;
        String name = username.getText().toString().trim();
        String secret = password.getText().toString();
        if (name.isEmpty()) { invalid(R.id.username, "Escribe tu usuario."); return; }
        if (secret.isEmpty()) { invalid(R.id.password, "Escribe tu contraseña."); return; }
        final VetApi api;
        try { api = client(); }
        catch (IllegalArgumentException error) { invalidServer(); return; }
        request(() -> api.login(name, secret), user -> {
            app().api = api;
            app().user = user;
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }, () -> {});
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_ACCOUNT && resultCode == RESULT_OK && data != null) {
            username.setText(data.getStringExtra("username"));
            password.setText("");
            password.requestFocus();
            successMessage(data.getBooleanExtra("pendiente", false)
                    ? "Solicitud de veterinario registrada. Podrás entrar cuando un administrador active tu cuenta."
                    : "Tu cuenta ya está creada. Ingresa tu contraseña para continuar.");
        }
    }
}
