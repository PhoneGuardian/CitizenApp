package com.matesnetwork.interfaces;

public interface RequestMissedCallListner {
	public void onRequestMissedCallSent();
	public void onRequestMissedCallSucess();
	public void onRequestMissedCallFailed();
	public void onVerificationSucess();
	public void onVerificationFailed();
}
