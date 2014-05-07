/**
 * 
 */
package com.stripe1.xmouse.util;


public class CustomItem{
	
	int mIcon;
	int mSpans;
	String mTitle;
	String mCommand;
	public CustomItem(int icon, int spans, String title, String desc){
		mIcon=icon;
		mSpans=spans;
		mTitle= title;
		mCommand=desc;
	}
	
	public int getmIcon() {
		return mIcon;
	}
	public void setmIcon(int mIcon) {
		this.mIcon = mIcon;
	}
	public int getmSpans() {
		return mSpans;
	}
	public void setmSpans(int mSpans) {
		this.mSpans = mSpans;
	}
	public String getmTitle() {
		return mTitle;
	}
	public void setmTitle(String mTitle) {
		this.mTitle = mTitle;
	}
	public String getmCommand() {
		return mCommand;
	}
	public void setmCommand(String mCommand) {
		this.mCommand = mCommand;
	}
	
}
