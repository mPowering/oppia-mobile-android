/* 
 * This file is part of OppiaMobile - https://digital-campus.org/
 * 
 * OppiaMobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * OppiaMobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with OppiaMobile. If not, see <http://www.gnu.org/licenses/>.
 */

package org.digitalcampus.oppia.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;

import org.opendeliver.oppia.R;
import org.digitalcampus.oppia.activity.AppActivity;
import org.digitalcampus.oppia.activity.OppiaMobileActivity;
import org.digitalcampus.oppia.activity.WelcomeActivity;
import org.digitalcampus.oppia.application.SessionManager;
import org.digitalcampus.oppia.listener.SubmitListener;
import org.digitalcampus.oppia.model.User;
import org.digitalcampus.oppia.service.GCMRegistrationService;
import org.digitalcampus.oppia.task.LoginTask;
import org.digitalcampus.oppia.task.Payload;
import org.digitalcampus.oppia.utils.UIUtils;

import java.util.ArrayList;

public class LoginFragment extends AppFragment implements SubmitListener {

	public static final String TAG = LoginFragment.class.getSimpleName();
    private EditText usernameField;
	private EditText passwordField;
	private ProgressDialog pDialog;
    private Context appContext;

    private Button registerBtn;
    private Button loginBtn;
    private Button resetPasswordBtn;
	
	public static LoginFragment newInstance() {
        return new LoginFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_login, container, false);

		usernameField = (EditText) v.findViewById(R.id.login_username_field);
		passwordField = (EditText) v.findViewById(R.id.login_password_field);
		loginBtn  = (Button) v.findViewById(R.id.login_btn);
		registerBtn = (Button) v.findViewById(R.id.register_btn);
		resetPasswordBtn = (Button) v.findViewById(R.id.forgot_btn);
		return v;
	}

	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		appContext = super.getActivity().getApplicationContext();

        loginBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onLoginClick();
			}
		});

		resetPasswordBtn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				WelcomeActivity wa = (WelcomeActivity) LoginFragment.super.getActivity();
				wa.switchTab(WelcomeActivity.TAB_PASSWORD);
			}
		});
		registerBtn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				WelcomeActivity wa = (WelcomeActivity) LoginFragment.super.getActivity();
				wa.switchTab(WelcomeActivity.TAB_REGISTER);
			}
		});

	}

    @Override
    public void onPause(){
        super.onPause();
        if (pDialog != null && pDialog.isShowing()){
            pDialog.dismiss();
        }
    }
	
	protected void onLoginClick(){
		String username = usernameField.getText().toString();
    	//check valid email address format
    	if(username.length() == 0){
    		UIUtils.showAlert(super.getActivity(),R.string.error,R.string.error_no_username);
    		return;
    	}
    	
    	String password = passwordField.getText().toString();
    	
    	// show progress dialog
        pDialog = new ProgressDialog(super.getActivity());
        pDialog.setTitle(R.string.title_login);
        pDialog.setMessage(this.getString(R.string.login_process));
        pDialog.setCancelable(true);
        pDialog.show();
        
    	ArrayList<Object> users = new ArrayList<>();
    	User u = new User();
    	u.setUsername(username);
    	u.setPassword(password);
    	users.add(u);
    	
    	Payload p = new Payload(users);
    	LoginTask lt = new LoginTask(super.getActivity());
    	lt.setLoginListener(this);
    	lt.execute(p);
	}
	

	public void submitComplete(Payload response) {
		try {
			pDialog.dismiss();
		} catch (IllegalArgumentException iae){
			//
		}
		if(response.isResult()){
			User user = (User) response.getData().get(0);
            SessionManager.loginUser(appContext, user);

            // Start IntentService to re-register the phone with GCM.
            Intent intent = new Intent(this.getActivity(), GCMRegistrationService.class);
            getActivity().startService(intent);
	    	
			// return to main activity
	    	startActivity(new Intent(super.getActivity(), OppiaMobileActivity.class));
	    	super.getActivity().finish();
		} else {
            Context ctx = super.getActivity();
            if (ctx != null){
                UIUtils.showAlert(ctx, R.string.title_login, response.getResultResponse());
            }
		}
	}
}
