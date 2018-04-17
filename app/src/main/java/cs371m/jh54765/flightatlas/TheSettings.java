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
    protected boolean highlight_dest;
    protected boolean draw_all_routes;
    protected Switch switch_dest;
    protected Switch switch_routes;
    protected Button button_ok;
    protected Button button_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.highlight_dest = getIntent().getBooleanExtra("highlight_dest",true);
        this.draw_all_routes = getIntent().getBooleanExtra("draw_all_routes",true);

        this.switch_dest = (Switch) findViewById(R.id.switch_dest);
        this.switch_routes = (Switch) findViewById(R.id.switch_routes);

        this.button_cancel = (Button) findViewById(R.id.button_cancel);
        this.button_ok = (Button) findViewById(R.id.button_ok);

        this.switch_dest.setChecked(this.highlight_dest);
        this.switch_routes.setChecked(this.draw_all_routes);

    }

    protected void okButtonIsPressed(View view) {
        this.highlight_dest = switch_dest.isChecked();
        this.draw_all_routes = switch_routes.isChecked();

        Intent _result = new Intent();
        _result.putExtra("highlight_dest",this.highlight_dest);
        _result.putExtra("draw_all_routes",this.draw_all_routes);
        setResult(Activity.RESULT_OK,_result);
        finish();
    }
    protected void cancelButtonIsPressed(View view) {
        Intent _result = new Intent();
        _result.putExtra("highlight_dest",this.highlight_dest);
        _result.putExtra("draw_all_routes",this.draw_all_routes);
        setResult(Activity.RESULT_OK,_result);
        finish();
    }
}
