package com.mobilis.testemenu;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;

/**
 * Created by danie on 28/06/2017.
 */

public class PasswordActivity extends Activity {

    private static final String TAG = "PasswordActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
    }
}
