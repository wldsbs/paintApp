package com.example.paint;

import android.graphics.Bitmap;
import android.graphics.Paint;

public class Mypicture {

    int picX,picY ;
    Bitmap picture;
    Paint paint;

    Mypicture(int picX, int picY, Bitmap picture, Paint paint){
        this.picX = picX;
        this.picY = picY;
        this.picture = picture;
        this.paint = paint;
    }

    int getPicX(){
        return picX;
    }

    int getPicY(){
        return picY;
    }

    void setPicX(int x){
        this.picX = x;
    }

    void setPicY(int Y){
        this.picY = Y;
    }

    void setPaint(Paint paint){
        this.paint = paint;
    }

    Paint getPaint(){
        return paint;
    }

    Bitmap getPicture(){
        return picture;
    }

    void setPicture(Bitmap picture){
        this.picture = picture;
    }

    boolean isOnPic(int x, int y){
        int w = picture.getWidth();
        int h = picture.getHeight();

        if(x >= picX && x <= picX +w){
            if(y >= picY && y<= picY +h){
                return true;
            }
        }
        return false;
    }

}
