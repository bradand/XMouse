package com.stripe1.xmouse;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class myDialogFragment extends DialogFragment {
	
	
	private Button positiveButton;
	
    static String layoutText;
	Context mCont;

	
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View myView = inflater.inflate(R.layout.dialog_edit_keyboard, null);
        final EditText thisText = (EditText) myView.findViewById(R.id.dialog_keys_text);
        thisText.setText(layoutText);
        	
    		builder.setView(myView).setMessage("Edit Layout");
        	builder
            // Add action buttons
                   .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int id) {
                           // sign in the user ...
                    	   
                    	   //initScoreCard();
	    					layoutText = thisText.getText().toString();
                    	   mListener.onDialogPositiveClickValid(myDialogFragment.this,layoutText);
                       }
                   })
                   .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                    	   //myDialogFragment.this.getDialog().cancel();
                    	   mListener.onDialogNegativeClick(myDialogFragment.this);
                       }
                   });  
        
        AlertDialog ad = builder.create();
        
        ad.setOnShowListener(new OnShowListener(){

			@Override
			public void onShow(DialogInterface dialog) {
				// TODO Auto-generated method stub
				positiveButton = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE);
			}
        });
        
        return ad;
    }
    public Context getmCont() {
		return mCont;
	}

	public void setmCont(Context mCont) {
		this.mCont = mCont;
	}
	/* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onDialogPositiveClickValid(myDialogFragment dialog, String layoutText);
        
        public void onDialogNegativeClick(myDialogFragment dialog);
        
    }
    
    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;
    
    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
	public void setText(String keyboardTextValue) {
		layoutText = keyboardTextValue;
		
	}
}