package com.god.attendance;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.god.attendence.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class CaptchaDialogFragment extends DialogFragment{

	private ImageView ivCapImg;
	private ProgressBar pbar;
	
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
	public void onStart() {
		super.onStart();
		
		// Reference the views from the layout
		Dialog dialogView = CaptchaDialogFragment.this.getDialog();
		Button bRefreshCaptcha = (Button) dialogView.findViewById(R.id.bRefresh);
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
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(inflater.inflate(R.layout.captcha_dialog, null))
		// Set the dialog title
		.setTitle("Input Captcha")
		// Set the dialog icon
		.setIcon(R.drawable.ic_menu_edit)
		.setCancelable(true)
		// Add action buttons
		.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				// Send the positive button event back to the host activity
				mListener.onDialogPositiveClick(CaptchaDialogFragment.this);
			}
		});    

		return builder.create();
	}

	private void getImg() 
	{
		// TODO Set priority and timeout
		Log.i(Login.class.getName(), "Loading captcha image...");
		ImageLoader imageLoader = MyVolley.getInstance().getImageLoader();
		imageLoader.setBatchedResponseDelay(0);
		imageLoader.get("https://academics.ddn.upes.ac.in/upes/modules/create_image.php",
				new ImageLoader.ImageListener() {

			final ImageView view = ivCapImg;
			final int defaultImageResId = R.drawable.spinner_black_48;
			final int errorImageResId = R.drawable.ic_menu_report_image;
			@Override
			public void onErrorResponse(VolleyError error) {
				if (errorImageResId != 0) {
					pbar.setVisibility(View.INVISIBLE);
					view.setVisibility(View.VISIBLE);
					view.setImageResource(errorImageResId);
				}
			}

			@Override
			public void onResponse(ImageContainer response, boolean isImmediate) {
				if (response.getBitmap() != null) {
					pbar.setVisibility(View.INVISIBLE);
					view.setVisibility(View.VISIBLE);
					view.setImageBitmap(response.getBitmap());
					Log.i(Login.class.getName(), "Loaded captcha image.");
				} else if (defaultImageResId != 0) {
					pbar.setVisibility(ProgressBar.VISIBLE);
					view.setVisibility(View.INVISIBLE);
				}
			}
		});
	}
}
