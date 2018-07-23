package com.example.bakur.myapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class sliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public sliderAdapter(Context context){
        this.context = context;
    }

    //Arrays

    public int[] slide_icons = {
        R.drawable.ic_marker,
        R.drawable.ic_time,
        R.drawable.ic_warning
    };

    public String[] slide_headings ={
      "Chose your location",
      "Keep updated",
      "Anticipate"
    };

    public String[] slide_desc ={
            "Des1 sdjfhdkjlfhlfkjshlf",
            "Desc2 asldjkashdljkashdlasjdhsa",
            "Des3 sldhsakdjlhsdlsahdñoaisddsañodasññsodifu"
    };

    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView slideImageView = (ImageView) view.findViewById(R.id.slide_image);
        TextView slideHeading = (TextView) view.findViewById(R.id.slide_heading);
        TextView slideDesc = (TextView) view.findViewById(R.id.slide_text);

        slideImageView.setImageResource(slide_icons[position]);
        slideHeading.setText(slide_headings[position]);
        slideDesc.setText(slide_desc[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
        }
}
