package com.example.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Main4Activity extends AppCompatActivity {

    private  final int[][] a = new int[8][8];

    private int[][] pre_board = new int[8][8];
    private boolean is_pre = false;

    private final Board current_board = new Board();

    private Board temp_board = new Board();

    private final int Degree_of_difficulty=Main2Activity.level;
    private int x=0;
    private int y=0;
    private int count=1;//

    private static boolean isboo=false;
    private static boolean black_or_white=false; // true黑子，false白子

    private int mobility = 0;
    private int mobility_point = 0;//行动力

    private static final int[][] square_point=
            {{999,-120,10,10,10,10,-120,999},
                    {-120,-160,5,5,5,5,-160,-120},
                    {10,5,1,1,1,1,5,10},
                    {10,5,1,1,1,1,5,10},
                    {10,5,1,1,1,1,5,10},
                    {10,5,1,1,1,1,5,10},
                    {-120,-160,5,5,5,5,-160,-120},
                    {999,-120,10,10,10,10,-120,999}};

    private static int temp_point = -200;
    public static int AI_point = -100;
    public static int player_point = -100;

    private static int search = 0;
    private static final int search_level = 5;//奇数

    private static int temp_x = 0;
    private static int temp_y = 0;//暂存 位置

    CustomView test;

    public Handler test_Handler;
    public final int do_hard = 123456;

    Button btn1_clear,btn2_last,btn3_choose;

    private Toast mToast;

    final Thread thread = new Thread(new Runnable(){
        @Override
        public void run() {
            Message msg = test_Handler.obtainMessage(do_hard);
            test_Handler.sendMessage(msg);
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        a[3][3]=1;
        a[3][4]=2;
        a[4][3]=2;
        a[4][4]=1;
        final int first_board[][] = new int[8][8];
        first_board[3][3]=1;
        first_board[3][4]=2;
        first_board[4][3]=2;
        first_board[4][4]=1;
        // 1 is black , 2 is white

        setContentView(R.layout.activity_main4);//R.layout.activity_main4
        LinearLayout layout=(LinearLayout) findViewById(R.id.root);
        btn1_clear = (Button) findViewById(R.id.btn_reset);
        btn2_last = (Button) findViewById(R.id.button_return);
        btn3_choose = (Button) findViewById(R.id.btn_decidemode);
        test = new CustomView(this );
        test.invalidate();
        layout.addView(test);
        test_Handler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case do_hard:
                    {
                        try
                        {
                            Thread.sleep(1000);
                            hardAI();
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        };

        btn1_clear.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                for(int i=0 ; i < 8 ; i++)
                {
                    for(int j=0 ; j<8 ; j++)
                    {
                        a[i][j] = first_board[i][j];
                    }
                }

                count = 1;

                test.invalidate();
            }
        });

        btn2_last.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view){
                if(is_pre)
                {
                    for(int i=0 ; i < 8 ; i++)
                    {
                        for(int j=0 ; j<8 ; j++)
                        {
                            a[i][j] = pre_board[i][j];
                        }
                    }

                    count-=2;
                    is_pre = false;
                    test.invalidate();
                }
            }

        });

        btn3_choose.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(Main4Activity.this,Main2Activity.class);
                startActivity(intent);
            }
        });
    }
    public  class CustomView extends View{

        private final Paint paint;
        public CustomView(Context context) {
            super(context);
            paint = new Paint();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.save();
            paint.setColor(Color.argb(255,0,90,0));
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(40, 0, 1040, 1000, paint);

            paint.setColor(Color.argb(255,0,0,0));
            paint.setStrokeWidth((float)5.0);
            int i=0;
            while(i<=1000)
            {
                canvas.drawLine(40,0+i,1040,0+i,paint);
                canvas.drawLine(40+i,0,40+i,1000,paint);
                i+=125;
            }
            paint.setStyle(Paint.Style.FILL);
            for(i=0;i<8;i++)
            {
                for(int j=0;j<8;j++)
                {
                    if(a[i][j]==1)
                    {
                        paint.setColor(Color.BLACK);
                        canvas.drawCircle(j*125+60+40,i*125+60,60,paint);
                    }
                    else if(a[i][j]==2)
                    {
                        paint.setColor(Color.WHITE);
                        canvas.drawCircle(j*125+60+40,i*125+60,60,paint);
                    }
                }
            }
            if(count%2==0)
            {
                paint.setColor(Color.RED);
                paint.setTextSize(45);
                canvas.drawText("白子思考中",0,1200,paint);
            }

            paint.setColor(Color.RED);
            paint.setTextSize(45);
            canvas.drawText("第" + count + "步",0,1400,paint);
        }
    }

    public boolean onTouchEvent(MotionEvent event)
    {
        if(Degree_of_difficulty!=0 && check())
        {
            x=(int) event.getX();
            y = (int) event.getY();
            y = y-200;
            int action = event.getAction();
            if(action==MotionEvent.ACTION_DOWN && x>=40 && x<1040 && y>=0 && y<1000)
            {
                if(a[y/125][(x - 40)/125]==0)
                {
                    chess();
                    return true;
                }
                else
                    showToast("不能下在这里");
            }

        }
        return false;
    }

    private void chess()
    {
        if(count%2==1)
        {
            for(int i=0 ; i < 8 ; i++)
            {
                for(int j=0 ; j<8 ; j++)
                {
                    pre_board[i][j] = a[i][j];
                }
            }
            is_pre = true;
            a[y/125][(x - 40)/125]=1;
        }
        else
            a[y/125][(x - 40)/125]=2;
        if(change(y/125,(x - 40)/125))
        {
            test.invalidate();

            count++;
            if(Degree_of_difficulty==3)
            {
                if(check())
                {
                    thread.start();
                }
                else
                    end();
            }

        }
        else {
            a[y/125][(x - 40)/125]=0;
            showToast("不能下在这里!");

        }
    }

    private boolean change(int aa, int bb)
    {
        boolean lazi = false;

        //black
        if(a[aa][bb] == 1)
        {
            //up

            int temp=-1;
            for(int i=aa-1;i>=0;i--)
            {
                if(a[i][bb]==2)
                {
                    if(i-1 >= 0)
                        if(a[i-1][bb]==1)
                        {
                            isboo=true;
                            temp = i-1;
                            break;
                        }
                }
                if(a[i][bb]==0)
                    break;
                if(a[i][bb]==1)
                    break;
            }
            if(isboo && temp != -1)
            {
                for(int i=aa-1 ; i>temp ; i--)
                    a[i][bb]=1;
            }
            if(isboo)
                lazi = true;
            isboo=false;

            //left

            int temp1=-1;
            for(int i=bb-1; i>=0 ; i--)
            {
                if(a[aa][i]==2)
                {
                    if(i-1 >= 0)
                        if(a[aa][i-1]==1)
                        {
                            isboo=true;
                            temp1 = i-1;
                            break;
                        }
                }
                if(a[aa][i]==0)
                    break;
                if(a[aa][i]==1)
                    break;
            }
            if(isboo && temp1 != -1)
            {
                for(int i=bb-1 ; i>temp1 ; i--)
                    a[aa][i]=1;
            }
            if(isboo)
                lazi = true;
            isboo=false;

            //down

            int temp2=-1;
            for(int i=aa+1;i<8;i++)
            {
                if(a[i][bb]==2)
                {
                    if(i+1 < 8)
                        if(a[i+1][bb]==1)
                        {
                            isboo=true;
                            temp2 = i+1;
                            break;
                        }
                }
                if(a[i][bb]==0)
                    break;
                if(a[i][bb]==1)
                    break;
            }
            if(isboo && temp2 != -1)
            {
                for(int i=aa+1 ; i<temp2 ; i++)
                    a[i][bb]=1;
            }
            if(isboo)
                lazi = true;
            isboo=false;

            //right

            int temp3=-1;
            for(int i=bb+1; i<8 ; i++)
            {
                if(a[aa][i]==2)
                {
                    if(i+1 < 8)
                        if(a[aa][i+1]==1)
                        {
                            isboo=true;
                            temp3 = i+1;
                            break;
                        }
                }
                if(a[aa][i]==0)
                    break;
                if(a[aa][i]==1)
                    break;
            }
            if(isboo && temp3 != -1)
            {
                for(int i=bb+1 ; i<temp3 ; i++)
                    a[aa][i]=1;
            }
            if(isboo)
                lazi = true;
            isboo=false;

            //left down

            int temp4=-1;
            int temp5=-1;
            for(int k=aa+1,l=bb-1; k<8&&l>=0;k++,l--)
            {
                if(a[k][l]==2)
                {
                    if(k+1 < 8 && l-1 >= 0)
                        if(a[k+1][l-1]==1)
                        {
                            isboo=true;
                            temp4 = k+1;
                            temp5 = l-1;
                            break;
                        }
                }
                if(a[k][l]==0)
                    break;
                if(a[k][l]==1)
                    break;
            }
            if(isboo && (temp4 != -1 && temp5 != -1))
            {
                for(int i=aa+1 , j=bb-1 ; i<temp4 && j>temp5 ; i++,j--)
                    a[i][j]=1;
            }
            if(isboo)
                lazi = true;
            isboo=false;

            //right down

            int temp6=-1;
            int temp7=-1;
            for(int k=aa+1,l=bb+1; k<8&&l<8;k++,l++)
            {
                if(a[k][l]==2)
                {

                    if(k+1 < 8 && l+1 < 8)
                        if(a[k+1][l+1]==1)
                        {
                            isboo=true;
                            temp6 = k+1;
                            temp7 = l+1;
                            break;
                        }
                }
                if(a[k][l]==0)
                    break;
                if(a[k][l]==1)
                    break;
            }
            if(isboo && temp6 != -1 && temp7 != -1)
            {
                for(int i=aa+1 , j=bb+1 ; i<temp6 && j<temp7 ; i++,j++)
                    a[i][j]=1;
            }
            if(isboo)
                lazi = true;
            isboo=false;

            //left up

            int temp8=-1;
            int temp9=-1;
            for(int k=aa-1,l=bb-1; k>=0&&l>=0;k--,l--)
            {
                if(a[k][l]==2)
                {
                    if(k-1 >= 0 && l-1 >= 0)
                        if(a[k-1][l-1]==1)
                        {
                            isboo=true;
                            temp8 = k-1;
                            temp9 = l-1;
                            break;
                        }
                }
                if(a[k][l]==0)
                    break;
                if(a[k][l]==1)
                    break;
            }
            if(isboo && (temp8 != -1 && temp9 != -1))
            {
                for(int i=aa-1 , j=bb-1 ; i>temp8 && j>temp9 ; i--,j--)
                    a[i][j]=1;
            }
            if(isboo)
                lazi = true;
            isboo=false;

            //right up

            int temp10=-1;
            int temp11=-1;
            for(int k=aa-1,l=bb+1; k>=0&&l<8;k--,l++)
            {
                if(a[k][l]==2)
                {
                    if(k-1 >= 0 && l+1 < 8)
                        if(a[k-1][l+1]==1)
                        {
                            isboo=true;
                            temp10 = k-1;
                            temp11 = l+1;
                            break;
                        }
                }
                if(a[k][l]==0)
                    break;
                if(a[k][l]==1)
                    break;
            }
            if(isboo && temp10 != -1 && temp11 != -1)
            {
                for(int i=aa-1 , j=bb+1 ; i>temp10 && j<temp11 ; i--,j++)
                    a[i][j]=1;
            }
            if(isboo)
                lazi = true;
            isboo=false;

        }

        //white
        if(a[aa][bb] == 2)
        {
            //up
            int temp=-1;
            for(int i=aa-1;i>=0;i--)
            {
                if(a[i][bb]==1)
                {
                    if(i-1 >= 0)
                        if(a[i-1][bb]==2)
                        {
                            isboo=true;
                            temp = i-1;
                            break;
                        }
                }
                if(a[i][bb]==0)
                    break;
                if(a[i][bb]==2)
                    break;
            }
            if(isboo && temp != -1)
            {
                for(int i=aa-1 ; i>temp ; i--)
                    a[i][bb]=2;
            }
            if(isboo)
                lazi = true;
            isboo=false;


            //left

            int temp1=-1;
            for(int i=bb-1; i>=0 ; i--)
            {
                if(a[aa][i]==1)
                {
                    if(i-1 >= 0)
                        if(a[aa][i-1]==2)
                        {
                            isboo=true;
                            temp1 = i-1;
                            break;
                        }
                }
                if(a[aa][i]==0)
                    break;
                if(a[aa][i]==2)
                    break;
            }
            if(isboo && temp1 != -1)
            {
                for(int i=bb-1 ; i>temp1 ; i--)
                    a[aa][i]=2;
            }
            if(isboo)
                lazi = true;
            isboo=false;

            //down

            int temp2=-1;
            for(int i=aa+1;i<8;i++)
            {
                if(a[i][bb]==1)
                {
                    if(i+1 < 8)
                        if(a[i+1][bb]==2)
                        {
                            isboo=true;
                            temp2 = i+1;
                            break;
                        }
                }
                if(a[i][bb]==0)
                    break;
                if(a[i][bb]==2)
                    break;
            }
            if(isboo && temp2 != -1)
            {
                for(int i=aa+1 ; i<temp2 ; i++)
                    a[i][bb]=2;
            }
            if(isboo)
                lazi = true;
            isboo=false;

            //right

            int temp3=-1;
            for(int i=bb+1; i<8 ; i++)
            {
                if(a[aa][i]==1)
                {
                    if(i+1 < 8)
                        if(a[aa][i+1]==2)
                        {
                            isboo=true;
                            temp3 = i+1;
                            break;
                        }
                }
                if(a[aa][i]==0)
                    break;
                if(a[aa][i]==2)
                    break;
            }
            if(isboo && temp3 != -1)
            {
                for(int i=bb+1 ; i<temp3 ; i++)
                    a[aa][i]=2;
            }
            if(isboo)
                lazi = true;
            isboo=false;

            //left down

            int temp4=-1;
            int temp5=-1;
            for(int k=aa+1,l=bb-1; k<8&&l>=0;k++,l--)
            {
                if(a[k][l]==1)
                {
                    if(k+1 < 8 && l-1 >= 0)
                        if(a[k+1][l-1]==2)
                        {
                            isboo=true;
                            temp4 = k+1;
                            temp5 = l-1;
                            break;
                        }
                }
                if(a[k][l]==0)
                    break;
                if(a[k][l]==2)
                    break;
            }
            if(isboo && (temp4 != -1 && temp5 != -1))
            {
                for(int i=aa+1 , j=bb-1 ; i<temp4 && j>temp5 ; i++,j--)
                    a[i][j]=2;
            }
            if(isboo)
                lazi = true;
            isboo=false;

            //right down

            int temp6=-1;
            int temp7=-1;
            for(int k=aa+1,l=bb+1; k<8&&l<8;k++,l++)
            {
                if(a[k][l]==1)
                {

                    if(k+1 < 8 && l+1 < 8)
                        if(a[k+1][l+1]==2)
                        {
                            isboo=true;
                            temp6 = k+1;
                            temp7 = l+1;
                            break;
                        }
                }
                if(a[k][l]==0)
                    break;
                if(a[k][l]==2)
                    break;
            }
            if(isboo && temp6 != -1 && temp7 != -1)
            {
                for(int i=aa+1 , j=bb+1 ; i<temp6 && j<temp7 ; i++,j++)
                    a[i][j]=2;
            }
            if(isboo)
                lazi = true;
            isboo=false;

            //left up

            int temp8=-1;
            int temp9=-1;
            for(int k=aa-1,l=bb-1; k>=0&&l>=0;k--,l--)
            {
                if(a[k][l]==1)
                {
                    if(k-1 >= 0 && l-1 >= 0)
                        if(a[k-1][l-1]==2)
                        {
                            isboo=true;
                            temp8 = k-1;
                            temp9 = l-1;
                            break;
                        }
                }
                if(a[k][l]==0)
                    break;
                if(a[k][l]==2)
                    break;
            }
            if(isboo && (temp8 != -1 && temp9 != -1))
            {
                for(int i=aa-1 , j=bb-1 ; i>temp8 && j>temp9 ; i--,j--)
                    a[i][j]=2;
            }
            if(isboo)
                lazi = true;
            isboo=false;

            //right up

            int temp10=-1;
            int temp11=-1;
            for(int k=aa-1,l=bb+1; k>=0&&l<8;k--,l++)
            {
                if(a[k][l]==1)
                {
                    if(k-1 >= 0 && l+1 < 8)
                        if(a[k-1][l+1]==2)
                        {
                            isboo=true;
                            temp10 = k-1;
                            temp11 = l+1;
                            break;
                        }
                }
                if(a[k][l]==0)
                    break;
                if(a[k][l]==2)
                    break;
            }
            if(isboo && temp10 != -1 && temp11 != -1)
            {
                for(int i=aa-1 , j=bb+1 ; i>temp10 && j<temp11 ; i--,j++)
                    a[i][j]=2;
            }
            if(isboo)
                lazi = true;
            isboo=false;

        }

        return lazi;
    }

    private void hardAI()
    {
        Board best_choice = new Board();
        int b[][] = new int[8][8];
        for(int i=0 ; i<8 ; i++)
        {
            for(int j=0 ; j<8 ; j++)
            {
                b[i][j] = a[i][j];
            }
        }
        current_board.board_record(a);
        temp_board.board_record(current_board.board_state());

        for(int i=0 ; i<8 ; i++)
        {
            for(int j=0 ; j<8 ; j++)
            {
                if(a[i][j]==2){
                    hard(i,j);
                }

                if(temp_board.getPoint() > best_choice.getPoint()){
                    best_choice.board_record(temp_board.board_state(), temp_board.getX(), temp_board.getY(),
                            temp_board.getPoint());
                }

                for(int m = 0; m < current_board.board_state().length; m++)
                    for(int n = 0; n < current_board.board_state()[m].length; n++)
                        a[m][n] = current_board.board_state()[m][n];
            }
        }

        current_board.board_record(best_choice.board_state(), best_choice.getX(), best_choice.getY(),
                best_choice.getPoint());

        for(int m = 0; m < current_board.board_state().length; m++)
            for(int n = 0; n < current_board.board_state()[m].length; n++)
                a[m][n] = current_board.board_state()[m][n];



        temp_point = -200;
        for(int i=0 ; i<8 ; i++)
        {
            for(int j=0 ; j<8 ; j++)
            {
                a[i][j] = b[i][j];
            }
        }
        a[temp_x][temp_y]=2;
        change(temp_x , temp_y);


        test.invalidate();
        count++;
        end();
    }

    private void hard(int aa, int bb) {

        int aaa, bbb;

        int maxPoint = -200;

        int playerPoint = -161, aiPoint = -161 ;

        int initializationPoint;

        int floorTempPoint = -200;

        initializationPoint = temp_point;

        int temp_mobility_point;

        Board best_decision = new Board();

        Board tempBoard = new Board();

        Board initializationBoard = new Board();

        initializationBoard.board_record(a);

        if (black_or_white) {
            if (search < search_level) {

                //up
                for (int i = aa - 1; i >= 0; i--) {
                    if (a[i][bb] == 1)
                        break;
                    if (a[i][bb] == 0)
                        break;
                    if (a[i][bb] == 2) {
                        if (i - 1 >= 0)
                            if (a[i - 1][bb] == 0) {
                                if (square_point[i - 1][bb] > playerPoint) {
                                    playerPoint = square_point[i - 1][bb];
                                    aaa = i - 1;
                                    bbb = bb;
                                    hard_change(aaa, bbb);
                                    tempBoard.board_record(a);

                                    checkMobility();
                                    temp_mobility_point = mobility_point;

                                    floorTempPoint = playerPoint - temp_mobility_point;

                                    for (int l = 0; l < 8; l++) {
                                        for (int j = 0; j < 8; j++) {
                                            if (a[l][j] == 2) {

                                                ++search;
                                                black_or_white = !black_or_white;//黑白交換
                                                hard(l, j);
                                                --search;
                                                black_or_white = !black_or_white;


                                                for (int m = 0; m < tempBoard.board_state().length; m++)
                                                    for (int n = 0; n < tempBoard.board_state()[m].length; n++)
                                                        a[m][n] = tempBoard.board_state()[m][n];

                                            }
                                        }
                                    }

                                    if ((playerPoint - temp_point - temp_mobility_point) > maxPoint) {
                                        maxPoint = playerPoint - temp_point - temp_mobility_point;
                                        temp_point = maxPoint;
                                        best_decision.board_record(a, aaa, bbb, maxPoint);
                                    }

                                    temp_point = initializationPoint;
                                }
                            }
                    }
                }

                //初始化為該層初始狀態
                for(int m = 0; m < initializationBoard.board_state().length; m++)
                    for(int n = 0; n < initializationBoard.board_state()[m].length; n++)
                        a[m][n] = initializationBoard.board_state()[m][n];
                //down
                for (int i = aa + 1; i < 8; i++) {
                    if (a[i][bb] == 1)
                        break;
                    if (a[i][bb] == 0)
                        break;
                    if (a[i][bb] == 2) {
                        if (i + 1 < 8)
                            if (a[i + 1][bb] == 0) {
                                if (square_point[i + 1][bb] > playerPoint) {
                                    playerPoint = square_point[i + 1][bb];
                                    aaa = i + 1;
                                    bbb = bb;
                                    hard_change(aaa, bbb);
                                    checkMobility();
                                    temp_mobility_point = mobility_point;
                                    tempBoard.board_record(a);

                                    if(playerPoint - temp_mobility_point > floorTempPoint) {

                                        floorTempPoint = playerPoint - temp_mobility_point;

                                        for (int l = 0; l < 8; l++) {
                                            for (int j = 0; j < 8; j++) {
                                                if (a[l][j] == 2) {
                                                    ++search;
                                                    black_or_white = !black_or_white;
                                                    hard(l, j);
                                                    --search;
                                                    black_or_white = !black_or_white;

                                                    for (int m = 0; m < tempBoard.board_state().length; m++)
                                                        for (int n = 0; n < tempBoard.board_state()[m].length; n++)
                                                            a[m][n] = tempBoard.board_state()[m][n];

                                                }
                                            }
                                        }
                                    }

                                    if ((playerPoint - temp_point - temp_mobility_point) > maxPoint) {
                                        maxPoint = playerPoint - temp_point - temp_mobility_point;
                                        temp_point = maxPoint;
                                        best_decision.board_record(a, aaa, bbb, maxPoint);
                                    }

                                    temp_point = initializationPoint;
                                }
                            }
                    }
                }

                for(int m = 0; m < initializationBoard.board_state().length; m++)
                    for(int n = 0; n < initializationBoard.board_state()[m].length; n++)
                        a[m][n] = initializationBoard.board_state()[m][n];
                //left
                for (int i = bb - 1; i >= 0; i--) {
                    if (a[aa][i] == 1)
                        break;
                    if (a[aa][i] == 0)
                        break;
                    if (a[aa][i] == 2) {
                        if (i - 1 >= 0)
                            if (a[aa][i - 1] == 0) {
                                if (square_point[aa][i - 1] > playerPoint) {
                                    playerPoint = square_point[aa][i - 1];
                                    aaa = aa;
                                    bbb = i - 1;
                                    hard_change(aaa, bbb);
                                    checkMobility();
                                    temp_mobility_point = mobility_point;
                                    tempBoard.board_record(a);

                                    if(playerPoint - temp_mobility_point > floorTempPoint) {

                                        floorTempPoint = playerPoint - temp_mobility_point;

                                        for (int l = 0; l < 8; l++) {
                                            for (int j = 0; j < 8; j++) {
                                                if (a[l][j] == 2) {
                                                    ++search;
                                                    black_or_white = !black_or_white;
                                                    hard(l, j);
                                                    --search;
                                                    black_or_white = !black_or_white;

                                                    for (int m = 0; m < tempBoard.board_state().length; m++)
                                                        for (int n = 0; n < tempBoard.board_state()[m].length; n++)
                                                            a[m][n] = tempBoard.board_state()[m][n];

                                                }
                                            }
                                        }
                                    }

                                    if ((playerPoint - temp_point - temp_mobility_point) > maxPoint) {
                                        maxPoint = playerPoint - temp_point - temp_mobility_point;
                                        temp_point = maxPoint;
                                        best_decision.board_record(a, aaa, bbb, maxPoint);
                                    }

                                    temp_point = initializationPoint;
                                }
                            }
                    }
                }

                for(int m = 0; m < initializationBoard.board_state().length; m++)
                    for(int n = 0; n < initializationBoard.board_state()[m].length; n++)
                        a[m][n] = initializationBoard.board_state()[m][n];
                //right
                for (int i = bb + 1; i < 8; i++) {

                    if (a[aa][i] == 1)
                        break;
                    if (a[aa][i] == 0)
                        break;
                    if (a[aa][i] == 2) {
                        if (i + 1 < 8)
                            if (a[aa][i + 1] == 0) {
                                if (square_point[aa][i + 1] > playerPoint) {
                                    playerPoint = square_point[aa][i + 1];
                                    aaa = aa;
                                    bbb = i + 1;
                                    hard_change(aaa, bbb);
                                    checkMobility();
                                    temp_mobility_point = mobility_point;
                                    tempBoard.board_record(a);

                                    if(playerPoint - temp_mobility_point > floorTempPoint) {

                                        floorTempPoint = playerPoint - temp_mobility_point;

                                        for (int l = 0; l < 8; l++) {
                                            for (int j = 0; j < 8; j++) {
                                                if (a[l][j] == 2) {
                                                    ++search;
                                                    black_or_white = !black_or_white;
                                                    hard(l, j);
                                                    --search;
                                                    black_or_white = !black_or_white;

                                                    for (int m = 0; m < tempBoard.board_state().length; m++)
                                                        for (int n = 0; n < tempBoard.board_state()[m].length; n++)
                                                            a[m][n] = tempBoard.board_state()[m][n];

                                                }
                                            }
                                        }
                                    }

                                    if ((playerPoint - temp_point - temp_mobility_point) > maxPoint) {
                                        maxPoint = playerPoint - temp_point - temp_mobility_point;
                                        temp_point = maxPoint;
                                        best_decision.board_record(a, aaa, bbb, maxPoint);
                                    }

                                    temp_point = initializationPoint;
                                }
                            }
                    }
                }

                for(int m = 0; m < initializationBoard.board_state().length; m++)
                    for(int n = 0; n < initializationBoard.board_state()[m].length; n++)
                        a[m][n] = initializationBoard.board_state()[m][n];
                //right up
                for (int k = aa - 1, l = bb + 1; k >= 0 && l < 8; k--, l++) {
                    if (a[k][l] == 1)
                        break;
                    if (a[k][l] == 0)
                        break;
                    if (a[k][l] == 2) {
                        if (k - 1 >= 0 && l + 1 < 8)
                            if (a[k - 1][l + 1] == 0) {
                                if (square_point[k - 1][l + 1] > playerPoint) {
                                    playerPoint = square_point[k - 1][l + 1];
                                    aaa = k - 1;
                                    bbb = l + 1;
                                    hard_change(aaa, bbb);
                                    checkMobility();
                                    temp_mobility_point = mobility_point;
                                    tempBoard.board_record(a);

                                    if(playerPoint - temp_mobility_point > floorTempPoint) {

                                        floorTempPoint = playerPoint - temp_mobility_point;

                                        for (int i = 0; i < 8; i++) {
                                            for (int j = 0; j < 8; j++) {
                                                if (a[i][j] == 2) {
                                                    ++search;
                                                    black_or_white = !black_or_white;
                                                    hard(l, j);
                                                    --search;
                                                    black_or_white = !black_or_white;

                                                    for (int m = 0; m < tempBoard.board_state().length; m++)
                                                        for (int n = 0; n < tempBoard.board_state()[m].length; n++)
                                                            a[m][n] = tempBoard.board_state()[m][n];

                                                }
                                            }
                                        }
                                    }

                                    if ((playerPoint - temp_point - temp_mobility_point) > maxPoint) {
                                        maxPoint = playerPoint - temp_point - temp_mobility_point;
                                        temp_point = maxPoint;
                                        best_decision.board_record(a, aaa, bbb, maxPoint);
                                    }

                                    temp_point = initializationPoint;
                                }
                            }
                    }
                }

                for(int m = 0; m < initializationBoard.board_state().length; m++)
                    for(int n = 0; n < initializationBoard.board_state()[m].length; n++)
                        a[m][n] = initializationBoard.board_state()[m][n];
                //right down
                for (int k = aa + 1, l = bb + 1; k < 8 && l < 8; k++, l++) {
                    if (a[k][l] == 1)
                        break;
                    if (a[k][l] == 0)
                        break;
                    if (a[k][l] == 2) {
                        if (k + 1 < 8 && l + 1 < 8)
                            if (a[k + 1][l + 1] == 0) {
                                if (square_point[k + 1][l + 1] > playerPoint) {
                                    playerPoint = square_point[k + 1][l + 1];
                                    aaa = k + 1;
                                    bbb = l + 1;
                                    hard_change(aaa, bbb);
                                    checkMobility();
                                    temp_mobility_point = mobility_point;
                                    tempBoard.board_record(a);

                                    if(playerPoint - temp_mobility_point > floorTempPoint) {

                                        floorTempPoint = playerPoint - temp_mobility_point;

                                        for (int i = 0; i < 8; i++) {
                                            for (int j = 0; j < 8; j++) {
                                                if (a[i][j] == 2) {
                                                    ++search;
                                                    black_or_white = !black_or_white;
                                                    hard(l, j);
                                                    --search;
                                                    black_or_white = !black_or_white;

                                                    for (int m = 0; m < tempBoard.board_state().length; m++)
                                                        for (int n = 0; n < tempBoard.board_state()[m].length; n++)
                                                            a[m][n] = tempBoard.board_state()[m][n];

                                                }
                                            }
                                        }
                                    }

                                    if ((playerPoint - temp_point - temp_mobility_point) > maxPoint) {
                                        maxPoint = playerPoint - temp_point - temp_mobility_point;
                                        temp_point = maxPoint;
                                        best_decision.board_record(a, aaa, bbb, maxPoint);
                                    }

                                    temp_point = initializationPoint;
                                }
                            }
                    }
                }

                for(int m = 0; m < initializationBoard.board_state().length; m++)
                    for(int n = 0; n < initializationBoard.board_state()[m].length; n++)
                        a[m][n] = initializationBoard.board_state()[m][n];
                //left up
                for (int k = aa - 1, l = bb - 1; k >= 0 && l >= 0; k--, l--) {

                    if (a[k][l] == 1)
                        break;
                    if (a[k][l] == 0)
                        break;
                    if (a[k][l] == 2) {
                        if (k - 1 >= 0 && l - 1 >= 0)
                            if (a[k - 1][l - 1] == 0) {
                                if (square_point[k - 1][l - 1] > playerPoint) {
                                    playerPoint = square_point[k - 1][l - 1];
                                    aaa = k - 1;
                                    bbb = l - 1;
                                    hard_change(aaa, bbb);
                                    checkMobility();
                                    temp_mobility_point = mobility_point;
                                    tempBoard.board_record(a);

                                    if(playerPoint - temp_mobility_point > floorTempPoint) {

                                        floorTempPoint = playerPoint - temp_mobility_point;

                                        for (int i = 0; i < 8; i++) {
                                            for (int j = 0; j < 8; j++) {
                                                if (a[i][j] == 2) {
                                                    ++search;
                                                    black_or_white = !black_or_white;
                                                    hard(l, j);
                                                    --search;
                                                    black_or_white = !black_or_white;

                                                    for (int m = 0; m < tempBoard.board_state().length; m++)
                                                        for (int n = 0; n < tempBoard.board_state()[m].length; n++)
                                                            a[m][n] = tempBoard.board_state()[m][n];

                                                }
                                            }
                                        }
                                    }

                                    if ((playerPoint - temp_point - temp_mobility_point) > maxPoint) {
                                        maxPoint = playerPoint - temp_point - temp_mobility_point;
                                        temp_point = maxPoint;
                                        best_decision.board_record(a, aaa, bbb, maxPoint);
                                    }

                                    temp_point = initializationPoint;
                                }
                            }
                    }

                }

                for(int m = 0; m < initializationBoard.board_state().length; m++)
                    for(int n = 0; n < initializationBoard.board_state()[m].length; n++)
                        a[m][n] = initializationBoard.board_state()[m][n];
                //left down
                for (int k = aa + 1, l = bb - 1; k < 8 && l >= 0; k++, l--) {

                    if (a[k][l] == 1)
                        break;
                    if (a[k][l] == 0)
                        break;
                    if (a[k][l] == 2) {
                        if (k + 1 < 8 && l - 1 >= 0)
                            if (a[k + 1][l - 1] == 0) {
                                if (square_point[k + 1][l - 1] > playerPoint) {
                                    playerPoint = square_point[k + 1][l - 1];
                                    aaa = k + 1;
                                    bbb = l - 1;
                                    hard_change(aaa, bbb);
                                    checkMobility();
                                    temp_mobility_point = mobility_point;
                                    tempBoard.board_record(a);

                                    if(playerPoint - temp_mobility_point > floorTempPoint) {

                                        floorTempPoint = playerPoint - temp_mobility_point;

                                        for (int i = 0; i < 8; i++) {
                                            for (int j = 0; j < 8; j++) {
                                                if (a[i][j] == 2) {
                                                    ++search;
                                                    black_or_white = !black_or_white;
                                                    hard(l, j);
                                                    --search;
                                                    black_or_white = !black_or_white;

                                                    for (int m = 0; m < tempBoard.board_state().length; m++)
                                                        for (int n = 0; n < tempBoard.board_state()[m].length; n++)
                                                            a[m][n] = tempBoard.board_state()[m][n];

                                                }
                                            }
                                        }
                                    }

                                    if ((playerPoint - temp_point - temp_mobility_point) > maxPoint) {
                                        maxPoint = playerPoint - temp_point - temp_mobility_point;
                                        temp_point = maxPoint;
                                        best_decision.board_record(a, aaa, bbb, maxPoint);
                                    }

                                    temp_point = initializationPoint;
                                }
                            }
                    }

                }

                temp_board.board_record(best_decision.board_state(), best_decision.getX(), best_decision.getY(),
                        best_decision.getPoint());

            }//if(search_level)

            else {

                //up
                for(int i=aa-1;i>=0;i--)
                {
                    if(a[i][bb]==1)
                        break;
                    if(a[i][bb]==0)
                        break;
                    if(a[i][bb]==2)
                    {
                        if(i-1 >= 0)
                            if(a[i-1][bb]==0)
                            {
                                if(square_point[i-1][bb]>temp_point)
                                {
                                    temp_point=square_point[i-1][bb];
                                }
                            }
                    }
                }

                //down
                for(int i=aa+1;i<8;i++)
                {
                    if(a[i][bb]==1)
                        break;
                    if(a[i][bb]==0)
                        break;
                    if(a[i][bb]==2)
                    {
                        if(i+1 < 8)
                            if(a[i+1][bb]==0)
                            {
                                if(square_point[i+1][bb]>temp_point)
                                {
                                    temp_point=square_point[i+1][bb];
                                }
                            }
                    }

                }

                //left
                for(int i=bb-1;i>=0;i--)
                {
                    if(a[aa][i]==1)
                        break;
                    if(a[aa][i]==0)
                        break;
                    if(a[aa][i]==2)
                    {
                        if(i-1 >= 0)
                            if(a[aa][i-1]==0)
                            {
                                if(square_point[aa][i-1]>temp_point)
                                {
                                    temp_point=square_point[aa][i-1];
                                }
                            }
                    }

                }

                //right
                for(int i=bb+1;i<8;i++)
                {
                    if(a[aa][i]==1)
                        break;
                    if(a[aa][i]==0)
                        break;
                    if(a[aa][i]==2)
                    {
                        if(i+1 < 8)
                            if(a[aa][i+1]==0)
                            {
                                if(square_point[aa][i+1]>temp_point)
                                {
                                    temp_point=square_point[aa][i+1];
                                }
                            }
                    }

                }

                //right up
                for(int k=aa-1,l=bb+1; k>=0&&l<8;k--,l++)
                {
                    if(a[k][l]==1)
                        break;
                    if(a[k][l]==0)
                        break;
                    if(a[k][l]==2)
                    {
                        if(k-1 >= 0 && l+1 < 8)
                            if(a[k-1][l+1]==0)
                            {
                                if(square_point[k-1][l+1]>temp_point)
                                {
                                    temp_point=square_point[k-1][l+1];
                                }
                            }
                    }

                }

                //right down
                for(int k=aa+1,l=bb+1; k<8&&l<8;k++,l++)
                {
                    if(a[k][l]==1)
                        break;
                    if(a[k][l]==0)
                        break;
                    if(a[k][l]==2)
                    {
                        if(k+1 < 8 && l+1 < 8)
                            if(a[k+1][l+1]==0)
                            {
                                if(square_point[k+1][l+1]>temp_point)
                                {
                                    temp_point=square_point[k+1][l+1];
                                }
                            }
                    }

                }

                //left up
                for(int k=aa-1,l=bb-1; k>=0&&l>=0;k--,l--) {
                    if (a[k][l] == 1)
                        break;
                    if (a[k][l] == 0)
                        break;
                    if (a[k][l] == 2) {
                        if (k - 1 >= 0 && l - 1 >= 0)
                            if (a[k - 1][l - 1] == 0) {
                                if (square_point[k - 1][l - 1] > temp_point)
                                {
                                    temp_point = square_point[k - 1][l - 1];
                                }
                            }
                    }

                }

                //left down
                for(int k=aa+1,l=bb-1; k<8&&l>=0;k++,l--)
                {
                    if(a[k][l]==1)
                        break;
                    if(a[k][l]==0)
                        break;
                    if(a[k][l]==2)
                    {
                        if(k+1 < 8 && l-1 >= 0)
                            if(a[k+1][l-1]==0)
                            {
                                if(square_point[k+1][l-1]>temp_point)
                                {
                                    temp_point=square_point[k+1][l-1];
                                }
                            }
                    }

                }

            }//else

        }//if(black_or_white)

        else{//白子

            if( search < search_level ) {

                //up
                for (int i = aa - 1; i >= 0; i--) {
                    if (a[i][bb] == 2)
                        break;
                    if (a[i][bb] == 0)
                        break;
                    if (a[i][bb] == 1) {
                        if (i - 1 >= 0)
                            if (a[i - 1][bb] == 0) {
                                if (square_point[i - 1][bb] > aiPoint) {
                                    aiPoint = square_point[i - 1][bb];
                                    aaa = i - 1;
                                    bbb = bb;
                                    hard_change(aaa, bbb);
                                    tempBoard.board_record(a);
                                    checkMobility();
                                    temp_mobility_point = mobility_point;

                                    floorTempPoint = aiPoint - temp_mobility_point;

                                    for (int l = 0; l < 8; l++) {
                                        for (int j = 0; j < 8; j++) {
                                            if (a[l][j] == 1) {
                                                ++search;
                                                black_or_white = !black_or_white;
                                                hard(l, j);
                                                --search;
                                                black_or_white = !black_or_white;

                                                for (int m = 0; m < tempBoard.board_state().length; m++)
                                                    for (int n = 0; n < tempBoard.board_state()[m].length; n++)
                                                        a[m][n] = tempBoard.board_state()[m][n];

                                            }
                                        }
                                    }

                                    if ((aiPoint - temp_point - temp_mobility_point) > maxPoint) {
                                        maxPoint = aiPoint - temp_point - temp_mobility_point;
                                        temp_point = maxPoint;
                                        best_decision.board_record(a, aaa, bbb, maxPoint);
                                        if(search == 0)
                                        {
                                            temp_x = aaa;
                                            temp_y = bbb;
                                        }
                                    }

                                    temp_point = initializationPoint;
                                }
                            }
                    }
                }

                for(int m = 0; m < initializationBoard.board_state().length; m++)
                    for(int n = 0; n < initializationBoard.board_state()[m].length; n++)
                        a[m][n] = initializationBoard.board_state()[m][n];
                //down
                for (int i = aa + 1; i < 8; i++) {
                    if (a[i][bb] == 2)
                        break;
                    if (a[i][bb] == 0)
                        break;
                    if (a[i][bb] == 1) {
                        if (i + 1 < 8)
                            if (a[i + 1][bb] == 0) {
                                if (square_point[i + 1][bb] > aiPoint) {
                                    aiPoint = square_point[i + 1][bb];
                                    aaa = i + 1;
                                    bbb = bb;
                                    hard_change(aaa, bbb);
                                    checkMobility();
                                    temp_mobility_point = mobility_point;
                                    tempBoard.board_record(a);

                                    if(aiPoint - temp_mobility_point > floorTempPoint) {

                                        floorTempPoint = aiPoint - temp_mobility_point;

                                        for (int l = 0; l < 8; l++) {
                                            for (int j = 0; j < 8; j++) {
                                                if (a[l][j] == 1) {
                                                    ++search;
                                                    black_or_white = !black_or_white;
                                                    hard(l, j);
                                                    --search;
                                                    black_or_white = !black_or_white;

                                                    for (int m = 0; m < tempBoard.board_state().length; m++)
                                                        for (int n = 0; n < tempBoard.board_state()[m].length; n++)
                                                            a[m][n] = tempBoard.board_state()[m][n];

                                                }
                                            }
                                        }
                                    }

                                    if ((aiPoint - temp_point - temp_mobility_point) > maxPoint) {
                                        maxPoint = aiPoint - temp_point - temp_mobility_point;
                                        temp_point = maxPoint;
                                        best_decision.board_record(a, aaa, bbb, maxPoint);
                                        if(search == 0)
                                        {
                                            temp_x = aaa;
                                            temp_y = bbb;
                                        }
                                    }

                                    temp_point = initializationPoint;
                                }
                            }
                    }
                }

                for(int m = 0; m < initializationBoard.board_state().length; m++)
                    for(int n = 0; n < initializationBoard.board_state()[m].length; n++)
                        a[m][n] = initializationBoard.board_state()[m][n];
                //left
                for (int i = bb - 1; i >= 0; i--) {
                    if (a[aa][i] == 2)
                        break;
                    if (a[aa][i] == 0)
                        break;
                    if (a[aa][i] == 1) {
                        if (i - 1 >= 0)
                            if (a[aa][i - 1] == 0) {
                                if (square_point[aa][i - 1] > aiPoint) {
                                    aiPoint = square_point[aa][i - 1];
                                    aaa = aa;
                                    bbb = i - 1;
                                    hard_change(aaa, bbb);
                                    checkMobility();
                                    temp_mobility_point = mobility_point;
                                    tempBoard.board_record(a);

                                    if(aiPoint - temp_mobility_point > floorTempPoint) {

                                        floorTempPoint = aiPoint - temp_mobility_point;

                                        for (int l = 0; l < 8; l++) {
                                            for (int j = 0; j < 8; j++) {
                                                if (a[l][j] == 1) {
                                                    ++search;
                                                    black_or_white = !black_or_white;
                                                    hard(l, j);
                                                    --search;
                                                    black_or_white = !black_or_white;

                                                    for (int m = 0; m < tempBoard.board_state().length; m++)
                                                        for (int n = 0; n < tempBoard.board_state()[m].length; n++)
                                                            a[m][n] = tempBoard.board_state()[m][n];

                                                }
                                            }
                                        }
                                    }

                                    if ((aiPoint - temp_point - temp_mobility_point) > maxPoint) {
                                        maxPoint = aiPoint - temp_point - temp_mobility_point;
                                        temp_point = maxPoint;
                                        best_decision.board_record(a, aaa, bbb, maxPoint);
                                        if(search == 0)
                                        {
                                            temp_x = aaa;
                                            temp_y = bbb;
                                        }
                                    }

                                    temp_point = initializationPoint;
                                }
                            }
                    }
                }

                for(int m = 0; m < initializationBoard.board_state().length; m++)
                    for(int n = 0; n < initializationBoard.board_state()[m].length; n++)
                        a[m][n] = initializationBoard.board_state()[m][n];
                //right
                for (int i = bb + 1; i < 8; i++) {

                    if (a[aa][i] == 2)
                        break;
                    if (a[aa][i] == 0)
                        break;
                    if (a[aa][i] == 1) {
                        if (i + 1 < 8)
                            if (a[aa][i + 1] == 0) {
                                if (square_point[aa][i + 1] > aiPoint) {
                                    aiPoint = square_point[aa][i + 1];
                                    aaa = aa;
                                    bbb = i + 1;
                                    hard_change(aaa, bbb);
                                    checkMobility();
                                    temp_mobility_point = mobility_point;
                                    tempBoard.board_record(a);

                                    if(aiPoint - temp_mobility_point > floorTempPoint) {

                                        floorTempPoint = aiPoint - temp_mobility_point;

                                        for (int l = 0; l < 8; l++) {
                                            for (int j = 0; j < 8; j++) {
                                                if (a[l][j] == 1) {
                                                    ++search;
                                                    black_or_white = !black_or_white;
                                                    hard(l, j);
                                                    --search;
                                                    black_or_white = !black_or_white;

                                                    for (int m = 0; m < tempBoard.board_state().length; m++)
                                                        for (int n = 0; n < tempBoard.board_state()[m].length; n++)
                                                            a[m][n] = tempBoard.board_state()[m][n];

                                                }
                                            }
                                        }
                                    }

                                    if ((aiPoint - temp_point - temp_mobility_point) > maxPoint) {
                                        maxPoint = aiPoint - temp_point - temp_mobility_point;
                                        temp_point = maxPoint;
                                        best_decision.board_record(a, aaa, bbb, maxPoint);
                                        if(search == 0)
                                        {
                                            temp_x = aaa;
                                            temp_y = bbb;
                                        }
                                    }

                                    temp_point = initializationPoint;
                                }
                            }
                    }
                }

                for(int m = 0; m < initializationBoard.board_state().length; m++)
                    for(int n = 0; n < initializationBoard.board_state()[m].length; n++)
                        a[m][n] = initializationBoard.board_state()[m][n];
                //right up
                for (int k = aa - 1, l = bb + 1; k >= 0 && l < 8; k--, l++) {
                    if (a[k][l] == 2)
                        break;
                    if (a[k][l] == 0)
                        break;
                    if (a[k][l] == 1) {
                        if (k - 1 >= 0 && l + 1 < 8)
                            if (a[k - 1][l + 1] == 0) {
                                if (square_point[k - 1][l + 1] > aiPoint) {
                                    aiPoint = square_point[k - 1][l + 1];
                                    aaa = k - 1;
                                    bbb = l + 1;
                                    hard_change(aaa, bbb);
                                    checkMobility();
                                    temp_mobility_point = mobility_point;
                                    tempBoard.board_record(a);

                                    if(aiPoint - temp_mobility_point > floorTempPoint) {

                                        floorTempPoint = aiPoint - temp_mobility_point;

                                        for (int i = 0; i < 8; i++) {
                                            for (int j = 0; j < 8; j++) {
                                                if (a[i][j] == 1) {
                                                    ++search;
                                                    black_or_white = !black_or_white;
                                                    hard(l, j);
                                                    --search;
                                                    black_or_white = !black_or_white;

                                                    for (int m = 0; m < tempBoard.board_state().length; m++)
                                                        for (int n = 0; n < tempBoard.board_state()[m].length; n++)
                                                            a[m][n] = tempBoard.board_state()[m][n];

                                                }
                                            }
                                        }
                                    }

                                    if ((aiPoint - temp_point - temp_mobility_point) > maxPoint) {
                                        maxPoint = aiPoint - temp_point - temp_mobility_point;
                                        temp_point = maxPoint;
                                        best_decision.board_record(a, aaa, bbb, maxPoint);
                                        if(search == 0)
                                        {
                                            temp_x = aaa;
                                            temp_y = bbb;
                                        }
                                    }

                                    temp_point = initializationPoint;
                                }
                            }
                    }
                }

                for(int m = 0; m < initializationBoard.board_state().length; m++)
                    for(int n = 0; n < initializationBoard.board_state()[m].length; n++)
                        a[m][n] = initializationBoard.board_state()[m][n];
                //right down
                for (int k = aa + 1, l = bb + 1; k < 8 && l < 8; k++, l++) {
                    if (a[k][l] == 2)
                        break;
                    if (a[k][l] == 0)
                        break;
                    if (a[k][l] == 1) {
                        if (k + 1 < 8 && l + 1 < 8)
                            if (a[k + 1][l + 1] == 0) {
                                if (square_point[k + 1][l + 1] > aiPoint) {
                                    aiPoint = square_point[k + 1][l + 1];
                                    aaa = k + 1;
                                    bbb = l + 1;
                                    hard_change(aaa, bbb);
                                    checkMobility();
                                    temp_mobility_point = mobility_point;
                                    tempBoard.board_record(a);

                                    if(aiPoint - temp_mobility_point > floorTempPoint) {

                                        floorTempPoint = aiPoint - temp_mobility_point;

                                        for (int i = 0; i < 8; i++) {
                                            for (int j = 0; j < 8; j++) {
                                                if (a[i][j] == 1) {
                                                    ++search;
                                                    black_or_white = !black_or_white;
                                                    hard(l, j);
                                                    --search;
                                                    black_or_white = !black_or_white;

                                                    for (int m = 0; m < tempBoard.board_state().length; m++)
                                                        for (int n = 0; n < tempBoard.board_state()[m].length; n++)
                                                            a[m][n] = tempBoard.board_state()[m][n];

                                                }
                                            }
                                        }
                                    }

                                    if ((aiPoint - temp_point - temp_mobility_point) > maxPoint) {
                                        maxPoint = aiPoint - temp_point - temp_mobility_point;
                                        temp_point = maxPoint;
                                        best_decision.board_record(a, aaa, bbb, maxPoint);
                                        if(search == 0)
                                        {
                                            temp_x = aaa;
                                            temp_y = bbb;
                                        }
                                    }

                                    temp_point = initializationPoint;
                                }
                            }
                    }
                }

                for(int m = 0; m < initializationBoard.board_state().length; m++)
                    for(int n = 0; n < initializationBoard.board_state()[m].length; n++)
                        a[m][n] = initializationBoard.board_state()[m][n];
                //left up
                for (int k = aa - 1, l = bb - 1; k >= 0 && l >= 0; k--, l--) {

                    if (a[k][l] == 2)
                        break;
                    if (a[k][l] == 0)
                        break;
                    if (a[k][l] == 1) {
                        if (k - 1 >= 0 && l - 1 >= 0)
                            if (a[k - 1][l - 1] == 0) {
                                if (square_point[k - 1][l - 1] > aiPoint) {
                                    aiPoint = square_point[k - 1][l - 1];
                                    aaa = k - 1;
                                    bbb = l - 1;
                                    hard_change(aaa, bbb);
                                    checkMobility();
                                    temp_mobility_point = mobility_point;
                                    tempBoard.board_record(a);

                                    if(aiPoint - temp_mobility_point > floorTempPoint) {

                                        floorTempPoint = aiPoint - temp_mobility_point;

                                        for (int i = 0; i < 8; i++) {
                                            for (int j = 0; j < 8; j++) {
                                                if (a[i][j] == 1) {
                                                    ++search;
                                                    black_or_white = !black_or_white;
                                                    hard(l, j);
                                                    --search;
                                                    black_or_white = !black_or_white;

                                                    for (int m = 0; m < tempBoard.board_state().length; m++)
                                                        for (int n = 0; n < tempBoard.board_state()[m].length; n++)
                                                            a[m][n] = tempBoard.board_state()[m][n];

                                                }
                                            }
                                        }
                                    }

                                    if ((aiPoint - temp_point - temp_mobility_point) > maxPoint) {
                                        maxPoint = aiPoint - temp_point - temp_mobility_point;
                                        temp_point = maxPoint;
                                        best_decision.board_record(a, aaa, bbb, maxPoint);
                                        if(search == 0)
                                        {
                                            temp_x = aaa;
                                            temp_y = bbb;
                                        }
                                    }

                                    temp_point = initializationPoint;
                                }
                            }
                    }

                }

                for(int m = 0; m < initializationBoard.board_state().length; m++)
                    for(int n = 0; n < initializationBoard.board_state()[m].length; n++)
                        a[m][n] = initializationBoard.board_state()[m][n];
                //left down
                for (int k = aa + 1, l = bb - 1; k < 8 && l >= 0; k++, l--) {

                    if (a[k][l] == 2)
                        break;
                    if (a[k][l] == 0)
                        break;
                    if (a[k][l] == 1) {
                        if (k + 1 < 8 && l - 1 >= 0)
                            if (a[k + 1][l - 1] == 0) {
                                if (square_point[k + 1][l - 1] > aiPoint) {
                                    aiPoint = square_point[k + 1][l - 1];
                                    aaa = k + 1;
                                    bbb = l - 1;
                                    hard_change(aaa, bbb);
                                    checkMobility();
                                    temp_mobility_point = mobility_point;
                                    tempBoard.board_record(a);

                                    if(aiPoint - temp_mobility_point > floorTempPoint) {

                                        floorTempPoint = aiPoint - temp_mobility_point;

                                        for (int i = 0; i < 8; i++) {
                                            for (int j = 0; j < 8; j++) {
                                                if (a[i][j] == 1) {
                                                    ++search;
                                                    black_or_white = !black_or_white;
                                                    hard(l, j);
                                                    --search;
                                                    black_or_white = !black_or_white;

                                                    for (int m = 0; m < tempBoard.board_state().length; m++)
                                                        for (int n = 0; n < tempBoard.board_state()[m].length; n++)
                                                            a[m][n] = tempBoard.board_state()[m][n];

                                                }
                                            }
                                        }
                                    }

                                    if ((aiPoint - temp_point - temp_mobility_point) > maxPoint) {
                                        maxPoint = aiPoint - temp_point - temp_mobility_point;
                                        temp_point = maxPoint;
                                        best_decision.board_record(a, aaa, bbb, maxPoint);
                                        if(search == 0)
                                        {
                                            temp_x = aaa;
                                            temp_y = bbb;
                                        }
                                    }

                                    temp_point = initializationPoint;
                                }
                            }
                    }

                }

                temp_board.board_record(best_decision.board_state(), best_decision.getX(), best_decision.getY(),
                        best_decision.getPoint());

            }//if(search_level<2)

        }//else(black_or_white)




        if(search == 0) {

            for(int m = 0; m < best_decision.board_state().length; m++)
                for(int n = 0; n < best_decision.board_state()[m].length; n++)
                    a[m][n] = best_decision.board_state()[m][n];

        }

    }


    private boolean hard_change(int aa, int bb)
    {
        boolean lazi = false;

        if(black_or_white) {

            a[aa][bb] = 1;

            //up
            int temp = -1;
            for (int i = aa - 1; i >= 0; i--) {
                if (a[i][bb] == 2) {
                    if (i - 1 >= 0)
                        if (a[i - 1][bb] == 1) {
                            isboo = true;
                            temp = i - 1;
                            break;
                        }
                }
                if (a[i][bb] == 0)
                    break;
                if (a[i][bb] == 1)
                    break;
            }
            if (isboo && temp != -1) {
                for (int i = aa - 1; i > temp; i--)
                    a[i][bb] = 1;
            }
            if (isboo)
                lazi = true;
            isboo = false;


            //left
            int temp1 = -1;
            for (int i = bb - 1; i >= 0; i--) {
                if (a[aa][i] == 2) {
                    if (i - 1 >= 0)
                        if (a[aa][i - 1] == 1) {
                            isboo = true;
                            temp1 = i - 1;
                            break;
                        }
                }
                if (a[aa][i] == 0)
                    break;
                if (a[aa][i] == 1)
                    break;
            }
            if (isboo && temp1 != -1) {
                for (int i = bb - 1; i > temp1; i--)
                    a[aa][i] = 1;
            }
            if (isboo)
                lazi = true;
            isboo = false;

            //down
            int temp2 = -1;
            for (int i = aa + 1; i < 8; i++) {
                if (a[i][bb] == 2) {
                    if (i + 1 < 8)
                        if (a[i + 1][bb] == 1) {
                            isboo = true;
                            temp2 = i + 1;
                            break;
                        }
                }
                if (a[i][bb] == 0)
                    break;
                if (a[i][bb] == 1)
                    break;
            }
            if (isboo && temp2 != -1) {
                for (int i = aa + 1; i < temp2; i++)
                    a[i][bb] = 1;
            }
            if (isboo)
                lazi = true;
            isboo = false;

            //right
            int temp3 = -1;
            for (int i = bb + 1; i < 8; i++) {
                if (a[aa][i] == 2) {
                    if (i + 1 < 8)
                        if (a[aa][i + 1] == 1) {
                            isboo = true;
                            temp3 = i + 1;
                            break;
                        }
                }
                if (a[aa][i] == 0)
                    break;
                if (a[aa][i] == 1)
                    break;
            }
            if (isboo && temp3 != -1) {
                for (int i = bb + 1; i < temp3; i++)
                    a[aa][i] = 1;
            }
            if (isboo)
                lazi = true;
            isboo = false;

            //left down
            int temp4 = -1;
            int temp5 = -1;
            for (int k = aa + 1, l = bb - 1; k < 8 && l >= 0; k++, l--) {
                if (a[k][l] == 2) {
                    if (k + 1 < 8 && l - 1 >= 0)
                        if (a[k + 1][l - 1] == 1) {
                            isboo = true;
                            temp4 = k + 1;
                            temp5 = l - 1;
                            break;
                        }
                }
                if (a[k][l] == 0)
                    break;
                if (a[k][l] == 1)
                    break;
            }
            if (isboo && (temp4 != -1 && temp5 != -1)) {
                for (int i = aa + 1, j = bb - 1; i < temp4 && j > temp5; i++, j--)
                    a[i][j] = 1;
            }
            if (isboo)
                lazi = true;
            isboo = false;

            //right down
            int temp6 = -1;
            int temp7 = -1;
            for (int k = aa + 1, l = bb + 1; k < 8 && l < 8; k++, l++) {
                if (a[k][l] == 2) {

                    if (k + 1 < 8 && l + 1 < 8)
                        if (a[k + 1][l + 1] == 1) {
                            isboo = true;
                            temp6 = k + 1;
                            temp7 = l + 1;
                            break;
                        }
                }
                if (a[k][l] == 0)
                    break;
                if (a[k][l] == 1)
                    break;
            }
            if (isboo && temp6 != -1 && temp7 != -1) {
                for (int i = aa + 1, j = bb + 1; i < temp6 && j < temp7; i++, j++)
                    a[i][j] = 1;
            }
            if (isboo)
                lazi = true;
            isboo = false;

            //left up
            int temp8 = -1;
            int temp9 = -1;
            for (int k = aa - 1, l = bb - 1; k >= 0 && l >= 0; k--, l--) {
                if (a[k][l] == 2) {
                    if (k - 1 >= 0 && l - 1 >= 0)
                        if (a[k - 1][l - 1] == 1) {
                            isboo = true;
                            temp8 = k - 1;
                            temp9 = l - 1;
                            break;
                        }
                }
                if (a[k][l] == 0)
                    break;
                if (a[k][l] == 1)
                    break;
            }
            if (isboo && (temp8 != -1 && temp9 != -1)) {
                for (int i = aa - 1, j = bb - 1; i > temp8 && j > temp9; i--, j--)
                    a[i][j] = 1;
            }
            if (isboo)
                lazi = true;
            isboo = false;

            //right up
            int temp10 = -1;
            int temp11 = -1;
            for (int k = aa - 1, l = bb + 1; k >= 0 && l < 8; k--, l++) {
                if (a[k][l] == 2) {
                    if (k - 1 >= 0 && l + 1 < 8)
                        if (a[k - 1][l + 1] == 1) {
                            isboo = true;
                            temp10 = k - 1;
                            temp11 = l + 1;
                            break;
                        }
                }
                if (a[k][l] == 0)
                    break;
                if (a[k][l] == 1)
                    break;
            }
            if (isboo && temp10 != -1 && temp11 != -1) {
                for (int i = aa - 1, j = bb + 1; i > temp10 && j < temp11; i--, j++)
                    a[i][j] = 1;
            }
            if (isboo)
                lazi = true;
            isboo = false;

        }

        else {

            a[aa][bb] = 2;

            //up
            int temp = -1;
            for (int i = aa - 1; i >= 0; i--) {
                if (a[i][bb] == 1) {
                    if (i - 1 >= 0)
                        if (a[i - 1][bb] == 2) {
                            isboo = true;
                            temp = i - 1;
                            break;
                        }
                }
                if (a[i][bb] == 0)
                    break;
                if (a[i][bb] == 2)
                    break;
            }
            if (isboo && temp != -1) {
                for (int i = aa - 1; i > temp; i--)
                    a[i][bb] = 2;
            }
            if (isboo)
                lazi = true;
            isboo = false;


            //left
            int temp1 = -1;
            for (int i = bb - 1; i >= 0; i--) {
                if (a[aa][i] == 1) {
                    if (i - 1 >= 0)
                        if (a[aa][i - 1] == 2) {
                            isboo = true;
                            temp1 = i - 1;
                            break;
                        }
                }
                if (a[aa][i] == 0)
                    break;
                if (a[aa][i] == 2)
                    break;
            }
            if (isboo && temp1 != -1) {
                for (int i = bb - 1; i > temp1; i--)
                    a[aa][i] = 2;
            }
            if (isboo)
                lazi = true;
            isboo = false;

            //down
            int temp2 = -1;
            for (int i = aa + 1; i < 8; i++) {
                if (a[i][bb] == 1) {
                    if (i + 1 < 8)
                        if (a[i + 1][bb] == 2) {
                            isboo = true;
                            temp2 = i + 1;
                            break;
                        }
                }
                if (a[i][bb] == 0)
                    break;
                if (a[i][bb] == 2)
                    break;
            }
            if (isboo && temp2 != -1) {
                for (int i = aa + 1; i < temp2; i++)
                    a[i][bb] = 2;
            }
            if (isboo)
                lazi = true;
            isboo = false;

            //right
            int temp3 = -1;
            for (int i = bb + 1; i < 8; i++) {
                if (a[aa][i] == 1) {
                    if (i + 1 < 8)
                        if (a[aa][i + 1] == 2) {
                            isboo = true;
                            temp3 = i + 1;
                            break;
                        }
                }
                if (a[aa][i] == 0)
                    break;
                if (a[aa][i] == 2)
                    break;
            }
            if (isboo && temp3 != -1) {
                for (int i = bb + 1; i < temp3; i++)
                    a[aa][i] = 2;
            }
            if (isboo)
                lazi = true;
            isboo = false;

            //left down
            int temp4 = -1;
            int temp5 = -1;
            for (int k = aa + 1, l = bb - 1; k < 8 && l >= 0; k++, l--) {
                if (a[k][l] == 1) {
                    if (k + 1 < 8 && l - 1 >= 0)
                        if (a[k + 1][l - 1] == 2) {
                            isboo = true;
                            temp4 = k + 1;
                            temp5 = l - 1;
                            break;
                        }
                }
                if (a[k][l] == 0)
                    break;
                if (a[k][l] == 2)
                    break;
            }
            if (isboo && (temp4 != -1 && temp5 != -1)) {
                for (int i = aa + 1, j = bb - 1; i < temp4 && j > temp5; i++, j--)
                    a[i][j] = 2;
            }
            if (isboo)
                lazi = true;
            isboo = false;

            //right down
            int temp6 = -1;
            int temp7 = -1;
            for (int k = aa + 1, l = bb + 1; k < 8 && l < 8; k++, l++) {
                if (a[k][l] == 1) {

                    if (k + 1 < 8 && l + 1 < 8)
                        if (a[k + 1][l + 1] == 2) {
                            isboo = true;
                            temp6 = k + 1;
                            temp7 = l + 1;
                            break;
                        }
                }
                if (a[k][l] == 0)
                    break;
                if (a[k][l] == 2)
                    break;
            }
            if (isboo && temp6 != -1 && temp7 != -1) {
                for (int i = aa + 1, j = bb + 1; i < temp6 && j < temp7; i++, j++)
                    a[i][j] = 2;
            }
            if (isboo)
                lazi = true;
            isboo = false;

            //left up
            int temp8 = -1;
            int temp9 = -1;
            for (int k = aa - 1, l = bb - 1; k >= 0 && l >= 0; k--, l--) {
                if (a[k][l] == 1) {
                    if (k - 1 >= 0 && l - 1 >= 0)
                        if (a[k - 1][l - 1] == 2) {
                            isboo = true;
                            temp8 = k - 1;
                            temp9 = l - 1;
                            break;
                        }
                }
                if (a[k][l] == 0)
                    break;
                if (a[k][l] == 2)
                    break;
            }
            if (isboo && (temp8 != -1 && temp9 != -1)) {
                for (int i = aa - 1, j = bb - 1; i > temp8 && j > temp9; i--, j--)
                    a[i][j] = 2;
            }
            if (isboo)
                lazi = true;
            isboo = false;

            //right up
            int temp10 = -1;
            int temp11 = -1;
            for (int k = aa - 1, l = bb + 1; k >= 0 && l < 8; k--, l++) {
                if (a[k][l] == 1) {
                    if (k - 1 >= 0 && l + 1 < 8)
                        if (a[k - 1][l + 1] == 2) {
                            isboo = true;
                            temp10 = k - 1;
                            temp11 = l + 1;
                            break;
                        }
                }
                if (a[k][l] == 0)
                    break;
                if (a[k][l] == 2)
                    break;
            }
            if (isboo && temp10 != -1 && temp11 != -1) {
                for (int i = aa - 1, j = bb + 1; i > temp10 && j < temp11; i--, j++)
                    a[i][j] = 2;
            }
            if (isboo)
                lazi = true;
            isboo = false;

        }

        return lazi;
    }

    private void end()
    {
        int black=0;
        int white=0;
        if(!check())
        {
            count++;
            if(!check())
            {
                for(int i=0;i<8;i++)
                {
                    for(int j=0;j<8;j++)
                    {
                        if(a[i][j]==1)
                            black++;
                        if(a[i][j]==2)
                            white++;
                    }
                }
                winner(black , white);
            }
            else
            {
                nowhere();
                if(count%2==0)
                {
                    if(Degree_of_difficulty==3)
                        hardAI();
                }
                else
                {
                    end();
                }
            }
        }
        else
        {
            for(int i=0;i<8;i++)
            {
                for(int j=0;j<8;j++)
                {
                    if(a[i][j]==1)
                        black++;
                    if(a[i][j]==2)
                        white++;
                }
            }
            if(black+white==64)
            {
                winner(black , white);
            }
        }
    }

    public void winner(int black , int white)
    {
        if(black>white)
            showToast("黑方赢");

        if(black<white)
            showToast("白方赢");

        if(black==white)
            showToast("平局");

    }

    public void nowhere()
    {
        showToast("换人");

    }

    private boolean check()
    {
        boolean ok=false;
        if(count%2==1)
        {
            for(int i=0;i<8;i++)
            {
                for(int j=0;j<8;j++)
                {
                    if(a[i][j]==1)
                    {
                        //up
                        for(int c=i-1;c>=0;c--)
                        {
                            if(a[c][j]==1)
                                break;
                            if(a[c][j]==0)
                                break;
                            if(a[c][j]==2)
                                if(c-1>=0)
                                    if(a[c-1][j]==0)
                                        return true;
                        }
                        //down
                        for(int c=i+1;c<8;c++)
                        {
                            if(a[c][j]==1)
                                break;
                            if(a[c][j]==0)
                                break;
                            if(a[c][j]==2)
                                if(c+1<8)
                                    if(a[c+1][j]==0)
                                        return true;
                        }
                        //left
                        for(int c=i-1;c>=0;c--)
                        {
                            if(a[i][c]==1)
                                break;
                            if(a[i][c]==0)
                                break;
                            if(a[i][c]==2)
                                if(c-1>=0)
                                    if(a[i][c-1]==0)
                                        return true;
                        }
                        //right
                        for(int c=i+1;c<8;c++)
                        {
                            if(a[i][c]==1)
                                break;
                            if(a[i][c]==0)
                                break;
                            if(a[i][c]==2)
                                if(c+1<8)
                                    if(a[i][c+1]==0)
                                        return true;
                        }
                        //right up
                        for(int k=i-1,l=j+1; k>=0&&l<8;k--,l++)
                        {
                            if(a[k][l]==1)
                                break;
                            if(a[k][l]==0)
                                break;
                            if(a[k][l]==2)
                                if(k-1 >= 0 && l+1 < 8)
                                    if(a[k-1][l+1]==0)
                                        return true;
                        }

                        //right down
                        for(int k=i+1,l=j+1; k<8&&l<8;k++,l++)
                        {
                            if(a[k][l]==1)
                                break;
                            if(a[k][l]==0)
                                break;
                            if(a[k][l]==2)
                                if(k+1 < 8 && l+1 < 8)
                                    if(a[k+1][l+1]==0)
                                        return true;
                        }

                        //left up
                        for(int k=i-1,l=j-1; k>=0&&l>=0;k--,l--)
                        {
                            if(a[k][l]==1)
                                break;
                            if(a[k][l]==0)
                                break;
                            if(a[k][l]==2)
                                if(k-1 >= 0 && l-1 >= 0)
                                    if(a[k-1][l-1]==0)
                                        return true;
                        }

                        //left down
                        for(int k=i+1,l=j-1; k<8&&l>=0;k++,l--)
                        {
                            if(a[k][l]==1)
                                break;
                            if(a[k][l]==0)
                                break;
                            if(a[k][l]==2)
                                if(k+1 < 8 && l-1 >= 0)
                                    if(a[k+1][l-1]==0)
                                        return true;
                        }
                    }
                }
            }
        }
        else
        {
            for(int i=0;i<8;i++)
            {
                for(int j=0;j<8;j++)
                {
                    if(a[i][j]==2)
                    {
                        //up
                        for(int c=i-1;c>=0;c--)
                        {
                            if(a[c][j]==2)
                                break;
                            if(a[c][j]==0)
                                break;
                            if(a[c][j]==1)
                                if(c-1>=0)
                                    if(a[c-1][j]==0)
                                        return true;
                        }
                        //down
                        for(int c=i+1;c<8;c++)
                        {
                            if(a[c][j]==2)
                                break;
                            if(a[c][j]==0)
                                break;
                            if(a[c][j]==1)
                                if(c+1<8)
                                    if(a[c+1][j]==0)
                                        return true;
                        }
                        //left
                        for(int c=i-1;c>=0;c--)
                        {
                            if(a[i][c]==2)
                                break;
                            if(a[i][c]==0)
                                break;
                            if(a[i][c]==1)
                                if(c-1>=0)
                                    if(a[i][c-1]==0)
                                        return true;
                        }
                        //right
                        for(int c=i+1;c<8;c++)
                        {
                            if(a[i][c]==2)
                                break;
                            if(a[i][c]==0)
                                break;
                            if(a[i][c]==1)
                                if(c+1<8)
                                    if(a[i][c+1]==0)
                                        return true;
                        }
                        //right up
                        for(int k=i-1,l=j+1; k>=0&&l<8;k--,l++)
                        {
                            if(a[k][l]==2)
                                break;
                            if(a[k][l]==0)
                                break;
                            if(a[k][l]==1)
                                if(k-1 >= 0 && l+1 < 8)
                                    if(a[k-1][l+1]==0)
                                        return true;
                        }

                        //right down
                        for(int k=i+1,l=j+1; k<8&&l<8;k++,l++)
                        {
                            if(a[k][l]==2)
                                break;
                            if(a[k][l]==0)
                                break;
                            if(a[k][l]==1)
                                if(k+1 < 8 && l+1 < 8)
                                    if(a[k+1][l+1]==0)
                                        return true;
                        }

                        //left up
                        for(int k=i-1,l=j-1; k>=0&&l>=0;k--,l--)
                        {
                            if(a[k][l]==2)
                                break;
                            if(a[k][l]==0)
                                break;
                            if(a[k][l]==1)
                                if(k-1 >= 0 && l-1 >= 0)
                                    if(a[k-1][l-1]==0)
                                        return true;
                        }

                        //left down
                        for(int k=i+1,l=j-1; k<8&&l>=0;k++,l--)
                        {
                            if(a[k][l]==2)
                                break;
                            if(a[k][l]==0)
                                break;
                            if(a[k][l]==1)
                                if(k+1 < 8 && l-1 >= 0)
                                    if(a[k+1][l-1]==0)
                                        return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void checkMobility()
    {
        mobility=0;

        if(count%2==1)
        {
            for(int i=0;i<8;i++)
            {
                for(int j=0;j<8;j++)
                {
                    if(a[i][j]==1)
                    {
                        //up
                        for(int c=i-1;c>=0;c--)
                        {
                            if(a[c][j]==1)
                                break;
                            if(a[c][j]==0)
                                break;
                            if(a[c][j]==2)
                                if(c-1>=0)
                                    if(a[c-1][j]==0)
                                        mobility++;
                        }
                        //down
                        for(int c=i+1;c<8;c++)
                        {
                            if(a[c][j]==1)
                                break;
                            if(a[c][j]==0)
                                break;
                            if(a[c][j]==2)
                                if(c+1<8)
                                    if(a[c+1][j]==0)
                                        mobility++;
                        }
                        //left
                        for(int c=i-1;c>=0;c--)
                        {
                            if(a[i][c]==1)
                                break;
                            if(a[i][c]==0)
                                break;
                            if(a[i][c]==2)
                                if(c-1>=0)
                                    if(a[i][c-1]==0)
                                        mobility++;
                        }
                        //right
                        for(int c=i+1;c<8;c++)
                        {
                            if(a[i][c]==1)
                                break;
                            if(a[i][c]==0)
                                break;
                            if(a[i][c]==2)
                                if(c+1<8)
                                    if(a[i][c+1]==0)
                                        mobility++;
                        }
                        //right up
                        for(int k=i-1,l=j+1; k>=0&&l<8;k--,l++)
                        {
                            if(a[k][l]==1)
                                break;
                            if(a[k][l]==0)
                                break;
                            if(a[k][l]==2)
                                if(k-1 >= 0 && l+1 < 8)
                                    if(a[k-1][l+1]==0)
                                        mobility++;
                        }

                        //right down
                        for(int k=i+1,l=j+1; k<8&&l<8;k++,l++)
                        {
                            if(a[k][l]==1)
                                break;
                            if(a[k][l]==0)
                                break;
                            if(a[k][l]==2)
                                if(k+1 < 8 && l+1 < 8)
                                    if(a[k+1][l+1]==0)
                                        mobility++;
                        }

                        //left up
                        for(int k=i-1,l=j-1; k>=0&&l>=0;k--,l--)
                        {
                            if(a[k][l]==1)
                                break;
                            if(a[k][l]==0)
                                break;
                            if(a[k][l]==2)
                                if(k-1 >= 0 && l-1 >= 0)
                                    if(a[k-1][l-1]==0)
                                        mobility++;
                        }

                        //left down
                        for(int k=i+1,l=j-1; k<8&&l>=0;k++,l--)
                        {
                            if(a[k][l]==1)
                                break;
                            if(a[k][l]==0)
                                break;
                            if(a[k][l]==2)
                                if(k+1 < 8 && l-1 >= 0)
                                    if(a[k+1][l-1]==0)
                                        mobility++;
                        }
                    }
                }
            }
        }
        else
        {
            for(int i=0;i<8;i++)
            {
                for(int j=0;j<8;j++)
                {
                    if(a[i][j]==2)
                    {
                        //up
                        for(int c=i-1;c>=0;c--)
                        {
                            if(a[c][j]==2)
                                break;
                            if(a[c][j]==0)
                                break;
                            if(a[c][j]==1)
                                if(c-1>=0)
                                    if(a[c-1][j]==0)
                                        mobility++;
                        }
                        //down
                        for(int c=i+1;c<8;c++)
                        {
                            if(a[c][j]==2)
                                break;
                            if(a[c][j]==0)
                                break;
                            if(a[c][j]==1)
                                if(c+1<8)
                                    if(a[c+1][j]==0)
                                        mobility++;
                        }
                        //left
                        for(int c=i-1;c>=0;c--)
                        {
                            if(a[i][c]==2)
                                break;
                            if(a[i][c]==0)
                                break;
                            if(a[i][c]==1)
                                if(c-1>=0)
                                    if(a[i][c-1]==0)
                                        mobility++;
                        }
                        //right
                        for(int c=i+1;c<8;c++)
                        {
                            if(a[i][c]==2)
                                break;
                            if(a[i][c]==0)
                                break;
                            if(a[i][c]==1)
                                if(c+1<8)
                                    if(a[i][c+1]==0)
                                        mobility++;
                        }
                        //right up
                        for(int k=i-1,l=j+1; k>=0&&l<8;k--,l++)
                        {
                            if(a[k][l]==2)
                                break;
                            if(a[k][l]==0)
                                break;
                            if(a[k][l]==1)
                                if(k-1 >= 0 && l+1 < 8)
                                    if(a[k-1][l+1]==0)
                                        mobility++;
                        }

                        //right down
                        for(int k=i+1,l=j+1; k<8&&l<8;k++,l++)
                        {
                            if(a[k][l]==2)
                                break;
                            if(a[k][l]==0)
                                break;
                            if(a[k][l]==1)
                                if(k+1 < 8 && l+1 < 8)
                                    if(a[k+1][l+1]==0)
                                        mobility++;
                        }

                        //left up
                        for(int k=i-1,l=j-1; k>=0&&l>=0;k--,l--)
                        {
                            if(a[k][l]==2)
                                break;
                            if(a[k][l]==0)
                                break;
                            if(a[k][l]==1)
                                if(k-1 >= 0 && l-1 >= 0)
                                    if(a[k-1][l-1]==0)
                                        mobility++;
                        }

                        //left down
                        for(int k=i+1,l=j-1; k<8&&l>=0;k++,l--)
                        {
                            if(a[k][l]==2)
                                break;
                            if(a[k][l]==0)
                                break;
                            if(a[k][l]==1)
                                if(k+1 < 8 && l-1 >= 0)
                                    if(a[k+1][l-1]==0)
                                        mobility++;
                        }
                    }
                }
            }
        }

        if (mobility == 0) {
            mobility_point = -100 ;
        }
        else if(mobility == 1){
            mobility_point = -60;
        }
        else if(mobility == 2){
            mobility_point = 0;
        }
        else if(mobility > 2 && mobility < 5){
            mobility_point = 20;
        }
        else{
            mobility_point = 40;
        }

    }

    private void showToast(String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        }
        mToast.setText(msg);
        mToast.show();
    }

    class Board{

        final int[][] board;
        int x = 0, y = 0;
        int point = -200;


        Board(){
            board=new int[8][8];
        }

        void board_record(int newBoard[][]){

            for(int i = 0; i < newBoard.length; i++)
                for(int j = 0; j < newBoard[i].length; j++)
                    board[i][j] = newBoard[i][j];
        }

        void board_record(int newBoard[][], int x, int y, int point){

            for(int i = 0; i < newBoard.length; i++)
                for(int j = 0; j < newBoard[i].length; j++)
                    board[i][j] = newBoard[i][j];


            this.x = x;
            this.y = y;
            this.point = point;
        }

        public void next_board(Board aNewBoard){
            Board preBoard = new Board();
            preBoard = temp_board;
            temp_board = aNewBoard;
        }

        /*public void pre_board(){
            temp_board = preBoard;
        }*/

        int[][] board_state(){
            return board;
        }

        int getX(){
            return x;
        }

        int getY(){
            return y;
        }

        int getPoint(){
            return point;
        }
    }
}

