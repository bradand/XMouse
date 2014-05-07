package com.stripe1.xmouse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.stripe1.xmouse.tasks.SshScpTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;



public class MouseSectionFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
	OnXMouseClickListener mCallbackClick;
	OnXMouseMoveListener mCallbackMove;
	enum ClickType {
		
		Standard,
		Drag_Down,
		Drag_up
	}
    // Container Activity must implement this interface
    public interface OnXMouseClickListener {
        public void OnXMouseClicked(ClickType dragUp);
        
    }
    // Container Activity must implement this interface
    public interface OnXMouseMoveListener {
        public void OnXMouseMoved(float dx,float dy,boolean scrolling);
        
    }

	
    public static final String ARG_SECTION_NUMBER = "section_number";
    private static Paint       mPaint;
    //private static MaskFilter  mEmboss;
    //private static MaskFilter  mBlur;
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallbackClick = (OnXMouseClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnXMouseClickListener");
        }
     // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallbackMove = (OnXMouseMoveListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnXMouseMoveListener");
        }
    }
	public MouseSectionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	
        View rootView = inflater.inflate(R.layout.mouse_layout, container, false);
        //TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
        //dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
        RelativeLayout V = (RelativeLayout) rootView.findViewById(R.id.mouse_touchpad);
        LinearLayout Vm = (LinearLayout) rootView.findViewById(R.id.mouse_area);
        MyView mV = new MyView(getActivity());
        mV.setBackgroundColor(Color.LTGRAY);
        
        
        Vm.addView(mV);
        //MyView myV = (MyView) rootView.findViewById(R.id.myView);
        //myV=new MyView(getActivity());
        
        
        //return new MyView(getActivity());
        return V;
    }
    
    public class MyView extends View {
    	
        //private static final float MINP = 0.25f;
        //private static final float MAXP = 0.75f;

        private Bitmap  mBitmap;
        //private Canvas  mCanvas;
        private Path    mPath;
        private Paint   mBitmapPaint;
        private float scrollStart = 90f; //pixels to draw from left, for scroll bar
        static final int CLICK = 3;
        private int w=0;
        private int h=0;
        Canvas canvas;
        PointF start = new PointF();
        long downStart = 0;
        boolean dragging = false;
        boolean draggable = true;
        public MyView(Context c) {
            super(c);
            
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
           
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            

            //mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6, 3.5f);

            //mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
           this.w=w;
           this.h=h;
            //mCanvas = new Canvas(mBitmap);
        }

        @Override
        protected void onDraw(Canvas c) {
            //canvas.drawColor(0xFFAAAAAA);
            //canvas.drawColor(0xFFAAAA4C);
        	canvas=c;
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            mPaint.setColor(Color.WHITE);
            mPaint.setStrokeWidth(3);
            canvas.drawLine(w-scrollStart, h*0.1f, w-scrollStart, h*0.9f, mPaint);
            
            //mPaint.setStrokeWidth(1);
            //mPaint.setTextSize(20);
            //canvas.drawText(String.valueOf(scrolling), 20, 20, mPaint);
            //canvas.drawText(mX+","+mY, 20, 40, mPaint);
                        
            if(dragging){
            	mPaint.setColor(Color.YELLOW);
            }else{
            	mPaint.setColor(0xFFFF0000);//red
            	
            }
            
            mPaint.setStrokeWidth(12);
            canvas.drawPath(mPath, mPaint);
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;
        private boolean scrolling = false;
        private void touch_start(float x, float y) {
        	if((w-x)<=scrollStart){
            	
            	scrolling=true;
            	 x = w-scrollStart/2;
            	 mPaint.setPathEffect(new DashPathEffect(new float[] {10,20}, 0));
            	 mPaint.setStrokeWidth(24);
            	 
            }else{
            	scrolling=false;
            	mPaint.setPathEffect(null);
            	 mPaint.setStrokeWidth(12);
            }
        	
        	mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
            
        }
        private void touch_move(float x, float y) {
            
        	if(scrolling){
        		 x = w-scrollStart/2;
        	}
        	
            
            float rx = x-mX;
            float ry = y-mY;
            float dx = Math.abs(rx);
            float dy = Math.abs(ry);
            
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;
                
                //addTextToTextView("xdotool mousemove x="+x+" y="+y);
                mCallbackMove.OnXMouseMoved(rx,ry,scrolling);
            }
        }
        private void touch_up() {
            mPath.lineTo(mX, mY);
            // commit the path to our offscreen
            //mCanvas.drawPath(mPath, mPaint);
            // kill this so we don't double draw
            mPath.reset();
            scrolling=false;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            PointF curr = new PointF(event.getX(), event.getY());
            
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                	
                    start.set(curr);
                    downStart = System.currentTimeMillis();
                	touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                	int xDiff1 = (int) Math.abs(curr.x - start.x);
                    int yDiff1 = (int) Math.abs(curr.y - start.y);
                    
                    //Log.d("onTouchEvent",xDiff1+" "+yDiff1+" "+CLICK);
                    long thisTime = System.currentTimeMillis()-downStart;
                    if (xDiff1 < CLICK*7 && yDiff1 < CLICK*7){
                    	//Log.d("onTouchEvent","move inside click");
                    	if(draggable && dragging==false && thisTime>350){
                    		//Log.d("onTouchEvent","start drag");
	                		mCallbackClick.OnXMouseClicked(ClickType.Drag_Down);
	                		dragging=true;
	                	}
                    }else{
                    	//Log.d("onTouchEvent","move outside click");
                    	draggable=false;
                    }
                	touch_move(x, y);
                	
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    
                	int xDiff = (int) Math.abs(curr.x - start.x);
                    int yDiff = (int) Math.abs(curr.y - start.y);
                    if (xDiff < CLICK && yDiff < CLICK){
                    	Log.d("onTouchEvent","up click");
                    	mCallbackClick.OnXMouseClicked(ClickType.Standard);
                    }else{
                    	//Log.d("onTouchEvent","up outside click");
                    }
                    if(dragging){
                    	dragging=false;
                    	mCallbackClick.OnXMouseClicked(ClickType.Drag_up);
                    }
                    draggable=true;
                	touch_up();
                    invalidate();
                    
                    /*if(MainActivity.setting_mouse_background){
                    	if(MainActivity.session!=null){
                    		if(MainActivity.session.isConnected()){
                    			
		                    	SshScpTask scp = new SshScpTask(getActivity(),"100","100"){
		            				protected void onPostExecute(String result) {
		            					Log.d("SshScpTask","onPostExecute");
		            					try {
											//FileInputStream fis = getActivity().openFileInput("mouse_bg.jpg");
											Bitmap mBitmap = BitmapFactory.decodeFile(getActivity().getFilesDir().getAbsoluteFile()+"/mouse_bg.jpg");
											//Bitmap mBitmap = Bitmap.createScaledBitmap(Bitmap src, int dstWidth, int dstHeight, boolean filter);
											canvas = new Canvas(mBitmap.copy(Bitmap.Config.ARGB_8888, true));
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
		            				}
		            			};
		            			scp.execute("");
                    		}
                    	}
                    }*/
                    break;
            }
            return true;
        }
    }
}
