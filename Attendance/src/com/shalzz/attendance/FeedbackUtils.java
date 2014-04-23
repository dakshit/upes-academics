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

package com.shalzz.attendance;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
 
public class FeedbackUtils {
    private static final String FEEDBACK_CHOOSER_TITLE = "Send us some feedback";
    private static final String EMAIL_ADDRESS = "shaleen.jain95@gmail.com";
    
    /**
     * Gets the recipient email address.
     * @return Email address
     */
    private static String[] getFeedbackEmailAddress() {
        return new String[] { EMAIL_ADDRESS };
    }
    
    /**
     * Get the current application name.
     * @param context
     * @return Application name.
     */
    private static String getApplicationName(Context context) {
        return context.getString(context.getApplicationInfo().labelRes);
    }
 
    /**
     * Get the application version.
     * @param context
     * @return
     */
    private static String getAppVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            return "vX.XX";
        }
    }
    
    /**
     * Get Email subject.
     * @param context
     * @return
     */
    private static String getFeedbackEmailSubject(Context context) {
        return getApplicationName(context) + " v" + getAppVersion(context);
    }
    
    /**
     * Get device density.
     * @param context
     * @return
     */
    private static float getDeviceDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }
 
    /**
     * Get the device specifications.
     * @param context
     * @return
     */
    private static String getHandsetInformation(Context context) {
        StringBuilder handsetInfoBuilder = new StringBuilder();
        handsetInfoBuilder.append("Bootloader: " + Build.BOOTLOADER);
        handsetInfoBuilder.append("\nBrand: " + Build.BRAND);
        handsetInfoBuilder.append("\nDevice: " + Build.DEVICE);
        handsetInfoBuilder.append("\nManufacturer: " + Build.MANUFACTURER);
        handsetInfoBuilder.append("\nModel: " + Build.MODEL);
        handsetInfoBuilder.append("\nScreen Density: " + getDeviceDensity(context));
        handsetInfoBuilder.append("\nVersion SDK int: " + Build.VERSION.SDK_INT);
        handsetInfoBuilder.append("\nVersion codename: " + Build.VERSION.CODENAME);
        handsetInfoBuilder.append("\nVersion incremental: " + Build.VERSION.INCREMENTAL);
        handsetInfoBuilder.append("\n");
        return handsetInfoBuilder.toString();
    }
    
    /**
     * Get the email message body.
     * @param context
     * @return
     */
    private static String getFeedbackDeviceInformation(Context context) {
        StringBuilder emailMessage = new StringBuilder();
        emailMessage.append("\n\n_________________");
        emailMessage.append("\n\nDevice info:\n\n");
        emailMessage.append(getHandsetInformation(context));
        emailMessage.append("\nPlease leave this data in the email to help with app issues and write above or below here. \n\n");
        emailMessage.append("_________________\n\n");
        return emailMessage.toString();
    }
    
    /**
     * Sets up the email intent for feedback.
     * @param context
     */
    public static void askForFeedback(Context context) {
        final Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, getFeedbackEmailAddress());
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getFeedbackEmailSubject(context));
        emailIntent.putExtra(Intent.EXTRA_TEXT, getFeedbackDeviceInformation(context));
        context.startActivity(Intent.createChooser(emailIntent, FEEDBACK_CHOOSER_TITLE));
    }
}
