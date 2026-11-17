package com.huella.acceso;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BaseActivity extends Activity {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    protected boolean busy;

    protected HuellaApp app() { return (HuellaApp) getApplication(); }
    protected interface Result<T> { void accept(T value); }

    protected <T> void request(Callable<T> task, Result<T> success, Runnable cleanup) {
        if (busy) return;
        busy = true;
        findViewById(R.id.loading).setVisibility(View.VISIBLE);
        setControlsEnabled(findViewById(android.R.id.content), false);
        message("");
        executor.execute(() -> {
            T value = null;
            Exception failure = null;
            try { value = task.call(); }
            catch (Exception error) {
                failure = error;
                Log.e("HuellaNetwork", "No se completó la operación (" + error.getClass().getSimpleName() + ")");
            }
            final T result = value;
            final Exception error = failure;
            runOnUiThread(() -> {
                if (isDestroyed() || isFinishing()) return;
                busy = false;
                findViewById(R.id.loading).setVisibility(View.GONE);
                setControlsEnabled(findViewById(android.R.id.content), true);
                cleanup.run();
                if (error == null) success.accept(result);
                else {
                    message(failureMessage(error));
                    if (error instanceof VetApi.HttpFailure
                            && ((VetApi.HttpFailure) error).code == 401
                            && app().user != null) sessionEnded();
                }
            });
        });
    }

    protected String failureMessage(Exception error) {
        if (error instanceof VetApi.HttpFailure) return error.getMessage();
        if (error instanceof java.net.SocketTimeoutException) {
            return "El servidor está tardando demasiado. Comprueba la conexión e intenta de nuevo.";
        }
        if (error instanceof java.net.ConnectException || error instanceof java.net.UnknownHostException) {
            return "No encontramos el servidor. Revisa la dirección y comprueba que esté encendido.";
        }
        if (error instanceof javax.net.ssl.SSLException) {
            return "No se pudo establecer una conexión segura con el servidor.";
        }
        if (error instanceof org.json.JSONException) {
            return "La respuesta del servidor no tiene el formato esperado.";
        }
        return "Se interrumpió la comunicación con la clínica. Revisa tu conexión.";
    }

    private void setControlsEnabled(View view, boolean enabled) {
        if (view instanceof Button || view instanceof EditText || view instanceof android.widget.Spinner) view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                setControlsEnabled(group.getChildAt(i), enabled);
            }
        }
    }

    protected void passwordToggle(int fieldId, int buttonId) {
        EditText input = findViewById(fieldId);
        Button toggle = findViewById(buttonId);
        toggle.setOnClickListener(view -> {
            int cursor = input.getSelectionStart();
            boolean hidden = input.getTransformationMethod() instanceof PasswordTransformationMethod;
            input.setTransformationMethod(hidden ? HideReturnsTransformationMethod.getInstance()
                    : PasswordTransformationMethod.getInstance());
            input.setSelection(Math.max(0, cursor));
            toggle.setText(hidden ? "Ocultar" : "Mostrar");
            toggle.setContentDescription(hidden ? "Ocultar contraseña" : "Mostrar contraseña");
        });
    }

    protected void invalid(int fieldId, String reason) {
        EditText input = findViewById(fieldId);
        input.setError(reason);
        input.requestFocus();
        message(reason);
    }

    protected void message(String text) {
        TextView message = findViewById(R.id.message);
        message.setText(text);
        message.setVisibility(text.isEmpty() ? View.GONE : View.VISIBLE);
        message.setBackgroundResource(R.drawable.notice);
        message.setTextColor(0xff594365);
    }

    protected void successMessage(String text) {
        message(text);
        TextView message = findViewById(R.id.message);
        message.setBackgroundResource(R.drawable.success);
        message.setTextColor(0xff2b654a);
    }

    protected void sessionEnded() {
        app().user = null;
        app().api = null;
        android.content.Intent intent = new android.content.Intent(this, LoginActivity.class);
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override protected void onDestroy() {
        executor.shutdownNow();
        super.onDestroy();
    }
}
