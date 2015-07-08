package com.matesnetwork.interfaces;

import java.util.ArrayList;

public interface VerificationListner {
	
	public void onVerificationStarted();
	public void onVerificationSucess();
	public void onVerificationFailed(ArrayList<String> errorList);
}
