package cs371m.jh54765.flightatlas;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.SupportMapFragment;

public class MapFragment extends SupportMapFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater,container,savedInstanceState);

        View view = inflater.inflate(R.layout.map_fragment,container,false);
        ImageView image = container.findViewById(R.id.imageView_info);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MapFragment","Clicked Info");
            }
        });
        return view;
    }
}
