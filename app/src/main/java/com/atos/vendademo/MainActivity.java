package com.atos.vendademo;

import android.Manifest;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.*;
import android.provider.Settings;
import android.text.*;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

public class MainActivity extends Activity {
    private final String[] platforms = {"Cakto", "Kiwify", "Eduzz", "Hotmart"};
    private final int[] colors = {0xff146342, 0xff237b40, 0xff1859ad, 0xffbf3d16};
    private final int[] platformIcons = {R.drawable.logo_cakto, R.drawable.logo_kiwify, R.drawable.logo_eduzz, R.drawable.logo_hotmart};
    private static final String CHANNEL = "simulation";
    private Spinner platform;
    private EditText value;
    private TextView preview, feedback;
    private String pendingAmount;
    private int pendingPlatform;
    private int dp(int n) { return Math.round(n * getResources().getDisplayMetrics().density); }
    private TextView text(String s, int size, boolean bold) {
        TextView t = new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(0xff18181b);
        if (bold) t.setTypeface(null, Typeface.BOLD);
        return t;
    }
    private void gap(LinearLayout box, int height) { View v = new View(this); box.addView(v, new LinearLayout.LayoutParams(1, dp(height))); }
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        if (state != null) { pendingAmount = state.getString("pendingAmount"); pendingPlatform = state.getInt("pendingPlatform"); }
        NotificationManager manager = getSystemService(NotificationManager.class);
        NotificationChannel channel = new NotificationChannel(CHANNEL, "Simulações de venda", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Notificações fictícias, sem transações reais."); manager.createNotificationChannel(channel);
        ScrollView scroll = new ScrollView(this); scroll.setFillViewport(true); scroll.setBackgroundColor(0xfff6f6f4);
        LinearLayout box = new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL); box.setPadding(dp(24), dp(24), dp(24), dp(32));
        scroll.addView(box); setContentView(scroll);
        box.setOnApplyWindowInsetsListener((v, insets) -> {
            v.setPadding(dp(24), dp(24) + insets.getSystemWindowInsetTop(), dp(24), dp(32) + insets.getSystemWindowInsetBottom()); return insets;
        }); box.requestApplyInsets();
        box.addView(text("NOTIFICAÇÃO", 13, true)); gap(box, 26);
        box.addView(text("Sua notificação,\nem um toque.", 30, true)); gap(box, 12);
        box.addView(text("Simulações para brincadeiras. Nenhuma venda é realizada.", 16, false)); gap(box, 30);
        box.addView(text("Plataforma", 14, true));
        platform = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, platforms);
        platform.setAdapter(adapter); platform.setContentDescription("Escolher plataforma da simulação");
        box.addView(platform, new LinearLayout.LayoutParams(-1, dp(56))); gap(box, 20);
        box.addView(text("Valor em reais", 14, true));
        value = new EditText(this); value.setSingleLine(true); value.setHint("Ex.: 7,87"); value.setTextSize(24);
        value.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        value.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        box.addView(value, new LinearLayout.LayoutParams(-1, dp(64))); gap(box, 24);
        box.addView(text("PRÉVIA", 12, true)); gap(box, 12);
        preview = text("", 17, false); preview.setPadding(dp(18), dp(20), dp(18), dp(20));
        GradientDrawable surface = new GradientDrawable(); surface.setColor(0xffffffff); surface.setCornerRadius(dp(16)); preview.setBackground(surface);
        box.addView(preview); gap(box, 26);
        Button generate = new Button(this); generate.setText("Gerar notificação"); generate.setAllCaps(false); generate.setTextSize(17); generate.setTextColor(0xffffffff);
        generate.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xff18181b)); box.addView(generate, new LinearLayout.LayoutParams(-1, dp(56)));
        feedback = text("", 14, false); gap(box, 12); box.addView(feedback);
        Button settings = new Button(this); settings.setText("Configurações de notificação"); settings.setAllCaps(false);
        settings.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName())));
        box.addView(settings); gap(box, 12);
        box.addView(text("O Android identifica a origem como Notificação.", 13, false));
        platform.setSelection(getPreferences(0).getInt("platform", 0)); value.setText(getPreferences(0).getString("value", "7,87"));
        value.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s,int start,int count,int after){}
            public void onTextChanged(CharSequence s,int start,int before,int count){ updatePreview(); }
            public void afterTextChanged(Editable e){}
        });
        platform.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,int position,long id) { updatePreview(); }
            public void onNothingSelected(AdapterView<?> parent){}
        });
        generate.setOnClickListener(v -> generate()); updatePreview();
    }
    private void updatePreview() {
        String amount; try { amount = Money.format(value.getText().toString()); } catch (IllegalArgumentException e) { amount = "R$ —"; }
        preview.setText("Notificação • " + platforms[platform.getSelectedItemPosition()] + "\n\nVenda Aprovada!\nValor: " + amount);
    }
    private void generate() {
        try { pendingAmount = Money.format(value.getText().toString()); value.setError(null); }
        catch (IllegalArgumentException e) { value.setError(e.getMessage()); value.requestFocus(); return; }
        pendingPlatform = platform.getSelectedItemPosition();
        getPreferences(0).edit().putString("value", value.getText().toString()).putInt("platform", pendingPlatform).apply();
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 10); return;
        }
        send();
    }
    private void send() {
        NotificationManager manager = getSystemService(NotificationManager.class);
        NotificationChannel channel = manager.getNotificationChannel(CHANNEL);
        if (!manager.areNotificationsEnabled() || channel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
            feedback.setText("Notificações bloqueadas. Ative nas configurações abaixo."); return;
        }
        PendingIntent open = PendingIntent.getActivity(this,0,new Intent(this,MainActivity.class),PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this, CHANNEL)
            .setSmallIcon(com.atos.vendademo.R.drawable.ic_notification)
            .setLargeIcon(android.graphics.drawable.Icon.createWithResource(this, platformIcons[pendingPlatform]))
            .setColor(colors[pendingPlatform])
            .setContentTitle("Venda Aprovada!")
            .setContentText("Valor: " + pendingAmount)
            .setSubText(platforms[pendingPlatform])
            .setContentIntent(open).setAutoCancel(true).setVisibility(Notification.VISIBILITY_PRIVATE)
            .build();
        int id = getPreferences(0).getInt("notificationId",0) + 1;
        getPreferences(0).edit().putInt("notificationId",id).apply(); manager.notify(id,notification);
        ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(value.getWindowToken(),0);
        feedback.setText("Simulação enviada. Abra a barra de notificações para ver.");
    }
    @Override public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state); state.putString("pendingAmount", pendingAmount); state.putInt("pendingPlatform", pendingPlatform);
    }
    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(requestCode,permissions,results);
        if (requestCode == 10 && results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED && pendingAmount != null) send();
        else feedback.setText("Permita as notificações nas configurações abaixo para gerar a simulação.");
    }
}
