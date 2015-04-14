package com.example.lemon.pedometer.ui;

import name.bagi.levente.pedometer.Pedometer;
import name.bagi.levente.pedometer.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pedometer.db.DBManager;

public class LoginActivity extends Activity implements OnClickListener{

	private TextView register;
	private EditText userName;
	private EditText passwd;
	private FButton login;
	private FButton exit;
	private DBManager dbm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		
		register = (TextView) findViewById(R.id.register_text_view);
		userName = (EditText) findViewById(R.id.username);
		passwd = (EditText) findViewById(R.id.passwd);
		
		login = (FButton) findViewById(R.id.login);
		exit = (FButton) findViewById(R.id.exit);
		
		register.setOnClickListener(this);
		login.setOnClickListener(this);
		exit.setOnClickListener(this);
		
		dbm = DBManager.getInstance(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.register_text_view:
			startActivity(new Intent(this,RegisterActivity.class));
			break;
		case R.id.login:
			if(dbm.userIsExist(userName.getText().toString(), passwd.getText().toString())){
				Intent intent = new Intent(this,Pedometer.class);
				intent.putExtra("uName", userName.getText().toString());
				startActivity(intent);
				finish();
			}else{
				Toast.makeText(this,"用户名或密码错误", 2000).show();
			}
			break;
		case R.id.exit:
			finish();
			break;
			default:
				break;
		}
		
	}

	
}
