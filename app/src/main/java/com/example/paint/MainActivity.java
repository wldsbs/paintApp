package com.example.paint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.Toast;
import android.os.Bundle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    final int LINE = 1, SQUARE = 2, CIRCLE = 3,TEXT=4 ,ERASER=5 ,FINGER = 6;
    private MyGraphicView mg;
    LinearLayout palette;
    LinearLayout textLayout;
    TableLayout filter;
    private ImageButton[] colorImageButtons;
    static Bitmap picture=null;
    // 초기화 버튼, 저장 버튼
    private ImageButton paletteBtn, eraserButton,textBtn,modeBtn,loadBtn,moveBtn,seekBarBtn;
    //안보이는 버튼들
    Button finTxetBtn;
    //text요소
    SeekBar brightBar;
    SeekBar blurBar;

    boolean imgFlag = false; //이미지가 있나 없나를 공유하는 전역변수


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("graphic editor");

        ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},MODE_PRIVATE);

        //그림 그릴 뷰를 생성
        mg = (MyGraphicView) findViewById(R.id.drawingView);

        LinearLayout pictureLayout = (LinearLayout) findViewById(R.id.pictureLayout);

        //invisible 요소들
        //palette = (LinearLayout) findViewById(R.id.palette);
        filter = (TableLayout) findViewById(R.id.hideFilter);
        textLayout = (LinearLayout)findViewById(R.id.textLayout);

        //메뉴버튼 등록
        modeBtn = (ImageButton) findViewById(R.id.modeBtn);
        registerForContextMenu(modeBtn);
        eraserButton = (ImageButton) findViewById(R.id.eraserBtn);
        registerForContextMenu(eraserButton);

        /*
        //색상차트
        colorImageButtons = new ImageButton[5];
        colorImageButtons[0] = (ImageButton) findViewById(R.id.blackColorBtn);
        colorImageButtons[1] = (ImageButton) findViewById(R.id.redColorBtn);
        colorImageButtons[2] = (ImageButton) findViewById(R.id.yellowColorBtn);
        colorImageButtons[3] = (ImageButton) findViewById(R.id.greenColorBtn);
        colorImageButtons[4] = (ImageButton) findViewById(R.id.blueColorBtn);
        for (ImageButton colorImageButton : colorImageButtons) {
            colorImageButton.setOnClickListener(this);
        }
        */

        //기능버튼
        //eraserButton = (ImageButton) findViewById(R.id.eraserBtn); //메뉴등록때 이미 등록
        eraserButton.setOnClickListener(this);
        modeBtn.setOnClickListener(this);//모드를 변경하는 버튼도 등록
        textBtn = (ImageButton) findViewById(R.id.textBtn);
        textBtn.setOnClickListener(this);
        loadBtn = (ImageButton) findViewById(R.id.loadBtn);
        loadBtn.setOnClickListener(this);
        //paletteBtn = (ImageButton) findViewById(R.id.paletteBtn);
        //paletteBtn.setOnClickListener(this);
        moveBtn = (ImageButton) findViewById(R.id.moveBtn);
        moveBtn.setOnClickListener(this);
        seekBarBtn = (ImageButton) findViewById(R.id.filterBtn);
        seekBarBtn.setOnClickListener(this);
        finTxetBtn = (Button)findViewById(R.id.finTextBtn);
        finTxetBtn.setOnClickListener(this);

        //시크바 등록
        brightBar = (SeekBar) findViewById(R.id.brightBar);
        blurBar = (SeekBar) findViewById(R.id.blurBar);


        brightBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mg.setBrightness(progress);

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        blurBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mg.setBlurness(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }
    //버튼 클릭 이벤트들...
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            /*case R.id.blackColorBtn:
                mg.setColor(Color.BLACK);
                break;

            case R.id.redColorBtn:
                mg.setColor(Color.RED);
                break;

            case R.id.yellowColorBtn:
                mg.setColor(Color.YELLOW);
                break;

            case R.id.greenColorBtn:
                mg.setColor(Color.GREEN);
                break;

            case R.id.blueColorBtn:
                mg.setColor(Color.BLUE);
                break;


            case R.id.paletteBtn:
                mg.setMode(LINE);
                if (palette.getVisibility() == view.GONE) {
                    palette.setVisibility(view.VISIBLE);
                } else {
                    palette.setVisibility(view.GONE);
                }
                break;
*/
            case R.id.eraserBtn:
                mg.setMode(ERASER);
                break;

            case R.id.modeBtn:
                mg.setMode(LINE);
                break;

            case R.id.textBtn:
                if (textLayout.getVisibility() == view.GONE) {
                    textLayout.setVisibility(view.VISIBLE);
                } else {
                    textLayout.setVisibility(view.GONE);
                }
                break;
            case R.id.finTextBtn:
                mg.setMode(FINGER); //텍스트 입력후 바로 움직일 수 있도록
                EditText editText = (EditText)findViewById(R.id.editText);
                mg.setTEXT(editText.getText().toString());
                break;

            case R.id.loadBtn:
                //sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,1);
                break;
            case R.id.moveBtn:
                mg.setMode(FINGER);
                break;
            case R.id.filterBtn:
                if (filter.getVisibility() == view.GONE) {
                    filter.setVisibility(view.VISIBLE);
                } else {
                    filter.setVisibility(view.GONE);
                }

                break;
        }
    }
    @Override//메뉴 등록
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater mInflater = getMenuInflater();
        if(v == modeBtn) {
            mInflater.inflate(R.menu.mode, menu);
        }
        if(v==eraserButton) {
            mInflater.inflate(R.menu.reset, menu);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater mnInflater = getMenuInflater();
        mnInflater.inflate(R.menu.menu1,menu);
        return true;
    }

    //메뉴클릭 동작
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.line:
                mg.setMode(LINE);
                return true;
            case R.id.square:
                mg.setMode(SQUARE);
                return true;
            case R.id.circle:
                mg.setMode(CIRCLE);
                return true;
            case R.id.clear:
                mg.reset();
                return true;
        }
        return super.onContextItemSelected(item);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch ( item.getItemId()){
            case R.id.save:
                View Myview = mg.getView();
                Myview.setDrawingCacheEnabled(true);
                Bitmap screenshot = Myview.getDrawingCache(); //스샷 형태로 저장
                File file = new File(Environment.getExternalStorageDirectory()+"/Pictures", "scr.png");
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                    screenshot.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                    Toast.makeText(MainActivity.this.getApplicationContext(), "저장완료", Toast.LENGTH_LONG).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this.getApplicationContext(), "파일없음 오류", Toast.LENGTH_LONG).show();
                } catch (IOException e){
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this.getApplicationContext(), "io오류", Toast.LENGTH_LONG).show();
                }
                Myview.setDrawingCacheEnabled(false);
                return true;
            case R.id.load:
                Bitmap picture = BitmapFactory.decodeFile("/sdcard/Pictures/scr.png");
                mg.addImage(picture);
                return true;
        }
        return false;
    }

    //이미지 불러오기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                try {
                    // 선택한 이미지에서 비트맵 생성
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    picture = BitmapFactory.decodeStream(in);
                    imgFlag = true;
                    in.close();
                    mg.addImage(picture);
                    //picture.recycle(); //비트맵 리소스 해제
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //이미지(사진) 뷰
    public class MyImgView extends View {
        public MyImgView(Context context) {
            super(context);
        }
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (picture != null) {
                int picX = (this.getWidth() - picture.getWidth()) / 2 ;
                int picY = (this.getHeight() - picture.getHeight()) / 2 ;
                canvas.drawBitmap(picture, picX, picY, null);
                picture.recycle(); //비트맵 리소스 해제
            }
        }
        public void reset() {
            imgFlag = false; // PaintPoint ArrayList Clear
            picture=null; //리소스 해체
            invalidate(); // 화면을 갱신함 -> onDraw()를 호출
        }
    }
}
