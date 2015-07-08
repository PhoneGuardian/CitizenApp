package com.matesnetwork.callverification;

import java.util.ArrayList;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.matesnetwork.callverification.AuthenticateToken.AuthenticateListner;
import com.matesnetwork.callverification.RequestmissedCallAsync.RequestMissedCallListnr;
import com.matesnetwork.interfaces.VerificationListner;

public class Cognalys {

	private Cognalys() {
	}

	// This method returns the country code
	public static String getCountryCode(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
    //  String numWithCode = Iso2Phone.getPhone(tm.getNetworkCountryIso());
        String numWithCode = Iso2Phone.getPhone(tm.getSimCountryIso());
		return numWithCode;
        //return "+385";

	}

	// This method provides the analytics
	public static void enableAnalytics(Context context,
			boolean enableActiveUserStatics, boolean enablUserLocation) {
		if (enableActiveUserStatics || enablUserLocation) {
			if (!TextUtils.isEmpty(Methods.getUserId(context))) {
				new SendLocationInBG(context, Methods.getaccessTok(context),
						Methods.getappId(context), Methods.getUserId(context),
						enablUserLocation, enableActiveUserStatics).execute("");
			}

		}
	}

	// This method will be called by a user to verify the mobile number
	public static void verifyMobileNumber(final Context context,
			final String tokenId, final String app_id,
			final String phoneNumber,
			final VerificationListner verificationListner) {
		if (TextUtils.isEmpty(tokenId)) {
			ArrayList<String> errorCode = new ArrayList<String>();
			errorCode.add("602");
			verificationListner.onVerificationFailed(errorCode);
			// Authentication error Invalid or null TokenId ;
		} else {
			new AuthenticateToken(context, tokenId, Methods.getSHA(context),
					app_id, new AuthenticateListner() {

						@Override
						public String[] onVerifySucess(String[] numberArray) {
							//
							new RequestmissedCallAsync(context, tokenId,
									Methods.getSHA(context), app_id,
									phoneNumber, numberArray,
									new RequestMissedCallListnr() {

										@Override
										public String onRequestStart() {
											return null;
										}

										@Override
										public String onRequestFailed(
												ArrayList<String> errorCodeList) {
											verificationListner
													.onVerificationFailed(errorCodeList);
											return null;
										}

										@Override
										public String[] onRequestSucess(
												String[] numb) {

											return null;
										}

										@Override
										public void onNumberVerified() {
											verificationListner
													.onVerificationSucess();
											ConfirmRegistration confirmRegistration = new ConfirmRegistration(
													context, tokenId, Methods
															.getSHA(context),
													app_id, phoneNumber);
											confirmRegistration.execute("");

										}

										@Override
										public void onNumberNotVerified(
												ArrayList<String> errorCodeList) {
											verificationListner
													.onVerificationFailed(errorCodeList);
										}

									}).execute("");
							return null;
						}

						@Override
						public String onVerifyFailed(ArrayList<String> errorList) {
							verificationListner.onVerificationFailed(errorList);
							return null;
						}
					}).execute("");

		}
	}

}
