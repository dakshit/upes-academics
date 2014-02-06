package com.shalzz.attendance;

import com.actionbarsherlock.widget.SearchView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public abstract class Miscellanius {

	private static AlertDialog.Builder builder = null;
	private static ProgressDialog pd = null;
	
	/**
	 * Shows the default user soft keyboard.
	 * @param mTextView
	 */
	protected static void showKeyboard(Context context, EditText mTextView) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			// only will trigger it if no physical keyboard is open
			imm.showSoftInput(mTextView, 0);
		}
	}
	
	/**
	 * Closes the default user soft keyboard.
	 * @param searchView
	 */
	protected static void closeKeyboard(Context context, SearchView searchView) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			// only will trigger it if no physical keyboard is open
			imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
		}
	}
	
	/**
	 * Displays the default Progress Dialog.
	 * @param mMessage
	 */
	protected static void showProgressDialog(Context context, String mMessage,boolean cancable, DialogInterface.OnCancelListener progressDialogCancelListener) {
		// lazy initialize
		if(pd==null)
		{
			// Setup the Progress Dialog
			pd = new ProgressDialog(context);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.setMessage(mMessage);
			pd.setIndeterminate(true);
			pd.setCancelable(cancable);
			pd.setOnCancelListener(progressDialogCancelListener);
		}
		pd.show();
	}

	/**
	 * Dismisses the Progress Dialog.
	 */
	protected static void dismissProgressDialog() {
		if(pd!=null)
			pd.dismiss();
	}
	
	/**
	 * Displays a basic Alert Dialog.
	 * @param mMessage
	 */
	protected static void showAlertDialog(Context context, String mMessage) {
		// lazy initialize
		if(builder==null)
		{
			builder = new AlertDialog.Builder(context);
			builder.setCancelable(true);
			builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
		}
		dismissProgressDialog();
		builder.setMessage(mMessage);
		AlertDialog alert = builder.create();
		alert.show();
	}
}
