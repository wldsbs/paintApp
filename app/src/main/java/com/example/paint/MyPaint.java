package com.example.paint;

import android.graphics.Paint;
import java.util.ArrayList;

public class MyPaint {
     private int startX,startY,shape;
     private int stopX, stopY =-1;
     private Paint paint;


        MyPaint(int startX, int startY, int stopX, int stopY, int shape, Paint paint){
            this.startX = startX;
            this.startY = startY;
            this.stopX = stopX;
            this.stopY = stopY;
            this.shape = shape;
            this.paint = paint;
        }

        public int getShape(){
            return shape;
        }

        public void setShape(int shape){
            this.shape = shape;
        }

        public double getDistance(int x1,int x2,int y1,int y2){
            return (Math.sqrt((Math.pow((x2-x1),2))+(Math.pow((y2-y1), 2))));
        }

        public boolean isOnLine(int x,int y){
            // (start)A----B---C(stop) 와같은 상황
            double AB = getDistance(startX,stopX,startY,stopY);
            double BC = getDistance(x,stopX,y,stopY);
            double AC = getDistance(startX,x,startY,y);

            if(AC + BC <= AB + 5 && AC + BC >= AB - 5){
                return true;
            }
            else if(AB == 0 || BC == 0){ //시작점이나 끝 점 위에 있다.
                return true;
            }
            else return false;
        }

        public boolean isOnLine(int x,int y,int startX,int startY,int stopX,int stopY){
            // (start)A----B---C 와같은 상황
            double AB = getDistance(startX,stopX,startY,stopY);
            double BC = getDistance(x,stopX,y,stopY);
            double AC = getDistance(startX,x,startY,y);

            if(AC + BC <= AB + 1 && AC + BC >= AB - 1){ // 사용자 편의성을 위한 오차범위 +-1
                return true;
            }
            else if(AB == 0 || BC == 0){ //시작점이나 끝 점 위에 있다.
                return true;
            }
            else return false;
        }

        public boolean isOnCircle(int x,int y){
            int disX = (startX - stopX);
            int disY = (startY - stopY);
            int r = (int)Math.sqrt(Math.pow(disX,2)+Math.pow(disY,2)); //도형의 반지름

            int mydisX = x-stopX;
            int mydisY = y-stopY;
            int myr = (int)Math.sqrt(Math.pow(mydisX,2)+Math.pow(mydisY,2)); //내가 선택한 점과 원 중심 사이의 거리

            if(myr <= r + 10 && myr >= r - 10){ // 사용자 편의성을 위한 오차범위 +-10
                return true;
            }
            else return false;
        }

        public int getStartX() {
            return startX;
        }

        public void setStartX(float x) {
            this.startX = (int)x;
        }

        public int getStartY() {
            return startY;
        }

        public void setStartY(float y) {
            this.startY = (int)y;
        }

        public int getStopX() {
            return stopX;
        }

        public void setStopX(float x) {
            this.stopX = (int)x;
        }

        public int getStopY() {
            return stopY;
        }

        public void setStopY(float Y) {
            this.stopY = (int)Y;
        }

        public float getDisX(){
            return (float) Math.sqrt(Math.pow(startX-stopX,2));
        }

        public float getDisY(){
            return (float) Math.sqrt(Math.pow(startY-stopY,2));
        }

        public Paint getPaint(){return paint;}

        public void setPaint(Paint paint){this.paint = paint;}

        boolean isOnText(int x, int y,String text){
            int h = (int) paint.descent() + (int)paint.ascent() ;
            int w = (int) paint.measureText(text);

            if(x >= startX && x <= startX + w){
                if(y - h >= startY && y<= startY){ //start좌표가 아래에서 시작함..
                    return true;
                }
            }
            return false;
        }
}

