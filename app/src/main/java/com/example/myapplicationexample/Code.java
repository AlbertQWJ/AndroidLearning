package com.example.myapplicationexample;
//package uk.ac.tees.aad.a0547505.WeijieQIU;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

public class Code {

    private static final char[] CHARS = {
            '2', '3', '4', '5',  '7', '8',
            'a',  'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm',
            'n', 'p',  'r', 's',  'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B',  'D', 'E', 'F',  'H',  'J', 'K', 'M',
            'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    private static Code bmpCode;

    public static Code getInstance() {
        if(bmpCode == null)
            bmpCode = new Code();
        return bmpCode;
    }

    //default settings
    //The default number of random numbers of verification code
    private static final int DEFAULT_CODE_LENGTH = 4;
    //Default font size
    private static final int DEFAULT_FONT_SIZE = 25;
    //Default number of lines
    private static final int DEFAULT_LINE_NUMBER = 5;
    //padding value
    private static final int BASE_PADDING_LEFT = 10, RANGE_PADDING_LEFT = 15, BASE_PADDING_TOP = 15, RANGE_PADDING_TOP = 20;
    //Default width and height of captcha
    private static final int DEFAULT_WIDTH = 100, DEFAULT_HEIGHT = 40;

    //settings decided by the layout xml
    //canvas width and height
    private int width = DEFAULT_WIDTH, height = DEFAULT_HEIGHT;

    //random word space and pading_top
    private int base_padding_left = BASE_PADDING_LEFT, range_padding_left = RANGE_PADDING_LEFT,
            base_padding_top = BASE_PADDING_TOP, range_padding_top = RANGE_PADDING_TOP;

    //number of chars, lines; font size
    private int codeLength = DEFAULT_CODE_LENGTH, line_number = DEFAULT_LINE_NUMBER, font_size = DEFAULT_FONT_SIZE;

    //variables
    private String code;
    private int padding_left, padding_top;
    private Random random = new Random();
    //verification code picture
    public Bitmap createBitmap() {
        padding_left = 0;

        Bitmap bp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bp);

        code = createCode();

        c.drawColor(Color.WHITE);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(font_size);
        //Draw verification code
        for (int i = 0; i < code.length(); i++) {
            randomTextStyle(paint);
            randomPadding();
            c.drawText(code.charAt(i) + "", padding_left, padding_top, paint);
        }
        //Drawing lines
        for (int i = 0; i < line_number; i++) {
            drawLine(c, paint);
        }

//        c.save( Canvas.ALL_SAVE_FLAG );//Save
        c.save();//Save
        c.restore();//
        return bp;
    }

    public String getCode() {
        return code;
    }

    //Generate verification code
    private String createCode() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < codeLength; i++) {
            buffer.append(CHARS[random.nextInt(CHARS.length)]);
        }
        return buffer.toString();
    }
    //Draw interference lines
    private void drawLine(Canvas canvas, Paint paint) {
        int color = randomColor();
        int startX = random.nextInt(width);
        int startY = random.nextInt(height);
        int stopX = random.nextInt(width);
        int stopY = random.nextInt(height);
        paint.setStrokeWidth(1);
        paint.setColor(color);
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }
    //Generate random colors
    private int randomColor() {
        return randomColor(1);
    }

    private int randomColor(int rate) {
        int red = random.nextInt(256) / rate;
        int green = random.nextInt(256) / rate;
        int blue = random.nextInt(256) / rate;
        return Color.rgb(red, green, blue);
    }
    //Randomly generate text style, color, thickness, slant
    private void randomTextStyle(Paint paint) {
        int color = randomColor();
        paint.setColor(color);
        paint.setFakeBoldText(random.nextBoolean());  //true for bold, false for non-bold
        float skewX = random.nextInt(11) / 10;
        skewX = random.nextBoolean() ? skewX : -skewX;
        paint.setTextSkewX(skewX); //float type parameter, negative numbers are right-skewed, integers are left-skewed
        //paint.setUnderlineText(true); //true is underlined, false is non-underlined
        //paint.setStrikeThruText(true); //true for strikethrough, false for non-strikethrough
    }
    //Randomly generated padding values
    private void randomPadding() {
        padding_left += base_padding_left + random.nextInt(range_padding_left);
        padding_top = base_padding_top + random.nextInt(range_padding_top);
    }
}
