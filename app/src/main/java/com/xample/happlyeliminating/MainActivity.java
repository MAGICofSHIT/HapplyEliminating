package com.xample.happlyeliminating;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.xample.happlyeliminating.activity.AboutActivity;
import com.xample.happlyeliminating.activity.HelpActivity;
import com.xample.happlyeliminating.activity.VolumeActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    int[] candies = {
            R.drawable.bluecandy,
            R.drawable.greencandy,
            R.drawable.orangecandy,
            R.drawable.purplecandy,
            R.drawable.yellowcandy,
            R.drawable.redcandy,
    };
    int widthOfBlock,   // 每个糖果图像视图的宽度（以像素为单位）
            noOfBlocks = 8, // 游戏板中每行（或每列）的糖果块数量
            widthOfScreen;  // 屏幕的宽度（以像素为单位）
    ArrayList<ImageView> candy = new ArrayList<>();     // 存储糖果图像视图的ImageView对象列表
    int candyToBeDragged,   // 被拖动的糖果的标识符（ImageView的ID）
            candyToBeReplaced;  // 要替换的糖果的标识符（ImageView的ID）
    int notCandy = R.drawable.transparent;  // 空白糖果的图像资源标识符
    Handler mHandler;   // 用于在指定的时间间隔内执行重复的任务
    int interval = 200; // 重复任务的时间间隔（以毫秒为单位）
    TextView scoreResult;   // 显示分数的TextView对象
    int score = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 处理导航菜单项的点击事件
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        scoreResult = findViewById(R.id.score);

        // 获取屏幕的显示度量信息并存储在displayMetrics中
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        widthOfScreen = displayMetrics.widthPixels;     // 屏幕宽度
        widthOfBlock = widthOfScreen / noOfBlocks;      // 计算每个糖果块的宽度，确保每个糖果块在屏幕上均匀分布，并根据屏幕宽度适应不同尺寸的屏幕

        createBoard();

        for (final ImageView imageView : candy) {    // 遍历存储糖果图像视图的ImageView对象列表，以监听每个imageView的滑动手势事件
            imageView.setOnTouchListener(new OnSwipeListener(this) {
                @Override
                public void onSwipeLeft() {     // 检测到向左滑动的手势
                    super.onSwipeLeft();
//                    Toast.makeText(MainActivity.this, "Left", Toast.LENGTH_SHORT).show();
                    candyToBeDragged = imageView.getId();
                    candyToBeReplaced = candyToBeDragged - 1;
                    candyInterchange();
                }

                @Override
                public void onSwipeRight() {    // 检测到向右滑动的手势
                    super.onSwipeRight();
//                    Toast.makeText(MainActivity.this, "Right", Toast.LENGTH_SHORT).show();
                    candyToBeDragged = imageView.getId();
                    candyToBeReplaced = candyToBeDragged + 1;
                    candyInterchange();
                }

                @Override
                public void onSwipeTop() {      // 检测到向上滑动的手势
                    super.onSwipeTop();
                    candyToBeDragged = imageView.getId();
                    candyToBeReplaced = candyToBeDragged - noOfBlocks;
                    candyInterchange();
                }

                @Override
                public void onSwipeBottom() {   // 检测到向下滑动的手势
                    super.onSwipeBottom();
                    candyToBeDragged = imageView.getId();
                    candyToBeReplaced = candyToBeDragged + noOfBlocks;
                    candyInterchange();
                }
            });
        }

        // 用于定时重复检查每行每列是否有三个相同颜色的糖果并消除
        mHandler = new Handler();
        startRepeat();

        // 为"重置分数"按钮设置点击事件监听器，用于将游戏分数重置为0
        findViewById(R.id.bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scoreResult.setText("0");
                score = 0;
            }
        });
    }

    // 检查行
    private void checkRawForThree() {
        for (int i = 0; i < 62; i++) {
            int chosedCandy = (int) candy.get(i).getTag();
            boolean isBlank = (int) candy.get(i).getTag() == notCandy;   // 检查当前位置的糖果是否为空白糖果
            Integer[] notValid = {6, 7, 14, 15, 22, 23, 30, 31, 38, 39, 46, 47, 54, 55};    // 这些位置是不需要进行检查的，即不满足三个相同颜色的糖果消除条件
            List<Integer> list = Arrays.asList(notValid);   //  将notValid数组转换为List集合，以便后续判断当前位置是否在不需要检查的列表中
            if (!list.contains(i)) {
                int x = i;
                if ((int) candy.get(x++).getTag() == chosedCandy && !isBlank &&
                        (int) candy.get(x++).getTag() == chosedCandy &&
                        (int) candy.get(x).getTag() == chosedCandy) {
                    score = score + 3;
                    scoreResult.setText(String.valueOf(score));
                    candy.get(x).setImageResource(notCandy);
                    candy.get(x).setTag(notCandy);
                    x--;
                    candy.get(x).setImageResource(notCandy);
                    candy.get(x).setTag(notCandy);
                    x--;
                    candy.get(x).setImageResource(notCandy);
                    candy.get(x).setTag(notCandy);
                }
            }
        }
        moveDownCandies();
    }

    private void checkColumnForThree() {
        for (int i = 0; i < 48; i++) {
            int chosedCandy = (int) candy.get(i).getTag();
            boolean isBlank = (int) candy.get(i).getTag() == notCandy;   // 检查当前位置的糖果是否为空白糖果
            int x = i;
            if ((int) candy.get(x).getTag() == chosedCandy && !isBlank &&
                    (int) candy.get(x + noOfBlocks).getTag() == chosedCandy &&
                    (int) candy.get(x + 2 * noOfBlocks).getTag() == chosedCandy) {
                score = score + 3;
                scoreResult.setText(String.valueOf(score));
                candy.get(x).setImageResource(notCandy);
                candy.get(x).setTag(notCandy);
                x = x + noOfBlocks;
                candy.get(x).setImageResource(notCandy);
                candy.get(x).setTag(notCandy);
                x = x + noOfBlocks;
                candy.get(x).setImageResource(notCandy);
                candy.get(x).setTag(notCandy);
            }
        }
        moveDownCandies();
    }

    private void moveDownCandies() {
        Integer[] firstRow = {0, 1, 2, 3, 4, 5, 6, 7};
        List<Integer> list = Arrays.asList(firstRow);   // 将firstRow数组转换为List集合，以便后续判断当前位置是否在第一行
        for (int i = 55; i >= 0; i--) {// 从倒数第二行开始遍历
            if ((int) candy.get(i + noOfBlocks).getTag() == notCandy) {  // 判断当前位置下方是否是空白糖果
                // 将当前糖果往下移动至空白糖果处
                candy.get(i + noOfBlocks).setImageResource((int) candy.get(i).getTag());
                candy.get(i + noOfBlocks).setTag(candy.get(i).getTag());

                // 将当前糖果设置为空白糖果
                candy.get(i).setImageResource(notCandy);
                candy.get(i).setTag(notCandy);

                if (list.contains(i) && (int) candy.get(i).getTag() == notCandy) {   // 如果当前位置在第一行，并且下方位置是空白糖果，则表示需要在当前位置生成新的糖果
                    int randomColor = (int) Math.floor(Math.random() * candies.length);
                    candy.get(i).setImageResource(candies[randomColor]);
                    candy.get(i).setTag(candies[randomColor]);
                }
            }
        }

        // 再次遍历游戏板的第一行，如果在第一行发现空白糖果，则生成新的糖果填充空缺
        for (int i = 0; i < 8; i++) {
            if ((int) candy.get(i).getTag() == notCandy) {
                int randomColor = (int) Math.floor(Math.random() * candies.length);
                candy.get(i).setImageResource(candies[randomColor]);
                candy.get(i).setTag(candies[randomColor]);
            }
        }
    }

    // 定时重复执行消除和更新游戏板糖果，即每200ms执行一次run()方法
    Runnable repeatChecker = new Runnable() {
        @Override
        public void run() {
            try {
                checkRawForThree();
                checkColumnForThree();
                moveDownCandies();
            } finally {
                mHandler.postDelayed(repeatChecker, interval);
            }
        }
    };

    void startRepeat() {
        repeatChecker.run();
    }

    private void candyInterchange() {
        // 用于实现交换糖果
        int background = (int) candy.get(candyToBeReplaced).getTag();
        int background1 = (int) candy.get(candyToBeDragged).getTag();
        candy.get(candyToBeDragged).setImageResource(background);
        candy.get(candyToBeReplaced).setImageResource(background1);
        candy.get(candyToBeDragged).setTag(background);
        candy.get(candyToBeReplaced).setTag(background1);
    }

    private void createBoard() {
        GridLayout gridLayout = findViewById(R.id.board);
        // 设置8x8的网格块数
        gridLayout.setRowCount(noOfBlocks);
        gridLayout.setColumnCount(noOfBlocks);
        // 设置网络长度和宽度均与屏幕宽度相同，即正方形布局
        gridLayout.getLayoutParams().width = widthOfScreen;
        gridLayout.getLayoutParams().height = widthOfScreen;

        // 遍历生成每一个网络块
        for (int i = 0; i < noOfBlocks * noOfBlocks; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setId(i);     // 给每一个网络块设定ID
            imageView.setLayoutParams(new android.view.ViewGroup.LayoutParams(widthOfBlock, widthOfBlock));  // 设置每个网格的布局参数

            // 设定最大的图像视图的高度和宽度
            imageView.setMaxHeight(widthOfBlock);
            imageView.setMaxWidth(widthOfBlock);

            // 产生一个介于0和candies.length的长度的随机索引，即随机选取一种颜色的糖果的图像资源
            int randomCandy = (int) Math.floor(Math.random() * candies.length);

            // 防止在生成糖果时出现一开始就有相邻的连续3个糖果出现
            if (i % noOfBlocks >= 2) {   // 列数>=2时
                if (i / noOfBlocks < 2) {    // 行数<2时检测当前糖果是否与当前行前一个糖果相同
                    while (candies[randomCandy] == (int) candy.get(i - 1).getTag()) {
                        randomCandy = (int) Math.floor(Math.random() * candies.length);
                    }
                } else {      // 行数>=2时检测当前糖果是否与当前行前一个糖果或者当前列前一个糖果相同
                    while (candies[randomCandy] == (int) candy.get(i - 1).getTag() ||
                            candies[randomCandy] == (int) candy.get(i - noOfBlocks).getTag()) {
                        randomCandy = (int) Math.floor(Math.random() * candies.length);
                    }
                }
            } else {  // 列数<2时只需判断竖直方向
                if (i / noOfBlocks >= 2) {  // 行数>2
                    while (candies[randomCandy] == (int) candy.get(i - noOfBlocks).getTag()) {
                        randomCandy = (int) Math.floor(Math.random() * candies.length);
                    }
                }
            }

            imageView.setImageResource(candies[randomCandy]);   // 使用随机索引来随机设置糖果的图像资源
            imageView.setTag(candies[randomCandy]);     // 将每个imageView的标签设定为糖果的图像资源标识符
            candy.add(imageView);   // 将设置好图像资源和标签的imageView添加到candy列表中
            gridLayout.addView(imageView);    // 添加视图图像显示
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {     // 点击返回按钮时，如果导航栏是打开状态则关闭导航栏，否则为默认的返回按钮行为
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_gallery) {
            // 设置
            Intent intent = new Intent(this, VolumeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_help) {
            // 帮助页面
            Intent intent = new Intent(this, HelpActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            // 关于
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {
            // 分享
            // 生成分享的文字信息
            String s = String.valueOf(scoreResult);

            // 将文字信息分享到其他应用
            Intent in = new Intent(Intent.ACTION_SEND);
            in.setType("text/plain");
            in.putExtra(Intent.EXTRA_TEXT, s);
            startActivity(in);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}