package aoyaman.github.io;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.SurfaceView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MySurfaceView surfaceView = new MySurfaceView(this);

        setContentView(surfaceView);
    }
}