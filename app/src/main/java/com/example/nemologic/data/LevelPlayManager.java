package com.example.nemologic.data;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.sql.SQLException;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class LevelPlayManager {

    public String category;
    public String name;
    public int[][] dataSet;
    public int[][] checkedSet;
    public int[][][] checkStack;
    public int height;
    public int width;

    public int stackNum = 0;
    public int stackMaxNum = 0;

    private int[][] parseDataSet(String dataSet, int width, int height)
    {

        String[] rawTemp = dataSet.split(" ");
        int[][] dataTemp = new int[height][width];

        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                dataTemp[y][x] = Integer.parseInt(rawTemp[x + y*width]);
            }
        }

        return dataTemp;
    }

    private String parseDataSetToString(int[][] dataSet)
    {
        String saveData = "";

        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                saveData +=  dataSet[y][x] + " ";
            }
        }

        return saveData;
    }

    public void savePlayData(Context ctx)
    {

        DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
        try {
            mDbOpenHelper.open();

            mDbOpenHelper.updateLevel(name, category, 1, parseDataSetToString(checkedSet));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        mDbOpenHelper.close();

        SharedPreferences lastPlayPref = ((Activity)ctx).getSharedPreferences("LASTPLAY", MODE_PRIVATE);
        SharedPreferences.Editor editor = lastPlayPref.edit();
        editor.putString("name", name);
        editor.putString("category", category);

        editor.commit();
    }

    public LevelPlayManager(String category, String name, int width, int height, String dataSet, String saveData)
    {
        this.category = category;
        this.name = name;
        this.dataSet = parseDataSet(dataSet, width, height);
        this.height = height;
        this.width = width;
        if(saveData.isEmpty())
        {
            //저장 데이터가 없으면 새로 빈 array 할당
            this.checkedSet = new int[height][width];
            for(int y = 0; y < height; y++)
            {
                for(int x = 0; x < width; x++)
                {
                    this.checkedSet[y][x] = 0;
                }
            }
        }
        else
        {
            //저장 데이터가 있으면 해당 데이터를 파싱해서 저장
            this.checkedSet = parseDataSet(saveData, width, height);
        }

        this.checkStack = new int[10][this.height][this.width];
        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                this.checkStack[0][y][x] = 0;
            }
        }
    }

    private void expandStackSize()
    {
        int[][][] stackTemp = new int[checkStack.length][this.height][this.width];

        for(int i = 0; i < checkStack.length; i++)
        {
            for(int y = 0; y < this.height; y++)
            {
                if (this.width >= 0)
                    System.arraycopy(checkStack[i][y], 0, stackTemp[i][y], 0, this.width);
            }
        }

        checkStack = new int[stackTemp.length*2][this.height][this.width];

        for(int i = 0; i < stackTemp.length; i++)
        {
            for(int y = 0; y < this.height; y++)
            {
                if (this.width >= 0)
                    System.arraycopy(stackTemp[i][y], 0, checkStack[i][y], 0, this.width);
            }
        }
    }

    public void pushCheckStack()
    {
        stackNum++;
        stackMaxNum = stackNum;

        if(stackNum == checkStack.length - 1)
            expandStackSize();

        for(int y = 0; y < height; y++)
        {
            if (width >= 0) System.arraycopy(checkedSet[y], 0, checkStack[stackNum][y], 0, width);
        }
    }

    public void prevCheckStack()
    {
        if(stackNum == 0)
            return;
        stackNum--;
        for(int y = 0; y < height; y++)
        {
            if (width >= 0) System.arraycopy(checkStack[stackNum][y], 0, checkedSet[y], 0, width);
        }
    }

    public void nextCheckStack()
    {
        if(stackNum == stackMaxNum)
            return;

        stackNum++;
        for(int y = 0; y < height; y++)
        {
            if (width >= 0) System.arraycopy(checkStack[stackNum][y], 0, checkedSet[y], 0, width);
        }

    }

    public boolean isGameEnd()
    {
        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                if(dataSet[y][x] == 1 && checkedSet[y][x] != 1)
                {
                    return false;
                }
                else if(dataSet[y][x] != 1 && checkedSet[y][x] == 1)
                {
                    return false;
                }
            }
        }

        return true;
    }

    public int getStackNum()
    {
        return stackNum;
    }

    public int getStackMaxNum()
    {
        return stackMaxNum;
    }
}