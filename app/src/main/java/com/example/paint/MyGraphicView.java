package com.example.paint;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import java.util.ArrayList;

public class MyGraphicView extends View implements View.OnTouchListener{
    //그래픽 요소를 저장하는 리스트
    ArrayList<MyPaint> paints = new ArrayList<MyPaint>();
    ArrayList<Mypicture> pics = new ArrayList<Mypicture>();

    final int LINE = 1, SQUARE = 2, CIRCLE = 3,TEXT=4, ERASER=5 ,FINGER = 6;
    int startX= -1, startY = -1, stopX = -1, stopY = -1, curX=-1, curY=-1;
    int sttX,sttY,stpX,stpY;
    int oldPX=1,oldPY=1;

    //그리기 옵션의 초기값
    private int color = Color.BLACK; // 선 색
    private int lineWith = 10; // 선 두께
    int curShape = LINE;
    String text = "text";
    int index= -1; //FOCUS된 객체의 인덱스
    int focusImg = -1;
    int brightness =50, blurness=0 ;//필터링 정도

    Context context;


    // View 를 Xml 에서 사용하기 위해선 3가지 생성자를 모두 정의 해주어야함
    // 정의 x -> Runtime Error
    public MyGraphicView(Context context) {
        super(context);
        this.setOnTouchListener(this);
        this.context = context;
    }
    public MyGraphicView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(this);
        this.context = context;
    }
    public MyGraphicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOnTouchListener(this);
        this.context = context;
    }

    public boolean onTouch(View view,MotionEvent event) {
        if (curShape == ERASER) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    curX = (int) event.getX();
                    curY = (int) event.getY();
                    erase();
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return true; //계속 터치 메소드 발생
        } else if (curShape == FINGER) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    curX = (int) event.getX();
                    curY = (int) event.getY();
                    startX = curX;
                    startY = curY;
                    index = findOn();
                    focusImg = findPicOn();

                    if (index != -1) {
                        sttX =  paints.get(index).getStartX();
                        sttY =  paints.get(index).getStartY();
                        stpX =  paints.get(index).getStopX();
                        stpY =  paints.get(index).getStopY();
                    }
                    else if (focusImg != -1) {
                        oldPX = pics.get(0).getPicX();
                        oldPX = pics.get(0).getPicY();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    curX = (int) event.getX();
                    curY = (int) event.getY();

                    int addX = startX - curX;
                    int addY = startY - curY;

                    if (index == -1) { //객체를 못찾음
                    } else { //내 좌표 cur에 paint객체가 있다.
                        paints.get(index).setStartX(sttX - addX);
                        paints.get(index).setStartY(sttY - addY);
                        paints.get(index).setStopX(stpX - addX);
                        paints.get(index).setStopY(stpY - addY);

                        invalidate();
                        break;
                    }
                    //-----------
                    if (focusImg == -1) {
                        break;
                    }
                    else {
                        pics.get(focusImg).setPicX(oldPX-addX);
                        pics.get(focusImg).setPicY(oldPY-addY);
                        invalidate();
                        break;
                    }
                case MotionEvent.ACTION_UP:
                    index = -1;
                    focusImg = -1;
                    break;
            }
        } else {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = (int) event.getX();
                    startY = (int) event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    Paint paint = new Paint();
                    paint.setColor(color);
                    paint.setStrokeWidth(lineWith);
                    paint.setStyle(Paint.Style.STROKE);
                    stopX = (int) event.getX();
                    stopY = (int) event.getY();
                    paints.add(new MyPaint(startX, startY, stopX, stopY, curShape, paint));
                    invalidate();
                    break;
            }//endof nomalmode
        }//endof nomal else
        return true; //계속실행
    }

    protected void onDraw(Canvas canvas) {
        if(!pics.isEmpty()) {//이미지를 먼저 출력한다
            for (int i = 0; i < pics.size(); i++) {
                canvas.drawBitmap(pics.get(i).getPicture(), pics.get(i).getPicX(), pics.get(i).getPicY(), pics.get(i).getPaint());
            }
        }
        if(!paints.isEmpty()) {//이미지 출력 위에 페인트를 출력한다.
            for (int i = 0; i < paints.size(); i++) {
                switch (paints.get(i).getShape()) {
                    case LINE:
                        canvas.drawLine(paints.get(i).getStartX(), paints.get(i).getStartY()
                                , paints.get(i).getStopX(), paints.get(i).getStopY(), paints.get(i).getPaint());
                        break;

                    case SQUARE:
                        canvas.drawRect(paints.get(i).getStartX(), paints.get(i).getStartY()
                                , paints.get(i).getStopX(), paints.get(i).getStopY(), paints.get(i).getPaint());
                        break;
                    case CIRCLE:
                        float disX = paints.get(i).getStopX() - paints.get(i).getStartX(); //x좌표 거리차
                        float disY = paints.get(i).getStopY() - paints.get(i).getStartY(); //y좌표 거리차
                        int radius = (int)  Math.sqrt(Math.pow(disX,2)+Math.pow(disY,2));
                        canvas.drawCircle(paints.get(i).getStopX(), paints.get(i).getStopY(),radius,paints.get(i).getPaint());
                        break;
                    case TEXT:
                        canvas.drawText(text,paints.get(i).getStartX(),paints.get(i).getStartY(),paints.get(i).getPaint());
                        break;

                }
            }
        }
    }

    public void reset() {
        paints.clear(); // PaintPoint ArrayList Clear
        pics.clear();
        invalidate(); // 화면을 갱신함 -> onDraw()를 호출
    }

    public void erase() {
        if(!paints.isEmpty()) {
            for (int i = 0; i < paints.size(); i++) { //이미 그려진 객체들을 다 순회하여 확인
                int startX = (int)paints.get(i).getStartX();
                int stopX = (int)paints.get(i).getStopX();
                int startY = (int)paints.get(i).getStartY();
                int stopY = (int)paints.get(i).getStopY();

                switch (paints.get(i).getShape()) {
                    case LINE:
                        //cur점이 두 점사이를 잇는 직선 위에 있는것인지 확인
                        if (paints.get(i).isOnLine(curX, curY)) {
                            paints.remove(i);
                            invalidate(); //ondraw호출
                            return;
                        }
                        break;

                    case SQUARE:
                        if (paints.get(i).isOnLine(curX, curY,startX,startY,stopX,startY)) {//사각형의 윗 변과 만나나?
                            paints.remove(i);
                            invalidate(); //ondraw호출
                            return;
                        }
                        else if(paints.get(i).isOnLine(curX,curY,stopX,stopY,stopX,startY)) { //사각형 오른쪽변
                            paints.remove(i);
                            invalidate(); //ondraw호출
                            return;
                        }
                        else if (paints.get(i).isOnLine(curX,curY,stopX,stopY,startX,stopY)) {//사각형 아랫변
                            paints.remove(i);
                            invalidate(); //ondraw호출
                            return;
                        }
                        else if (paints.get(i).isOnLine(curX,curY,startX,startX,startX,stopY)) {//사각형 아랫변
                            paints.remove(i);
                            invalidate(); //ondraw호출
                            return;
                        }
                        break;
                    case CIRCLE:
                        if (paints.get(i).isOnCircle(curX, curY)) {
                            paints.remove(i);
                            invalidate(); //ondraw호출
                            return;
                        }
                        break;
                    case TEXT :
                        if (paints.get(i).isOnText(curX,curY,text)){
                            paints.remove(i);
                            invalidate();
                            return;
                        }
                        break;
                }
            }
        }
        //paint 객체가 위에 그려지므로 먼저 해당 좌표에 존재하면 우선적으로 지운다.
        if(!pics.isEmpty()){
            for (int i = pics.size()-1; i >= 0; i--) { //이미 그려진 객체들을 다 순회하여 확인
                int X = (int) pics.get(i).getPicX();
                int Y = (int) pics.get(i).getPicY();
                if(pics.get(i).isOnPic(curX,curY)){
                    pics.remove(i);
                    invalidate();
                    return;
                }
            }
        }
    }

    public int findOn(){
        if (!paints.isEmpty()) {
            for (int i = 0; i < paints.size(); i++) { //이미 그려진 객체들을 다 순회하여 확인
                int startX = (int) paints.get(i).getStartX();
                int stopX = (int) paints.get(i).getStopX();
                int startY = (int) paints.get(i).getStartY();
                int stopY = (int) paints.get(i).getStopY();

                switch (paints.get(i).getShape()) {
                    case LINE:
                        //cur점이 두 점사이를 잇는 직선 위에 있는것인지 확인
                        if (paints.get(i).isOnLine(curX, curY)) {
                            return i;
                        }
                        break;

                    case SQUARE:
                        if (paints.get(i).isOnLine(curX, curY, startX, startY, stopX, startY)) {//사각형의 윗 변과 만나나?
                            return i;
                        } else if (paints.get(i).isOnLine(curX, curY, stopX, stopY, stopX, startY)) { //사각형 오른쪽변
                            return i;
                        } else if (paints.get(i).isOnLine(curX, curY, stopX, stopY, startX, stopY)) {//사각형 아랫변
                            return i;
                        } else if (paints.get(i).isOnLine(curX, curY, startX, startX, startX, stopY)) {//사각형 아랫변
                            return i;
                        }
                        break;
                    case CIRCLE:
                        if (paints.get(i).isOnCircle(curX, curY)) {
                            return i;
                        }
                        break;
                    case TEXT :
                        if (paints.get(i).isOnText(curX,curY,text)){
                            return i;
                        }
                }
            }
        }
        return -1; //못찾은 경우
    }

    public int findPicOn(){
        if(!pics.isEmpty()){
            for (int i = pics.size()-1; i >= 0; i--) { //이미 그려진 객체들을 다 순회하여 확인
                int X = (int) pics.get(i).getPicX();
                int Y = (int) pics.get(i).getPicY();
                if(pics.get(i).isOnPic(curX,curY)){
                    return i;
                }
            }
        }
        return -1;
    }

    public View getView(){
        return this;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getLineWith() {
        return lineWith;
    }

    public void setLineWith(int lineWith) {
        this.lineWith = lineWith;
    }

    public void setMode(int curShape) {
        this.curShape = curShape;
    }

    public void setTEXT(String text){
        this.text = text;
        Paint paint = new Paint();
        paint.setTextSize(70);
        paint.setColor(color);
        paints.add(new MyPaint(500,500,50,50,TEXT,paint));
        invalidate();
    }

    public String getText(){
        return text;
    }

    public void addImage(Bitmap picture){
        int picX = (this.getWidth() - picture.getWidth()) / 2 ;
        int picY = (this.getHeight() - picture.getHeight()) / 2 ;
        pics.add(new Mypicture(picX,picY,picture,null));
        invalidate();
    }

    public void setBrightness(int brightness){
        brightness -= 50;
        this.brightness = brightness;
        if (!pics.isEmpty()) setFilter();
    }

    public void setBlurness(int blurness){
        this.blurness = blurness;
        if (!pics.isEmpty()) setFilter();
    }

    public void setFilter (){
        if(!pics.isEmpty()) {//이미지를 먼저 출력한다
            Paint paint = new Paint();
            BlurMaskFilter bMask;
            if(blurness != 0) { //0이 아닐때만
                bMask = new BlurMaskFilter(blurness, BlurMaskFilter.Blur.NORMAL);
                paint.setMaskFilter(bMask);
            }

            float[] array = { 1,0,0,0,brightness,
                    0,1,0,0,brightness,
                    0,0,1,0,brightness,
                    0,0,0,1,0} ;
            ColorMatrix cm = new ColorMatrix(array);


            paint.setColorFilter(new ColorMatrixColorFilter(cm));
            for (int i = 0; i < pics.size(); i++) {
                pics.get(i).setPaint(paint);
            }
            invalidate();
        }
    }
}
