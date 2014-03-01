package com.shalzz.attendance;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.shalzz.attendance.R;

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
		
		final AlertDialog d = builder.create(); 
		d.setOnShowListener(new DialogInterface.OnShowListener() {

		    @Override
		    public void onShow(DialogInterface dialog) {

		        Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
		        b.setOnClickListener(new View.OnClickListener() {

		            @Override
		            public void onClick(View view) {
	            		EditText captxt = (EditText) d.findViewById(R.id.etCapTxt);
		            	if (captxt.getText().toString().length()!=6) {
		            		captxt.setError("Captcha must be of 6 digits");
		            		Miscellanius.showKeyboard(getActivity(), captxt);
		            	}
		            	else
		            		mListener.onDialogPositiveClick(CaptchaDialogFragment.this);
		            }
		        });
		    }
		});
		
		
		return d;
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

		// Get the Captcha Image
		getImg();

		// OnClickListener event for the Reload captcha Button
		bRefreshCaptcha.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {	
				Log.i(Login.class.toString(), "Refreshing Captcha...");
				getImg();
			}
		});

		// logs in when user press done on keyboard.
		Captxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {   
					Miscellanius.closeKeyboard(getActivity(), Captxt);
					mListener.onDialogPositiveClick(CaptchaDialogFragment.this); 
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
		Log.i(Login.class.getName(), "Loading captcha image...");
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
					String msg = VolleyErrorHelper.getMessage(error, getActivity());
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
					Log.i(Login.class.getName(), "Loaded captcha image.");
				} else if (defaultImageResId != 0) {
					pbar.setVisibility(ProgressBar.VISIBLE);
					view.setVisibility(View.INVISIBLE);
				}
			}
		});
	}
}
