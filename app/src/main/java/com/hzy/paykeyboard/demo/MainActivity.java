package com.hzy.paykeyboard.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.hzy.paykeyboard.IPasswordCallback;
import com.hzy.paykeyboard.PayKeyboard;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, IPasswordCallback {

    PayKeyboard mPayKeyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.show_keyboard).setOnClickListener(this);
        mPayKeyboard = new PayKeyboard(this);
        mPayKeyboard.setPasswordCallback(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.show_keyboard) {
            mPayKeyboard.show();
        }
    }

    @Override
    public void onInputComplete(Object encrypted) {
        String password = mPayKeyboard.getDecryptedPassword(encrypted);
        Toast.makeText(this, password, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInputCancel() {
        Toast.makeText(this, "cancel", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPasswordForget() {
        Toast.makeText(this, "forget", Toast.LENGTH_SHORT).show();
    }
}
