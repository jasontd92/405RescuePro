package com.example.jasontd.rescuepro405;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle("RescuePro");

        TextView txtName = (TextView)findViewById(R.id.name);
        TextView txtVehicle = (TextView)findViewById(R.id.vehicle);
        TextView txtTime = (TextView)findViewById(R.id.timeofaccident);
        TextView txtLocation = (TextView)findViewById(R.id.location);
        TextView txtSpeed = (TextView)findViewById(R.id.speedofimpact);
        Button btnCall = (Button)findViewById(R.id.callbutton);

        Bundle b = getIntent().getExtras();
        if (b != null)
        {
            String name = b.getString("name");
            txtName.setText(name);

            if (name.equals("John Doe"))
            {
                txtVehicle.setText("Ford F-150 2013");
                txtTime.setText("N/A");
                txtLocation.setText("Boise, ID");
                txtSpeed.setText("N/A");
                btnCall.setText("Call John");
            }
        }
    }
}
