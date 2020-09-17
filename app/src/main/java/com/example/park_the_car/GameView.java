package com.example.park_the_car;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class GameView extends View {

    private Path path = new Path();
    private Paint brush = new Paint();
    private Bitmap finish,start,car,coin,tree;
    private OnCoordinateUpdate mCoordinatesListener;
    public Canvas canvas2;

    private boolean isStart = false;
    private boolean carMoves = false;
    private boolean coin1Touched = false;
    private boolean coin2Touched = false;
    private boolean treeTouched = false;

    private float posX,posY;
    private int count = 0;

    public GameView(Context context) {
        super(context);
        setWillNotDraw(false);
        init(null);
    }

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        init(attrs);
    }

    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        init(attrs);
    }

    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setWillNotDraw(false);
        init(attrs);
    }

    public void init(AttributeSet set){
        finish = BitmapFactory.decodeResource(getResources(),R.drawable.ic_finish);
        start = BitmapFactory.decodeResource(getResources(),R.drawable.ic_start);
        car = BitmapFactory.decodeResource(getResources(),R.drawable.ic_car);
        coin = BitmapFactory.decodeResource(getResources(),R.drawable.ic_coin);
        tree = BitmapFactory.decodeResource(getResources(),R.drawable.ic_tree);
        brush.setAntiAlias(true);
        brush.setColor(Color.YELLOW);
        brush.setStyle(Paint.Style.STROKE);
        brush.setStrokeJoin(Paint.Join.ROUND);
        brush.setStrokeWidth(50f);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float pointX = event.getX();
        float pointY = event.getY();

        if(count < 1) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (pointX > (getWidth() / 2) - (start.getWidth() / 2) && pointX < (getWidth() / 2) + (start.getWidth() / 2) && pointY < getHeight() && pointY > getHeight() - start.getHeight()-10) {
                        path.moveTo(pointX, pointY);
                        isStart = true;
                    } else {
                        isStart = false;
                    }
                    return true;
                case MotionEvent.ACTION_MOVE:
                    if (isStart) {
                        path.lineTo(pointX, pointY);
                    }
                    if (mCoordinatesListener != null) {
                        mCoordinatesListener.onUpdate(pointX, pointY, false, false);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (pointX > (getWidth() / 2) - (finish.getWidth() / 2) && pointX < (getWidth() / 2) + (finish.getWidth() / 2) && pointY < finish.getHeight()-10) {
                        if (mCoordinatesListener != null) {
                            mCoordinatesListener.onUpdate(pointX, pointY, true, false);
                        }
                        count += 1;
                    } else {
                        if (mCoordinatesListener != null) {
                            mCoordinatesListener.onUpdate(pointX, pointY, true, true);
                        }
                        path = new Path();
                        Paint clearPaint = new Paint();
                        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                    }
                    break;
                default:
                    return false;
            }
                postInvalidate();

        }
        return false;
    }

    public void setPathLine(float x , float y) {

        if(count == 1 ) {
            posX = x - (car.getWidth() / 2);
            posY = y - (car.getHeight() / 2);
            carMoves = true;
            invalidate();
        }
    }

    public float canvasWidth(){
        return getWidth();
    }

    public float canvasHeight(){
        return getHeight();
    }

    public float coinWidth(){
        return coin.getWidth();
    }

    public float coinHeight(){
        return coin.getHeight();
    }

    public float treeWidth(){
        return tree.getWidth()-20;
    }

    public float treeHeight(){
        return tree.getHeight()-10;
    }

    public float carHeight(){
        return car.getHeight();
    }

    public void setCoin1Touched(){
        coin1Touched = true;
    }

    public void setCoin2Touched(){
        coin2Touched = true;
    }

    public void setTreeTouched(){
        treeTouched = true;
        count = 2;
    }

    public void resetGame(){
        isStart = false;
        carMoves = false;
        coin1Touched = false;
        coin2Touched = false;
        treeTouched = false;
        count = 0;
        path = new Path();
        Paint clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        postInvalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas2 = canvas;
        canvas.drawPath(path,brush);
        canvas.drawBitmap(finish,(getWidth()/2)-(finish.getWidth()/2),0,null);

        if(!carMoves) {
            posX = (getWidth()/2)-(car.getWidth()/2);
            posY = getHeight()-car.getHeight();
        }

        canvas.drawBitmap(car, posX, posY, null);
        canvas.drawBitmap(start,(getWidth()/2)-(start.getWidth()/2),getHeight()-start.getHeight(),null);

        if(!coin1Touched){
            canvas.drawBitmap(coin, 150, (getHeight()/2)-200, null);
        }

        if(!coin2Touched){
            canvas.drawBitmap(coin, getWidth()-coin.getWidth()-150, (getHeight()/2)+200, null);
        }

        canvas.drawBitmap(tree, (getWidth()/2)-tree.getWidth()-50, (getHeight()/2)-400, null);
        canvas.drawBitmap(tree, (getWidth()/2)+50, (getHeight()/2)+400, null);
    }

    public void setCoordinatesListener(OnCoordinateUpdate listener) {
        mCoordinatesListener = listener;
    }

    public interface OnCoordinateUpdate {
        void onUpdate(float X,float Y,boolean isMove,boolean notFinish);
    }
}
