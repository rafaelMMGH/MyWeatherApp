package com.example.bakur.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {

    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;

    private TextView[] mDots;

    private sliderAdapter sliderAdapter;

    private Button btnPrev,btnNext;

    private int mCurrentPage;

    public static Activity wA;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        wA = this;

        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        if (!isFirstRun)
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));

        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).apply();

        mSlideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
        mDotLayout = (LinearLayout) findViewById(R.id.dotsLayout);

        btnNext = (Button) findViewById(R.id.btnNext);
        btnPrev = (Button) findViewById(R.id.btnPrev);

        sliderAdapter = new sliderAdapter(this);

        mSlideViewPager.setAdapter(sliderAdapter);

        addDotsIndicators(0);

        mSlideViewPager.addOnPageChangeListener(viewListener);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentPage < 2)
                    mSlideViewPager.setCurrentItem(mCurrentPage + 1);
                else{
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlideViewPager.setCurrentItem(mCurrentPage -1);
            }
        });
    }

    public void addDotsIndicators(int position){

        mDots = new TextView[3];
        mDotLayout.removeAllViews();

        for (int i=0; i < mDots.length; i++){
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorTransparent));

            mDotLayout.addView(mDots[i]);
        }

        if (mDots.length > 0)
            mDots[position].setTextColor(getResources().getColor(R.color.colorWhite));
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicators(position);
            mCurrentPage = position;

            if (position == 0){
                btnNext.setEnabled(true);
                btnPrev.setEnabled(false);
                btnPrev.setVisibility(View.INVISIBLE);

                btnNext.setText("Next");
                btnPrev.setText("");

            }else if (position == mDots.length -1){
                btnNext.setEnabled(true);
                btnPrev.setEnabled(true);
                btnPrev.setVisibility(View.VISIBLE);

                btnNext.setText("Finish");
                btnPrev.setText("Back");
            }else{
                btnNext.setEnabled(true);
                btnPrev.setEnabled(true);
                btnPrev.setVisibility(View.VISIBLE);


                btnNext.setText("Next");
                btnPrev.setText("Back");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
