package mx.com.theevent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Gatsu on 11/14/2016.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setContentView(R.layout.splashactivity);
//
//        TextView textview = (TextView) this.findViewById(R.id.cargando);
//        textview.setTypeface(CustomFontsLoader.getTypeface(this,3));
//
//        ImageView animationTarget = (ImageView) this.findViewById(R.id.testImage);
//        Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate);
//        animationTarget.startAnimation(animation);
//
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                startActivity(new Intent(getApplicationContext(), Login.class));
//                finish();
//            }
//        }, 2500);

        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();

    }
}
