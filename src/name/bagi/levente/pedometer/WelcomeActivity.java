package name.bagi.levente.pedometer;

import com.example.lemon.pedometer.ui.LoginActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class WelcomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_welcome);
		
		new Handler().postDelayed(new Runnable(){
			@Override
			public void run(){
				startActivity(new Intent(WelcomeActivity.this,LoginActivity.class));
				WelcomeActivity.this.finish();
			}
		}, 2000);
	}
}
