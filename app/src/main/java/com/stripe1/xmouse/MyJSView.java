package com.stripe1.xmouse;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MyJSView extends View {
    private Paint base;
    private Paint dead_zone;
    private Paint hat;

    private int cx;
    private int cy;

    private int hx;
    private int hy;

    private int cs;
    private int dzs;
    private int hs;

    private float reX;
    private float reY;

    private float sensitivity;


    public MyJSView(Context c) {
        super(c);
        
        base = new Paint();
        base.setAntiAlias(true);
        base.setColor(Color.BLACK);
        base.setStrokeWidth(3);
        base.setStyle(Paint.Style.STROKE);
        base.setStrokeJoin(Paint.Join.MITER);
        base.setStrokeCap(Paint.Cap.SQUARE);
        
        dead_zone = new Paint();
        dead_zone.setAntiAlias(true);
        dead_zone.setColor(Color.GRAY);
        dead_zone.setStrokeWidth(1);
        dead_zone.setStyle(Paint.Style.FILL_AND_STROKE);
        dead_zone.setStrokeJoin(Paint.Join.MITER);
        dead_zone.setStrokeCap(Paint.Cap.SQUARE);
        
        hat = new Paint();
        hat.setAntiAlias(true);
        hat.setColor(Color.BLACK);
        hat.setStrokeWidth(3);
        hat.setStyle(Paint.Style.FILL_AND_STROKE);
        hat.setStrokeJoin(Paint.Join.MITER);
        hat.setStrokeCap(Paint.Cap.SQUARE);

        sensitivity=MainActivity.setting_js_sensitivity;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cx=getWidth()/2;
        cy=getHeight()/2;
        hx=cx;
        hy=cy;
        int m=Math.min(w,h);
        cs= (int) (m*MainActivity.setting_js_size);
        dzs= (int) (m*MainActivity.setting_js_dead_zone);
        hs=(cs+dzs)/2;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);

        canvas.drawRect(cx-cs/2, cy-cs/2, cx+cs/2, cy+cs/2,base);
        canvas.drawRect(cx-dzs/2, cy-dzs/2, cx+dzs/2, cy+dzs/2,dead_zone);
        canvas.drawRect(hx-hs/2, hy-hs/2, hx+hs/2, hy+hs/2,hat);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Log.d("js", event.toString());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                int ex= (int) event.getX();
                int ey= (int) event.getY();

                int Mx=cx+cs/2;
                int mx=cx-cs/2;
                int My=cy+cs/2;
                int my=cy-cs/2;
                
                int x=ex>Mx?Mx:ex<mx?mx:ex;
                int y=ey>My?My:ey<my?my:ey;


                int Mdx=cx+dzs/2;
                int mdx=cx-dzs/2;
                int Mdy=cy+dzs/2;
                int mdy=cy-dzs/2;

                boolean dz=x<Mdx&&x>mdx&&y<Mdy&&y>mdy;
                hx=dz?cx:x;
                hy=dz?cy:y;

                jsmove();
                invalidate();
                
                break;
            case MotionEvent.ACTION_UP:
                hx=cx;
                hy=cy;

                invalidate();

                break;
        }

        return true;
    }


    public void jsmove() {

        float dx=((float) hx-cx)/cs*MainActivity.setting_js_sensitivity*100;
        float dy=((float) hy-cy)/cs*MainActivity.setting_js_sensitivity*100;

        dx += reX;
        dy += reY;
        reX = dx - Math.round(dx);
        reY = dy - Math.round(dy);
        dx -= reX;
        dy -= reY;

        String cmd="";
        if(dx <0 || dy <0){
            cmd="xdotool mousemove_relative -- "+dx+" "+dy;
        }else{
            cmd="xdotool mousemove_relative "+dx+" "+dy;
        }
        MainActivity.conn.executeShellCommand(cmd);
    }
}
