package georgearistov.shulte_game;
;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.*;

import static georgearistov.shulte_game.MainActivity.*;


public class CustomGridAdapter extends BaseAdapter {

    private Context context;
    private View lastTouchView;
    private int mapSize;
    public TextView textView;
    public LinearLayout itemLayout;
    private static final String LOG_TAG = "CustomGridAdapter";
    public Integer counter=1;
    private TextView nextItem;

    private String[] colors = new String[]{"#000000","#FF0000","#FF4500","#000000","#0000FF","#2F4F4F","#A0522D","#800000","#BC8F8F","#0000FF","#4B0082","#BDB76B","#00FF7F","#4682B4"};
    private Integer[] drawables = new Integer[]{R.drawable.item_highlight_black, R.drawable.item_highlight_red};

    public Map<Integer, Map<Integer, Integer>> gridValues = new HashMap<Integer, Map<Integer, Integer>>();
    private List<Integer> numbers = new ArrayList<>();
    private ColorStateList oldColor;

    //Constructor to initialize values
    public CustomGridAdapter(Context context, int mapSize, TextView nextItem) {

        this.nextItem = nextItem;
        this.context = context;
        this.mapSize = mapSize;
        reGenerate(this.mapSize);
    }

    @Override
    public int getCount() {
        Log.d(LOG_TAG , "count " + gridValues.size()*gridValues.get(0).size());
        if (gridValues.size() == 0)
            return 0;
        return gridValues.size()*gridValues.get(0).size();
    }

    @Override
    public Object getItem(int i) {
        Log.d(LOG_TAG, i+ " -i :  " +(int)i/mapSize + "  " + (i - ((int)i/mapSize)*mapSize) + " data "+gridValues.get((int)i/mapSize).get(i - ((int)i/mapSize)*mapSize));
        return gridValues.get((int)i/mapSize).get(i - ((int)i/mapSize)*mapSize);
    }

    @Override
    public long getItemId(int i) {
        Log.d(LOG_TAG, i+ " -i :  " );
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        // LayoutInflator to call external grid_item.xml file

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridViewItem = view;
        if(view == null)
            gridViewItem = inflater.inflate( R.layout.grid_item , null);

        // set value into textview

        LinearLayout itemLayout = gridViewItem.findViewById(R.id.itemLayout);
        textView = (TextView) gridViewItem
                .findViewById(R.id.grid_item_label);

        Integer tmpItem = (int)getItem(position);
        textView.setText(String.valueOf(tmpItem));


        if(sharedPreferences.getBoolean("rotate_field", false)) {
            Random rand = new Random();
            int tmp = rand.nextInt(360);
            textView.setRotation(tmp);
            if(tmpItem.toString().contains("9") || tmpItem.toString().contains("6")) {
                String i = String.valueOf(tmpItem) + ".";
                textView.setText(i);
            }
        }
        else
        {
            textView.setRotation(0);
        }

        if(sharedPreferences.getBoolean("color_field", false) && currentGameMode == 0)
        {

            Random random = new Random();
            int nextInt = random.nextInt(colors.length);
            textView.setTextColor(Color.parseColor(colors[nextInt]));
        }
        else if(currentGameMode != 1)
        {
            Log.d(LOG_TAG,  " setColor Back");
            setBackTextColor();
        }
        //black&rad field with one color text
        if(currentGameMode == 1)
        {
            if(oldColor == null)
                oldColor = textView.getTextColors();
            Random random = new Random();
            int nextInt = random.nextInt(2);
            Log.d(LOG_TAG, nextInt+" color number");
            itemLayout.setBackground(ContextCompat.getDrawable(context, drawables[nextInt]));
            textView.setTextColor(context.getResources().getColor(R.color.white));
        }
        else if(currentGameMode == 0)
        {
            itemLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.item_highlight));
        }






        if(gridView != null)
        {
            int tmp = gridView.getColumnWidth();
            textView.setHeight(gridView.getColumnWidth());
        }


        itemLayout = (LinearLayout) gridViewItem.findViewById(R.id.itemLayout);

        RelativeLayout.LayoutParams layoutParams =  new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        if((int)position/mapSize >0 && (position - ((int)position/mapSize)*mapSize) >0)
        {
            layoutParams.setMargins(3,3,0,0);
        }

        else if((int)position/mapSize >0)
        {
            layoutParams.setMargins(0,3,0,0);
            Log.d(LOG_TAG, position + " - position " + mapSize + " mapsize " +  (position - ((int)position/mapSize)*mapSize));
        }
        else if( (position - ((int)position/mapSize)*mapSize) >0)
        {
            layoutParams.setMargins(3,0,0,0);
        }
        itemLayout.setLayoutParams(layoutParams);

        return gridViewItem;
    }

    @Override
    public boolean isEnabled(int position)
    {
        return true;
    }

    public void setBackTextColor()
    {
        if(textView != null) {
            if (Build.VERSION.SDK_INT < 23) {
                textView.setTextAppearance(context, R.style.TextGameField);
            } else {
                textView.setTextAppearance(R.style.TextGameField);
            }
        }
    }

    public void setBackBackgroundColor()
    {
        if(itemLayout != null) {
            if (Build.VERSION.SDK_INT < 21) {
                textView.setTextAppearance(context, R.style.TextGameField);
            } else {
                itemLayout.setBackground(context.getResources().getDrawable(R.drawable.item_highlight, null));
            }
        }
    }


    public void reGenerate(int mapSize)
    {
        counter = 1;
        this.mapSize = mapSize;
        gridValues = new HashMap<Integer, Map<Integer, Integer>>();
        numbers = new ArrayList<>();
        for(int i =1; i<=this.mapSize*this.mapSize; i++)
        {
            numbers.add(i);
        }

        for(int i=0; i<mapSize; i++)
        {
            for(int j = 0; j<mapSize; j++)
            {
                boolean valid = false;
                int n = 0;
                List<Integer> numbersTmp = new ArrayList<>();
                numbersTmp.addAll(numbers);
                while(!valid) {
                    Random rand = new Random();


                    n = rand.nextInt(numbersTmp.size());
                    if(j-1>= 0)
                    if (j - 1 >= 0 && gridValues.get(i).get(j-1)!= numbersTmp.get(n)-1 && gridValues.get(i).get(j-1)!= numbersTmp.get(n)+1) {
                        valid = true;
                    }
                    else if(j-1<0)
                        valid = true;
                    else
                        valid = false;
                    if (i - 1 >= 0 && gridValues.get(i-1).get(j)!= numbersTmp.get(n)-1 && gridValues.get(i-1).get(j)!= numbersTmp.get(n)+1 && valid) {
                        valid = true;
                    }
                    else if(i-1 < 0 && valid)
                        valid = true;
                    else
                        valid = false;
                    if (i - 1 >= 0 && j - 1 >= 0 && gridValues.get(i-1).get(j-1)!= numbersTmp.get(n)-1 && gridValues.get(i-1).get(j-1)!= numbersTmp.get(n)+1 && valid) {
                        valid = true;
                    }
                    else if((i - 1 < 0 || j - 1 < 0) && valid)
                        valid = true;
                    else
                        valid = false;
                    if(valid || numbersTmp.size() == 1) {
                        Map<Integer, Integer> tmp = new HashMap<Integer, Integer>();
                        if (gridValues.get(i) != null) {
                            gridValues.get(i).put(j, numbersTmp.get(n));
                            Log.d(LOG_TAG, gridValues.get(i).get(j)+" --" + i +" -- " + j );
                        }
                        else {
                            tmp.put(j, numbersTmp.get(n));
                            gridValues.put(i, tmp);
                            Log.d(LOG_TAG, gridValues.get(i).get(j)+" --" + i +" -- " + j );
                        }
                        break;
                    }
                    else
                        numbersTmp.remove(n);
                }
                numbers.remove(numbers.indexOf(numbersTmp.get(n)));
            }
        }
    }
}
