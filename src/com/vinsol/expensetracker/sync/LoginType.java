package com.vinsol.expensetracker.sync;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.vinsol.expensetracker.BaseActivity;
import com.vinsol.expensetracker.Constants;
import com.vinsol.expensetracker.R;

public class LoginType extends BaseActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sync_setup_login_type);
		findViewById(R.id.userSignUp).setOnClickListener(this);
		findViewById(R.id.userSignIn).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(this, UserAuthentication.class);
		switch (v.getId()) {
		case R.id.userSignUp:
			intent.putExtra(Constants.KEY_IS_SIGNUP, true);
			break;
		case R.id.userSignIn:
			intent.putExtra(Constants.KEY_IS_SIGNUP, false);
			break;
		default:
			break;
		}
		startActivity(intent);
	}
	
}
