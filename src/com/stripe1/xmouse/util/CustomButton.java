package com.stripe1.xmouse.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class CustomButton extends Button{
	public CustomButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	private int id;
	private String script;
	
	public int getDatabaseId() {
		return id;
	}
	public void setDatadaseId(int id) {
		this.id = id;
	}
	public String getScript() {
		return script;
	}
	public void setScript(String script) {
		this.script = script;
	}
}
