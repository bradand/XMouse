package com.stripe1.xmouse;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by bradley on 12/15/15.
 */
public class MyMouseView extends View {
    //TODO: two finger scroll, pinch to zoom

    //private final float MINP = 0.25f;
    //private final float MAXP = 0.75f;
    private Paint mPaint;
    private Bitmap mBitmap;
    //private Canvas  mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    private float scrollStart = 120f; //pixels to draw from left, for scroll bar
    final int CLICK = 3;
    private int w=0;
    private int h=0;
    Canvas canvas;
    PointF start = new PointF();
    long downStart = 0;
    boolean dragging = false;
    boolean draggable = true;
    boolean touching = false;


    enum ClickType {

        Left_click,
        Right_click,
        Drag_Down,
        Drag_up,
        Zoom_in,
        Zoom_out
    }

    public MyMouseView(Context c) {
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

        //Log.d("MyMouseView", String.valueOf(w + ',' + h));
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

        mPaint.setStrokeWidth(10);
        canvas.drawPath(mPath, mPaint);

        if(touching) {
            mPaint.setColor(Color.BLACK);
            if(dragging) {
                mPaint.setStrokeWidth(10);
            }else{
                mPaint.setStrokeWidth(3);
            }
            if(!scrolling && !zooming) {
                canvas.drawCircle(mX, mY, 80, mPaint);
            }
            if(zooming || scrolling){


                canvas.drawCircle(curr.x, curr.y, 80, mPaint);

                if(yX<scrollStart || twoFingerScroll) {
                    canvas.drawCircle(yX, yY, 80, mPaint);

                }
            }
        }
    }

    private boolean twoFingerScroll = false;
    private int zoomCounter = 0;
    private int zoomOverFlow = 10;
    private float mX=0, mY=0, yX=scrollStart, yY=0;
    private final float TOUCH_TOLERANCE = 4;
    private final float SCROLL_TOLERANCE = 20;
    private boolean scrolling = false;
    private double scaleFactor = 1;
    private boolean firstTouch= true;
    private boolean zooming =false;
    private double dist = 0;
    private PointF curr;

    private void touch_start(float x, float y) {
        touching=true;
        if((w-x)<=scrollStart){

            scrolling=true;
            x = w-scrollStart/2;
            mPaint.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));


        }else{
            scrolling=false;
            mPaint.setPathEffect(null);

        }

        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;

    }
    private void touch_move(float x, float y) {

        if (scrolling) {
            x = w - scrollStart / 2;
        }


        float rx = x - mX;
        float ry = y - mY;
        float dx = Math.abs(rx);
        float dy = Math.abs(ry);


        if (!zooming) {

            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;

                //addTextToTextView("xdotool mousemove x="+x+" y="+y);

                OnXMouseMoved(rx, ry, scrolling);
            }
        }else{
            //zooming

            mPath.reset();
            mPath.setLastPoint(curr.x,curr.y);
            mPath.lineTo(yX,yY);
            //Log.d("current points","curr.x="+curr.x+" curr.y="+curr.y+" yX="+yX+"yY="+yY);

        }

    }
    private void touch_up() {
        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        //mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPath.reset();
        scrolling=false;
        touching=false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        curr = new PointF(event.getX(), event.getY());
        double newDist = 0;
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

                if (event.getPointerCount() > 1) {

                    //more than 1 finger
                    yX = (float) MotionEventCompat.getX(event, 1);
                    yY = (float) MotionEventCompat.getY(event, 1);

                    newDist = Math.sqrt(Math.pow(yX - x, 2) + Math.pow(yY - y, 2));
                    if(firstTouch){
                        dist=newDist;
                        firstTouch=false;
                    }

                    scaleFactor = (newDist-dist)/dist;
                    //Log.d("diffmon","newDist="+newDist+" scalefactor=" + scaleFactor);

                    if(Math.abs(scaleFactor)>0.08){
                        zooming=true;
                        zoomCounter++;
                        if(zoomCounter>zoomOverFlow) {

                            if (scaleFactor > 0) {
                                OnXMouseClicked(ClickType.Zoom_in);
                            } else {
                                OnXMouseClicked(ClickType.Zoom_out);
                            }
                            zoomCounter=0;
                        }

                    }else if(!zooming){
                        twoFingerScroll=true;
                        scrolling=true;

                    }
                }

                //Log.d("onTouchEvent",xDiff1+" "+yDiff1+" "+CLICK);
                long thisTime = System.currentTimeMillis()-downStart;
                if (xDiff1 < CLICK*6 && yDiff1 < CLICK*6){
                    //Log.d("onTouchEvent","move inside click");
                    if(draggable && dragging==false && thisTime>350){
                        //Log.d("onTouchEvent","start drag");
                        OnXMouseClicked(ClickType.Drag_Down);
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
                    //Log.d("onTouchEvent", "up click");
                    if(scrolling){
                        OnXMouseClicked(ClickType.Right_click);
                    }else{
                        OnXMouseClicked(ClickType.Left_click);
                    }

                }else{
                    //Log.d("onTouchEvent","up outside click");
                }
                if(dragging){
                    dragging=false;
                    OnXMouseClicked(ClickType.Drag_up);
                }


                firstTouch=true;
                zooming=false;
                draggable=true;
                twoFingerScroll=false;
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


    public void OnXMouseMoved(float dx, float dy,boolean scroll) {

        dx=dx*MainActivity.setting_sensitivity;
        dy=dy*MainActivity.setting_sensitivity;
        String cmd="";
        if(dx <0 || dy <0){
            cmd="xdotool mousemove_relative -- "+dx+" "+dy;
            if(scroll){
                cmd="xdotool click 5";//mouse wheel
            }

        }else{
            cmd="xdotool mousemove_relative "+dx+" "+dy;
            if(scroll){
                cmd="xdotool click 4";//mouse wheel
            }
        }
        MainActivity.conn.executeShellCommand(cmd);
    }


    public void OnXMouseClicked(ClickType type) {
        String cmd ="";
        switch(type){
            case Left_click:
                cmd ="xdotool click 1";
                break;
            case Right_click:
                cmd ="xdotool click 3";
                break;
            case Drag_Down:
                cmd ="xdotool mousedown 1";
                break;
            case Drag_up:
                cmd ="xdotool mouseup 1";
                break;
            case Zoom_in:
                cmd="xdotool key Ctrl+plus";
                break;
            case Zoom_out:
                cmd="xdotool key Ctrl+minus";
                break;
            default:
                break;
        }
        MainActivity.conn.executeShellCommand(cmd);

    }
}
