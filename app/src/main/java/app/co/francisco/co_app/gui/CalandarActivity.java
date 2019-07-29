package app.co.francisco.co_app.gui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import com.github.clans.fab.FloatingActionButton;

import app.co.francisco.co_app.R;

public class CalandarActivity extends AppCompatActivity {

    DatePicker picker;
    FloatingActionButton btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calandar);

        picker=(DatePicker)findViewById(R.id.calandrier);
        btn_back = (FloatingActionButton)findViewById(R.id.left_calandar);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent().setClass(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });


    }
}
