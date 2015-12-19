/**
 * 
 */
package com.stripe1.xmouse;


public class CustomKeyboardButton {
	
	int mIcon;
	int mSpans;
	String mTitle;
	String mCommand;
	String mColor="#FFFFFF";
	public CustomKeyboardButton(int icon, int spans, String title, String desc, String color){
		mIcon=icon;
		mSpans=spans;
		mTitle= title;
		mCommand=desc;
		if(color!=null){
			mColor=color;
		}
	}
	
	public String getmColor() {
		return mColor;
	}

	public void setmColor(String mColor) {
		this.mColor = mColor;
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
	public void setColor(String c){
		this.mColor = c;
	}
	public void setSpans(Integer s){
		this.mSpans=s;
	}
	public void setName(String n){
		this.mTitle = n;
	}
	
}
