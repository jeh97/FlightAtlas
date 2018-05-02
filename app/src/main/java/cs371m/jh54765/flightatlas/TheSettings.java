package cs371m.jh54765.flightatlas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

/**
 * Created by Jacob on 3/29/18.
 */

public class TheSettings extends AppCompatActivity {
    protected boolean show_markers;
    protected boolean show_routes;
    protected Switch switch_markers;
    protected Switch switch_routes;
    protected Button button_ok;
    protected Button button_cancel;

    public DBManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        db = DBManager.getInstance(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.show_markers = getIntent().getBooleanExtra("show_markers",true);
        this.show_routes = getIntent().getBooleanExtra("show_routes",true);

        this.switch_markers = (Switch) findViewById(R.id.switch_markers);
        this.switch_routes = (Switch) findViewById(R.id.switch_routes);

        this.button_cancel = (Button) findViewById(R.id.button_cancel);
        this.button_ok = (Button) findViewById(R.id.button_ok);

        this.switch_markers.setChecked(this.show_markers);
        this.switch_routes.setChecked(this.show_routes);

    }

    protected void okButtonIsPressed(View view) {
        this.show_markers = switch_markers.isChecked();
        this.show_routes = switch_routes.isChecked();

        Intent _result = new Intent();
        _result.putExtra("show_markers",this.show_markers);
        _result.putExtra("show_routes",this.show_routes);
        setResult(Activity.RESULT_OK,_result);
        finish();
    }
    protected void cancelButtonIsPressed(View view) {
        Intent _result = new Intent();
        _result.putExtra("show_markers",this.show_markers);
        _result.putExtra("show_routes",this.show_routes);
        setResult(Activity.RESULT_OK,_result);
        finish();
    }

    public void enableButtonIsPressed(View view) {
        db.enableAllAirlines();
    }

    public void disableButtonIsPressed(View view) {
        db.disableAllAirlines();

    }
}
