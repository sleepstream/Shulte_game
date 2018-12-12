package georgearistov.shulte_game;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

import static georgearistov.shulte_game.MainActivity.customGridAdapter;

public class GridViewEx extends GridView {

    private int mRequestedNumColumns = 0;

    public GridViewEx(Context context) {
        super(context);
    }

    public GridViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridViewEx(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setNumColumns(int numColumns) {
        super.setNumColumns(numColumns);

        if (numColumns != mRequestedNumColumns) {
            mRequestedNumColumns = numColumns;
        }
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec+35);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int tmp = getColumnWidth();
        int tmp1 = getNumColumns();
        if (getLayoutParams() != null && w != h) {
            getLayoutParams().height = tmp*tmp1;//+ ((tmp-1) * getHorizontalSpacing())+ getListPaddingLeft() + getListPaddingRight();
            getLayoutParams().width = getLayoutParams().height;

            setLayoutParams(getLayoutParams());
        }
    }

   /* @Override
    public boolean onTouchEvent(MotionEvent ev){
/*        // Called when a child does not want this parent and its ancestors to intercept touch events.
        requestDisallowInterceptTouchEvent(true);

        if(ev.getAction() == MotionEvent.ACTION_MOVE)
            return true;
        else
            return super.onTouchEvent(ev);
*/
/*        return true;//ev.getAction() != MotionEvent.ACTION_MOVE;
    }*/
}
