package com.huella.acceso;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONArray;

public final class PropietariosActivity extends CitasActivity {
    private LinearLayout list;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (!ready("Propietarios")) return;
        action("Actualizar", this::load);
        list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        add(list);
        load();
    }

    private void load() {
        request(app().api::owners, this::showOwners, () -> {});
    }

    private void showOwners(JSONArray owners) {
        list.removeAllViews();
        if (owners.length() == 0) {
            message("No hay propietarios registrados.");
            return;
        }
        for (int i = 0; i < owners.length(); i++) {
            var owner = owners.optJSONObject(i);
            if (owner == null) continue;
            String name = (owner.optString("nombres", "") + " " + owner.optString("apellidos", "")).trim();
            String email = owner.optString("email", "");
            String phone = owner.optString("telefono", "");
            TextView card = new TextView(this);
            card.setText(name + (email.isEmpty() ? "" : "\n" + email)
                    + (phone.isEmpty() ? "" : "\n" + phone));
            card.setTextColor(getColor(R.color.ink));
            card.setTextSize(16);
            card.setBackgroundResource(R.drawable.home_tile);
            int padding = (int)(14 * getResources().getDisplayMetrics().density);
            card.setPadding(padding, padding, padding, padding);
            var params = new LinearLayout.LayoutParams(-1, -2);
            params.bottomMargin = padding;
            list.addView(card, params);
        }
    }
}
