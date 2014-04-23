/*  
 *    Copyright (C) 2013 - 2014 Shaleen Jain <shaleen.jain95@gmail.com>
 *
 *	  This file is part of UPES Academics.
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
**/    

package com.shalzz.attendance.fragment;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.shalzz.attendance.Miscellaneous;
import com.shalzz.attendance.R;
import com.shalzz.attendance.activity.LoginActivity;
import com.shalzz.attendance.wrapper.MyVolley;
import com.shalzz.attendance.wrapper.MyVolleyErrorHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CaptchaDialogFragment extends DialogFragment{

	private ImageView ivCapImg;
	private ProgressBar pbar;
	private EditText Captxt;

	// Use this instance of the interface to deliver action events
	CaptchaDialogListener mListener;

	/** The activity that creates an instance of this dialog fragment must
	 * implement this interface in order to receive event callbacks.
	 * Each method passes the DialogFragment in case the host needs to query it. 
	 **/
	public interface CaptchaDialogListener {
		public void onDialogPositiveClick(DialogFragment dialog);
	}

	// Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the CaptchaDialogListener so we can send events to the host
			mListener = (CaptchaDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement CaptchaDialogListener");
		}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		LayoutInflater inflater = getActivity().getLayoutInflater();
		
		builder.setView(inflater.inflate(R.layout.captcha_dialog, null))
		.setTitle("Input Captcha")
		.setIcon(R.drawable.ic_menu_edit)
		.setCancelable(true)
		.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				mListener.onDialogPositiveClick(CaptchaDialogFragment.this);
			}
		});    
		
		final AlertDialog alertDialog = builder.create(); 
		alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

		    @Override
		    public void onShow(DialogInterface dialog) {

        		EditText captxt = (EditText) alertDialog.findViewById(R.id.etCapTxt);
        		Miscellaneous.showKeyboard(getActivity(), captxt);
        		
		        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
		        positiveButton.setOnClickListener(new View.OnClickListener() {

		            @Override
		            public void onClick(View view) {
		        		EditText captxt = (EditText) alertDialog.findViewById(R.id.etCapTxt);
		            	if (captxt.getText().toString().length()!=6) {
		            		captxt.setError("Captcha must be of 6 digits");
		            		Miscellaneous.showKeyboard(getActivity(), captxt);
		            	}
		            	else
		            		mListener.onDialogPositiveClick(CaptchaDialogFragment.this);
		            }
		        });
		    }
		});
		
		
		return alertDialog;
	}

	/**
	 * Called when the DialogView is started. Used to setup the onClick listeners.
	 */
	@Override
	public void onStart() {
		super.onStart();

		// Reference the views from the layout
		Dialog dialogView = CaptchaDialogFragment.this.getDialog();
		Button bRefreshCaptcha = (Button) dialogView.findViewById(R.id.bRefresh);
		Captxt = (EditText) dialogView.findViewById(R.id.etCapTxt);
		ivCapImg = (ImageView) dialogView.findViewById(R.id.ivCapImg);
		pbar = (ProgressBar) dialogView.findViewById(R.id.progressBar1);
		
		AlertDialog alertDialog = (AlertDialog) getDialog();
		final Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
		
		// Get the Captcha Image
		getImg();

		// OnClickListener event for the Reload captcha Button
		bRefreshCaptcha.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {	
				Log.i(LoginActivity.class.toString(), "Refreshing Captcha...");
				getImg();
				Captxt.setText("");
			}
		});

		// logs in when user press done on keyboard.
		Captxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {   
					positiveButton.performClick(); 
					return true;
				}
				return false;
			}});
	}

	/**
	 * Gets the captcha image.
	 */
	private void getImg() 
	{
		// TODO Set priority and timeout
		Log.i(LoginActivity.class.getName(), "Loading captcha image...");
		ImageLoader imageLoader = MyVolley.getInstance().getImageLoader();
		imageLoader.setBatchedResponseDelay(0);
		imageLoader.get("https://academics.ddn.upes.ac.in/upes/modules/create_image.php",
				new ImageLoader.ImageListener() {

			final ImageView view = ivCapImg;
			final int defaultImageResId = 5;
			final int errorImageResId = R.drawable.ic_menu_report_image;
			@Override
			public void onErrorResponse(VolleyError error) {
				if (errorImageResId != 0) {
					pbar.setVisibility(View.INVISIBLE);
					view.setVisibility(View.VISIBLE);
					view.setScaleType(ImageView.ScaleType.CENTER);
					view.setImageResource(errorImageResId);
					String msg = MyVolleyErrorHelper.getMessage(error, getActivity());
					Log.e(getActivity().getClass().getName(), msg);
				}
			}

			@Override
			public void onResponse(ImageContainer response, boolean isImmediate) {
				if (response.getBitmap() != null) {
					pbar.setVisibility(View.INVISIBLE);
					view.setVisibility(View.VISIBLE);
					view.setImageBitmap(response.getBitmap());
					view.setScaleType(ImageView.ScaleType.FIT_XY);
					Log.i(LoginActivity.class.getName(), "Loaded captcha image.");
				} else if (defaultImageResId != 0) {
					pbar.setVisibility(ProgressBar.VISIBLE);
					view.setVisibility(View.INVISIBLE);
				}
			}
		});
	}
}
