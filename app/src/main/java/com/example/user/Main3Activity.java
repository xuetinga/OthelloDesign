package com.example.user;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import  android.graphics.*;
import android.view.*;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Main3Activity extends AppCompatActivity{
    public final int[][] a=new int[8][8];
    private final int level=Main2Activity.level;
    private int x=0;
    private int y=0;
    private int count=1;
    private static boolean isboo=false;
    private static final int[][] b=new int[8][8];
    private static int tempa=0;
    private static int tempb=0;
    private static int temp=-200;
    private static int number=0;
    private static int AIpoint=-100;
    private static int Playerpoint=-100;

    Main3Activity.CustomView test;
    private int[][] pre_board = new int[8][8];
    private boolean is_pre = false;

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

        setContentView(R.layout.activity_main3);//R.layout.activity_main4
        LinearLayout layout=(LinearLayout) findViewById(R.id.root);
        btn1_clear = (Button) findViewById(R.id.btn_reset);
        btn2_last = (Button) findViewById(R.id.button_return);
        btn3_choose = (Button) findViewById(R.id.btn_decidemode);
        test = new Main3Activity.CustomView(this );
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
                            if(level == 1)
                                easyAI();
                            if(level == 2)
                                normalAI();
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
                intent.setClass(Main3Activity.this,Main2Activity.class);
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
        if(level!=0 && check())
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
            if(level!=0)
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
            showToast("不能下在这里");

        }
    }

    private boolean change(int aa, int bb)
    {
        boolean lazi = false;
        //黑子
        if(a[aa][bb] == 1)
        {
            //上

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
    private void easyAI()
    {

        for(int i=0 ; i<8 ; i++)
        {
            for(int j=0 ; j<8 ; j++)
            {
                if(a[i][j]==2)
                    easy(i,j);
            }
        }
        temp=0;
        number=0;
        a[tempa][tempb]=2;
        change(tempa , tempb);
        test.invalidate();
        count++;
        end();
    }
    private void easy(int aa, int bb)
    {
        //up
        for(int i=aa-1;i>=0;i--)
        {
            if(a[i][bb]==2)
                break;
            if(a[i][bb]==0)
                break;
            if(a[i][bb]==1)
            {
                if(i-1 >= 0)
                    if(a[i-1][bb]==0)
                    {
                        temp=aa-i;
                        if(temp > number)
                        {
                            number = temp;
                            tempa=i-1;
                            tempb=bb;
                            break;
                        }
                    }
            }
        }

        //down
        for(int i=aa+1;i<8;i++)
        {
            if(a[i][bb]==2)
                break;
            if(a[i][bb]==0)
                break;
            if(a[i][bb]==1)
            {
                if(i+1 < 8)
                    if(a[i+1][bb]==0)
                    {
                        temp=i-aa;
                        if(temp > number)
                        {
                            number = temp;
                            tempa=i+1;
                            tempb=bb;
                            break;
                        }
                    }
            }
        }

        //left
        for(int i=bb-1;i>=0;i--)
        {
            if(a[aa][i]==2)
                break;
            if(a[aa][i]==0)
                break;
            if(a[aa][i]==1)
            {
                if(i-1 >= 0)
                    if(a[aa][i-1]==0)
                    {
                        temp=bb-i;
                        if(temp > number)
                        {
                            number = temp;
                            tempa=aa;
                            tempb=i-1;
                            break;
                        }
                    }
            }
        }

        //right
        for(int i=bb+1;i<8;i++)
        {
            if(a[aa][i]==2)
                break;
            if(a[aa][i]==0)
                break;
            if(a[aa][i]==1)
            {
                if(i+1 < 8)
                    if(a[aa][i+1]==0)
                    {
                        temp=i-bb;
                        if(temp > number)
                        {
                            number = temp;
                            tempa=aa;
                            tempb=i+1;
                            break;
                        }
                    }
            }
        }

        //right up
        for(int k=aa-1,l=bb+1; k>=0&&l<8;k--,l++)
        {
            if(a[k][l]==2)
                break;
            if(a[k][l]==0)
                break;
            if(a[k][l]==1)
            {
                if(k-1 >= 0 && l+1 < 8)
                    if(a[k-1][l+1]==0)
                    {
                        temp=aa-k;
                        if(temp > number)
                        {
                            number = temp;
                            tempa=k-1;
                            tempb=l+1;
                            break;
                        }
                    }
            }
        }

        //right down
        for(int k=aa+1,l=bb+1; k<8&&l<8;k++,l++)
        {
            if(a[k][l]==2)
                break;
            if(a[k][l]==0)
                break;
            if(a[k][l]==1)
            {
                if(k+1 < 8 && l+1 < 8)
                    if(a[k+1][l+1]==0)
                    {
                        temp=k-aa;
                        if(temp > number)
                        {
                            number = temp;
                            tempa=k+1;
                            tempb=l+1;
                            break;
                        }
                    }
            }
        }

        //left up
        for(int k=aa-1,l=bb-1; k>=0&&l>=0;k--,l--)
        {
            if(a[k][l]==2)
                break;
            if(a[k][l]==0)
                break;
            if(a[k][l]==1)
            {
                if(k-1 >= 0 && l-1 >= 0)
                    if(a[k-1][l-1]==0)
                    {
                        temp=aa-k;
                        if(temp > number)
                        {
                            number = temp;
                            tempa=k-1;
                            tempb=l-1;
                            break;
                        }
                    }
            }
        }

        //left down
        for(int k=aa+1,l=bb-1; k<8&&l>=0;k++,l--)
        {
            if(a[k][l]==2)
                break;
            if(a[k][l]==0)
                break;
            if(a[k][l]==1)
            {
                if(k+1 < 8 && l-1 >= 0)
                    if(a[k+1][l-1]==0)
                    {
                        temp=k-aa;
                        if(temp > number)
                        {
                            number = temp;
                            tempa=k+1;
                            tempb=l-1;
                            break;
                        }
                    }
            }
        }
    }

    private void normalAI()
    {
        try
        {
            Thread.sleep(1000);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        for(int i=0 ; i<8 ; i++)
        {
            for(int j=0 ; j<8 ; j++)
            {
                if(a[i][j]==2)
                    normal(i,j);
            }
        }
        temp=-200;

        a[tempa][tempb]=2;
        change(tempa , tempb);
        test.invalidate();
        count++;
        end();
    }

    private void b_normal(int aa, int bb)
    {
        int point[][]= {{90,-60,10,10,10,10,-60,90},
                {-60,-80,5,5,5,5,-80,-60},
                {10,5,1,1,1,1,5,10},
                {10,5,1,1,1,1,5,10},
                {10,5,1,1,1,1,5,10},
                {10,5,1,1,1,1,5,10},
                {-60,-80,5,5,5,5,-80,-60},
                {90,-60,10,10,10,10,-60,90}};


        //up
        for(int i=aa-1;i>=0;i--)
        {
            if(b[i][bb]==1)
                break;
            if(b[i][bb]==0)
                break;
            if(b[i][bb]==2)
            {
                if(i-1 >= 0)
                    if(b[i-1][bb]==0)
                    {
                        if(point[i-1][bb]>Playerpoint)
                        {
                            Playerpoint=point[i-1][bb];
                        }
                    }
            }
        }

        //down
        for(int i=aa+1;i<8;i++)
        {
            if(b[i][bb]==1)
                break;
            if(b[i][bb]==0)
                break;
            if(b[i][bb]==2)
            {
                if(i+1 < 8)
                    if(b[i+1][bb]==0)
                    {
                        if(point[i+1][bb]>Playerpoint)
                        {
                            Playerpoint=point[i+1][bb];
                        }
                    }
            }

        }

        //left
        for(int i=bb-1;i>=0;i--)
        {
            if(b[aa][i]==1)
                break;
            if(b[aa][i]==0)
                break;
            if(b[aa][i]==2)
            {
                if(i-1 >= 0)
                    if(b[aa][i-1]==0)
                    {
                        if(point[aa][i-1]>Playerpoint)
                        {
                            Playerpoint=point[aa][i-1];
                        }
                    }
            }

        }

        //right
        for(int i=bb+1;i<8;i++)
        {
            if(b[aa][i]==1)
                break;
            if(b[aa][i]==0)
                break;
            if(b[aa][i]==2)
            {
                if(i+1 < 8)
                    if(b[aa][i+1]==0)
                    {
                        if(point[aa][i+1]>Playerpoint)
                        {
                            Playerpoint=point[aa][i+1];
                        }
                    }
            }

        }

        //right up
        for(int k=aa-1,l=bb+1; k>=0&&l<8;k--,l++)
        {
            if(b[k][l]==1)
                break;
            if(b[k][l]==0)
                break;
            if(b[k][l]==2)
            {
                if(k-1 >= 0 && l+1 < 8)
                    if(b[k-1][l+1]==0)
                    {
                        if(point[k-1][l+1]>Playerpoint)
                        {
                            Playerpoint=point[k-1][l+1];
                        }
                    }
            }

        }

        //right down
        for(int k=aa+1,l=bb+1; k<8&&l<8;k++,l++)
        {
            if(b[k][l]==1)
                break;
            if(b[k][l]==0)
                break;
            if(b[k][l]==2)
            {
                if(k+1 < 8 && l+1 < 8)
                    if(b[k+1][l+1]==0)
                    {
                        if(point[k+1][l+1]>Playerpoint)
                        {
                            Playerpoint=point[k+1][l+1];
                        }
                    }
            }

        }

        //left up
        for(int k=aa-1,l=bb-1; k>=0&&l>=0;k--,l--)
        {
            if(b[k][l]==1)
                break;
            if(b[k][l]==0)
                break;
            if(b[k][l]==2)
            {
                if(k-1 >= 0 && l-1 >= 0)
                    if(b[k-1][l-1]==0)
                    {
                        if(point[k-1][l-1]>Playerpoint)
                        {
                            Playerpoint=point[k-1][l-1];
                        }
                    }
            }

        }

        //left down
        for(int k=aa+1,l=bb-1; k<8&&l>=0;k++,l--)
        {
            if(b[k][l]==1)
                break;
            if(b[k][l]==0)
                break;
            if(b[k][l]==2)
            {
                if(k+1 < 8 && l-1 >= 0)
                    if(b[k+1][l-1]==0)
                    {
                        if(point[k+1][l-1]>Playerpoint)
                        {
                            Playerpoint=point[k+1][l-1];
                        }
                    }
            }

        }

    }

    private void normal(int aa, int bb)
    {
        int point[][]= {{90,-60,10,10,10,10,-60,90},
                {-60,-80,5,5,5,5,-80,-60},
                {10,5,1,1,1,1,5,10},
                {10,5,1,1,1,1,5,10},
                {10,5,1,1,1,1,5,10},
                {10,5,1,1,1,1,5,10},
                {-60,-80,5,5,5,5,-80,-60},
                {90,-60,10,10,10,10,-60,90}};
        int aaa,bbb;
        //up
        for(int i=aa-1;i>=0;i--)
        {
            if(a[i][bb]==2)
                break;
            if(a[i][bb]==0)
                break;
            if(a[i][bb]==1)
            {
                if(i-1 >= 0)
                    if(a[i-1][bb]==0)
                    {
                        if(point[i-1][bb]>AIpoint)
                        {
                            AIpoint=point[i-1][bb];
                            aaa=i-1;
                            bbb=bb;
                            normal_change(aaa,bbb);

                            for(int l=0; l<8 ; l++)
                            {
                                for(int j=0 ; j<8 ; j++)
                                {
                                    if(b[l][j]==1)
                                        b_normal(l,j);
                                }
                            }

                            if((AIpoint-Playerpoint)>temp)
                            {
                                temp=AIpoint-Playerpoint;
                                tempa=aaa;
                                tempb=bbb;
                            }
                            AIpoint=-100;
                            Playerpoint=-100;
                        }
                    }
            }
        }

        //down
        for(int i=aa+1;i<8;i++)
        {
            if(a[i][bb]==2)
                break;
            if(a[i][bb]==0)
                break;
            if(a[i][bb]==1)
            {
                if(i+1 < 8)
                    if(a[i+1][bb]==0)
                    {
                        if(point[i+1][bb]>AIpoint)
                        {
                            AIpoint=point[i+1][bb];
                            aaa=i+1;
                            bbb=bb;
                            normal_change(aaa,bbb);

                            for(int l=0; l<8 ; l++)
                            {
                                for(int j=0 ; j<8 ; j++)
                                {
                                    if(b[l][j]==1)
                                        b_normal(l,j);
                                }
                            }

                            if((AIpoint-Playerpoint)>temp)
                            {
                                temp=AIpoint-Playerpoint;
                                tempa=aaa;
                                tempb=bbb;
                            }
                            AIpoint=-100;
                            Playerpoint=-100;
                        }
                    }
            }
        }

        //left
        for(int i=bb-1;i>=0;i--)
        {
            if(a[aa][i]==2)
                break;
            if(a[aa][i]==0)
                break;
            if(a[aa][i]==1)
            {
                if(i-1 >= 0)
                    if(a[aa][i-1]==0)
                    {
                        if(point[aa][i-1]>AIpoint)
                        {
                            AIpoint=point[aa][i-1];
                            aaa=aa;
                            bbb=i-1;
                            normal_change(aaa,bbb);

                            for(int l=0; l<8 ; l++)
                            {
                                for(int j=0 ; j<8 ; j++)
                                {
                                    if(b[l][j]==1)
                                        b_normal(l,j);
                                }
                            }

                            if((AIpoint-Playerpoint)>temp)
                            {
                                temp=AIpoint-Playerpoint;
                                tempa=aaa;
                                tempb=bbb;
                            }
                            AIpoint=-100;
                            Playerpoint=-100;
                        }
                    }
            }
        }

        //right
        for(int i=bb+1;i<8;i++)
        {

            if(a[aa][i]==2)
                break;
            if(a[aa][i]==0)
                break;
            if(a[aa][i]==1)
            {
                if(i+1 < 8)
                    if(a[aa][i+1]==0)
                    {
                        if(point[aa][i+1]>AIpoint)
                        {
                            AIpoint=point[aa][i+1];
                            aaa=aa;
                            bbb=i+1;
                            normal_change(aaa,bbb);

                            for(int l=0; l<8 ; l++)
                            {
                                for(int j=0 ; j<8 ; j++)
                                {
                                    if(b[l][j]==1)
                                        b_normal(l,j);
                                }
                            }

                            if((AIpoint-Playerpoint)>temp)
                            {
                                temp=AIpoint-Playerpoint;
                                tempa=aaa;
                                tempb=bbb;
                            }
                            AIpoint=-100;
                            Playerpoint=-100;
                        }
                    }
            }
        }

        //right up
        for(int k=aa-1,l=bb+1; k>=0&&l<8;k--,l++)
        {
            if(a[k][l]==2)
                break;
            if(a[k][l]==0)
                break;
            if(a[k][l]==1)
            {
                if(k-1 >= 0 && l+1 < 8)
                    if(a[k-1][l+1]==0)
                    {
                        if(point[k-1][l+1]>AIpoint)
                        {
                            AIpoint=point[k-1][l+1];
                            aaa=k-1;
                            bbb=l+1;
                            normal_change(aaa,bbb);

                            for(int i=0; i<8 ; i++)
                            {
                                for(int j=0 ; j<8 ; j++)
                                {
                                    if(b[i][j]==1)
                                        b_normal(i,j);
                                }
                            }

                            if((AIpoint-Playerpoint)>temp)
                            {
                                temp=AIpoint-Playerpoint;
                                tempa=aaa;
                                tempb=bbb;
                            }
                            AIpoint=-100;
                            Playerpoint=-100;
                        }
                    }
            }
        }

        //right down
        for(int k=aa+1,l=bb+1; k<8&&l<8;k++,l++)
        {
            if(a[k][l]==2)
                break;
            if(a[k][l]==0)
                break;
            if(a[k][l]==1)
            {
                if(k+1 < 8 && l+1 < 8)
                    if(a[k+1][l+1]==0)
                    {
                        if(point[k+1][l+1]>AIpoint)
                        {
                            AIpoint=point[k+1][l+1];
                            aaa=k+1;
                            bbb=l+1;
                            normal_change(aaa,bbb);

                            for(int i=0; i<8 ; i++)
                            {
                                for(int j=0 ; j<8 ; j++)
                                {
                                    if(b[i][j]==1)
                                        b_normal(i,j);
                                }
                            }

                            if((AIpoint-Playerpoint)>temp)
                            {
                                temp=AIpoint-Playerpoint;
                                tempa=aaa;
                                tempb=bbb;
                            }
                            AIpoint=-100;
                            Playerpoint=-100;
                        }
                    }
            }
        }

        //left up
        for(int k=aa-1,l=bb-1; k>=0&&l>=0;k--,l--)
        {

            if(a[k][l]==2)
                break;
            if(a[k][l]==0)
                break;
            if(a[k][l]==1)
            {
                if(k-1 >= 0 && l-1 >= 0)
                    if(a[k-1][l-1]==0)
                    {
                        if(point[k-1][l-1]>AIpoint)
                        {
                            AIpoint=point[k-1][l-1];
                            aaa=k-1;
                            bbb=l-1;
                            normal_change(aaa,bbb);

                            for(int i=0; i<8 ; i++)
                            {
                                for(int j=0 ; j<8 ; j++)
                                {
                                    if(b[i][j]==1)
                                        b_normal(i,j);
                                }
                            }

                            if((AIpoint-Playerpoint)>temp)
                            {
                                temp=AIpoint-Playerpoint;
                                tempa=aaa;
                                tempb=bbb;
                            }
                            AIpoint=-100;
                            Playerpoint=-100;
                        }
                    }
            }

        }

        //left down
        for(int k=aa+1,l=bb-1; k<8&&l>=0;k++,l--)
        {

            if(a[k][l]==2)
                break;
            if(a[k][l]==0)
                break;
            if(a[k][l]==1)
            {
                if(k+1 < 8 && l-1 >= 0)
                    if(a[k+1][l-1]==0)
                    {
                        if(point[k+1][l-1]>AIpoint)
                        {
                            AIpoint=point[k+1][l-1];
                            aaa=k+1;
                            bbb=l-1;
                            normal_change(aaa,bbb);

                            for(int i=0; i<8 ; i++)
                            {
                                for(int j=0 ; j<8 ; j++)
                                {
                                    if(b[i][j]==1)
                                        b_normal(i,j);
                                }
                            }

                            if((AIpoint-Playerpoint)>temp)
                            {
                                temp=AIpoint-Playerpoint;
                                tempa=aaa;
                                tempb=bbb;
                            }
                            AIpoint=-100;
                            Playerpoint=-100;
                        }
                    }
            }

        }
    }
    private boolean normal_change(int aa, int bb)
    {
        boolean lazi = false;
        for(int i=0 ; i<8 ;i++)
        {
            System.arraycopy(a[i], 0, b[i], 0, 8);
        }
        b[aa][bb]=2;

        //up
        int temp=-1;
        for(int i=aa-1;i>=0;i--)
        {
            if(b[i][bb]==1)
            {
                if(i-1 >= 0)
                    if(b[i-1][bb]==2)
                    {
                        isboo=true;
                        temp = i-1;
                        break;
                    }
            }
            if(b[i][bb]==0)
                break;
            if(b[i][bb]==2)
                break;
        }
        if(isboo && temp != -1)
        {
            for(int i=aa-1 ; i>temp ; i--)
                b[i][bb]=2;
        }
        if(isboo)
            lazi = true;
        isboo=false;


        //left
        int temp1=-1;
        for(int i=bb-1; i>=0 ; i--)
        {
            if(b[aa][i]==1)
            {
                if(i-1 >= 0)
                    if(b[aa][i-1]==2)
                    {
                        isboo=true;
                        temp1 = i-1;
                        break;
                    }
            }
            if(b[aa][i]==0)
                break;
            if(b[aa][i]==2)
                break;
        }
        if(isboo && temp1 != -1)
        {
            for(int i=bb-1 ; i>temp1 ; i--)
                b[aa][i]=2;
        }
        if(isboo)
            lazi = true;
        isboo=false;

        //down
        int temp2=-1;
        for(int i=aa+1;i<8;i++)
        {
            if(b[i][bb]==1)
            {
                if(i+1 < 8)
                    if(b[i+1][bb]==2)
                    {
                        isboo=true;
                        temp2 = i+1;
                        break;
                    }
            }
            if(b[i][bb]==0)
                break;
            if(b[i][bb]==2)
                break;
        }
        if(isboo && temp2 != -1)
        {
            for(int i=aa+1 ; i<temp2 ; i++)
                b[i][bb]=2;
        }
        if(isboo)
            lazi = true;
        isboo=false;

        //right
        int temp3=-1;
        for(int i=bb+1; i<8 ; i++)
        {
            if(b[aa][i]==1)
            {
                if(i+1 < 8)
                    if(b[aa][i+1]==2)
                    {
                        isboo=true;
                        temp3 = i+1;
                        break;
                    }
            }
            if(b[aa][i]==0)
                break;
            if(b[aa][i]==2)
                break;
        }
        if(isboo && temp3 != -1)
        {
            for(int i=bb+1 ; i<temp3 ; i++)
                b[aa][i]=2;
        }
        if(isboo)
            lazi = true;
        isboo=false;

        //left down
        int temp4=-1;
        int temp5=-1;
        for(int k=aa+1,l=bb-1; k<8&&l>=0;k++,l--)
        {
            if(b[k][l]==1)
            {
                if(k+1 < 8 && l-1 >= 0)
                    if(b[k+1][l-1]==2)
                    {
                        isboo=true;
                        temp4 = k+1;
                        temp5 = l-1;
                        break;
                    }
            }
            if(b[k][l]==0)
                break;
            if(b[k][l]==2)
                break;
        }
        if(isboo && (temp4 != -1 && temp5 != -1))
        {
            for(int i=aa+1 , j=bb-1 ; i<temp4 && j>temp5 ; i++,j--)
                b[i][j]=2;
        }
        if(isboo)
            lazi = true;
        isboo=false;

        //right down
        int temp6=-1;
        int temp7=-1;
        for(int k=aa+1,l=bb+1; k<8&&l<8;k++,l++)
        {
            if(b[k][l]==1)
            {

                if(k+1 < 8 && l+1 < 8)
                    if(b[k+1][l+1]==2)
                    {
                        isboo=true;
                        temp6 = k+1;
                        temp7 = l+1;
                        break;
                    }
            }
            if(b[k][l]==0)
                break;
            if(b[k][l]==2)
                break;
        }
        if(isboo && temp6 != -1 && temp7 != -1)
        {
            for(int i=aa+1 , j=bb+1 ; i<temp6 && j<temp7 ; i++,j++)
                b[i][j]=2;
        }
        if(isboo)
            lazi = true;
        isboo=false;

        //left up
        int temp8=-1;
        int temp9=-1;
        for(int k=aa-1,l=bb-1; k>=0&&l>=0;k--,l--)
        {
            if(b[k][l]==1)
            {
                if(k-1 >= 0 && l-1 >= 0)
                    if(b[k-1][l-1]==2)
                    {
                        isboo=true;
                        temp8 = k-1;
                        temp9 = l-1;
                        break;
                    }
            }
            if(b[k][l]==0)
                break;
            if(b[k][l]==2)
                break;
        }
        if(isboo && (temp8 != -1 && temp9 != -1))
        {
            for(int i=aa-1 , j=bb-1 ; i>temp8 && j>temp9 ; i--,j--)
                b[i][j]=2;
        }
        if(isboo)
            lazi = true;
        isboo=false;

        //right up
        int temp10=-1;
        int temp11=-1;
        for(int k=aa-1,l=bb+1; k>=0&&l<8;k--,l++)
        {
            if(b[k][l]==1)
            {
                if(k-1 >= 0 && l+1 < 8)
                    if(b[k-1][l+1]==2)
                    {
                        isboo=true;
                        temp10 = k-1;
                        temp11 = l+1;
                        break;
                    }
            }
            if(b[k][l]==0)
                break;
            if(b[k][l]==2)
                break;
        }
        if(isboo && temp10 != -1 && temp11 != -1)
        {
            for(int i=aa-1 , j=bb+1 ; i>temp10 && j<temp11 ; i--,j++)
                b[i][j]=2;
        }
        if(isboo)
            lazi = true;
        isboo=false;
        return lazi;
    }//normal end


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
                    if(level==1)
                        easyAI();
                    if(level==2)
                        normalAI();
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
        showToast("没有地方下了");

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

    private void showToast(String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        }
        mToast.setText(msg);
        mToast.show();
    }
}
