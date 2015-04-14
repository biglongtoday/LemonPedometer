package com.example.lemon.pedometer.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import name.bagi.levente.pedometer.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.pedometer.db.DBManager;
import com.example.pedometer.model.User;

@SuppressLint("ShowToast") public class RegisterActivity extends Activity implements OnClickListener{

	private EditText nickName;
	private EditText passwd;
	private EditText passwd_cfm;
	private EditText email;
	private ImageButton backButton;
	private FButton registerButton;
	
	private boolean v_name;
	private boolean v_passwd;
	private boolean v_passwd_cfm;
	private boolean v_email;
	
	private DBManager dbm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);
		
		nickName = (EditText) findViewById(R.id.register_nickname);
		passwd = (EditText)findViewById(R.id.register_password);
		passwd_cfm = (EditText) findViewById(R.id.register_password_cfm);
		email = (EditText) findViewById(R.id.register_email);
		
		backButton = (ImageButton) findViewById(R.id.commen_title_bar_ret);
		backButton.setVisibility(View.VISIBLE);
		registerButton = (FButton) findViewById(R.id.register_button);
		
		backButton.setOnClickListener(this);
		registerButton.setOnClickListener(this);
		dbm = DBManager.getInstance(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.commen_title_bar_ret:
			finish();
			break;
		case R.id.register_button:
			
			if(verifyNickName(nickName.getText().toString())){
				if(verifyPasswd(passwd.getText().toString(),passwd_cfm.getText().toString())){
					if(verifyEmail(email.getText().toString())){
						User u = new User();
						u.setName(nickName.getText().toString());
						u.setPasswd(passwd.getText().toString());
						u.setEmail(email.getText().toString());
						dbm.addUser(u);
						Intent intent = new Intent(this,LoginActivity.class);
						intent.putExtra("name", nickName.getText().toString());
						startActivity(intent);
						finish();
						dbm.closeDB();
					}
				}
			}

			break;
		default:
				break;
		}
		
	}
	
	
	public boolean verifyNickName(String s) {
        if(nickName.getText().toString().length() > 20 ){
        	Toast.makeText(this,"用户昵称不能超过20个字符",2000).show();
			nickName.setFocusable(true);
			nickName.setText("");
			//v_name = false;
			return false;
        }else if(dbm.userIsExist(nickName.getText().toString(),null)){
        	Toast.makeText(this,"用户已存在，请登录",2000).show();
			nickName.setFocusable(true);
			nickName.setText("");
			//v_name = false;
			return false;
        }else
        	//v_name=true;
        	return true;
    }
	
	public boolean verifyPasswd(String passwd,String passwd_cfm){
		if(passwd.length() < 6 || passwd.length() > 15){
			Toast.makeText(this, "密码长度允许的长度范围是6到15位", 2000).show();
			v_passwd = false;
			//v_passwd_cfm = false;
			return false;
		}else if(!passwd.equals(passwd_cfm)){
			Toast.makeText(this, "两次输入的密码不一样", 2000).show();
			//v_passwd = false;
			//v_passwd_cfm = false;
			return false;
		}else{
			return true;
		}
		
	}
	
	public boolean verifyEmail(String s){
		Pattern p = Pattern.compile( "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
	            "\\@" +
	            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
	            "(" +
	                "\\." +
	                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
	            ")+");
		Matcher m = p.matcher(s);
		return  m.matches();
		
	}
	

	
}
