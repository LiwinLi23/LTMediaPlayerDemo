package com.example.bestvplayerdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity2 extends Activity {

	Button btn1;
	Button btn2;
	Button btn3;

	EditText editPhone;
	EditText editCode;
	
	Button btnInit;
	Button btnSendVrifCode;
	Button btnRegister;
	Button btnCreateOrder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btn1 = (Button)findViewById(R.id.button1);
		btn2 = (Button)findViewById(R.id.button2);
		btn3 = (Button)findViewById(R.id.button3);
		mUrlAddrView = (EditText)findViewById(R.id.url_addr);
		mUrlAddrView.setText(URL_APPLE_TEST);

		btn1.setEnabled(true);
		btn2.setEnabled(false);
		btn3.setEnabled(false);
		
		btn1.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {

				SimpleTestActivity2.isLive = false;
				Intent intent = new Intent(MainActivity2.this, SimpleTestActivity2.class);
				startActivity(intent);
			}
		});

		btn2.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {

				
			}
		});
		
		btn3.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {

			}
		});
	}

	private static final String URL_APPLE_TEST = "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8";

	private EditText mUrlAddrView;
}
