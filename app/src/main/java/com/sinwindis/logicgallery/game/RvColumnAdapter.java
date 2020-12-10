package com.sinwindis.logicgallery.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.sinwindis.logicgallery.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RvColumnAdapter extends RecyclerView.Adapter<RvColumnAdapter.ViewHolder> {

    ColumnIndexDataManager columnIndexDataManager;

    private final List<TextView> tvList = new ArrayList<>();
    private final List<ConstraintLayout> clList = new ArrayList<>();
    private float widthUnder = 0;
    private int widthOffset = 0;
    private final int length;
    private Drawable completeBackgroundDrawable;
    private Drawable incompleteBackgroundDrawable;

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public static class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조. (hold strong reference)
        }
    }

    RvColumnAdapter(ColumnIndexDataManager columnIndexDataManager) {
        //생성자
        this.columnIndexDataManager = columnIndexDataManager;
        columnIndexDataManager.makeIdxDataSet();
        this.length = columnIndexDataManager.idxNumSet.length;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @SuppressLint("UseCompatLoadingForDrawables")
    @NonNull
    @Override
    public RvColumnAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        if (completeBackgroundDrawable == null) {
            completeBackgroundDrawable = context.getDrawable(R.drawable.background_index_column_gray);
        }

        if (incompleteBackgroundDrawable == null) {
            incompleteBackgroundDrawable = context.getDrawable(R.drawable.background_index_column);
        }

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item_column, parent, false);


        widthUnder += parent.getMeasuredWidth() % this.length / ((float) this.length);

        if (widthUnder >= 1) {
            widthOffset = 1;
            widthUnder--;
        }

        view.setLayoutParams(new RecyclerView.LayoutParams(parent.getMeasuredWidth() / this.length + widthOffset, ViewGroup.LayoutParams.MATCH_PARENT));

        widthOffset = 0;

        return new ViewHolder(view);
    }

    // onBindViewHolder() - position 에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(RvColumnAdapter.ViewHolder holder, int position) {
        final TextView tv = holder.itemView.findViewById(R.id.tv_item_column);
        final ConstraintLayout cl = holder.itemView.findViewById(R.id.cl_background);
        tv.post(() -> tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) tv.getMeasuredWidth() / 2));

        tvList.add(tv);
        clList.add(cl);
        refreshView(position);
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return this.length;
    }

    public void refreshView(int columnNum) {
        boolean[] completeIdx = columnIndexDataManager.getIdxMatch(columnNum);
        int idxMaxNum = columnIndexDataManager.getIdxNumSet()[columnNum];
        int[][] dataSet = columnIndexDataManager.getIdxDataSet();

        boolean isColumnComplete = true;
        if (idxMaxNum == 0) {
            isColumnComplete = false;
        }

        tvList.get(columnNum).setText("");
        SpannableStringBuilder numStr;
        for (int i = 0; i < idxMaxNum; i++) {
            if (i == 0) {
                numStr = new SpannableStringBuilder(String.valueOf(dataSet[columnNum][i]));
            } else {
                numStr = new SpannableStringBuilder('\n' + String.valueOf(dataSet[columnNum][i]));
            }

            if (completeIdx[i]) {
                numStr.setSpan(new ForegroundColorSpan(Color.parseColor("#a0a0a0")), 0, numStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                numStr.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), 0, numStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                isColumnComplete = false;
            }
            tvList.get(columnNum).append(numStr);
        }

        if (isColumnComplete) {
            clList.get(columnNum).setBackground(completeBackgroundDrawable);
        } else {
            clList.get(columnNum).setBackground(incompleteBackgroundDrawable);
        }

    }
}