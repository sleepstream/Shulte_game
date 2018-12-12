package georgearistov.shulte_game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;



public class FontFitTextView extends android.support.v7.widget.AppCompatTextView {

    private float defaultTextSize;
    private Paint mTestPaint;


    public FontFitTextView(Context context) {
        super(context);
        initialise();
    }

    public FontFitTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise();
    }

    private void initialise() {
        mTestPaint = new Paint();
        mTestPaint.set(this.getPaint());
        defaultTextSize = getTextSize();
        //max size defaults to the initially specified text size unless it is too small
    }

    /* Re size the font so the specified text fits in the text box
     * assuming the text box is the specified width.
     */
    private void refitText(String text, int textWidth)
    {
        if (textWidth <= 0)
            return;
        int targetWidth = textWidth - this.getPaddingLeft() - this.getPaddingRight();
        // this is most likely a non-relevant call
        if( targetWidth<=2 )
            return;

        // text already fits with the xml-defined font size?
        mTestPaint.set(this.getPaint());
        mTestPaint.setTextSize(defaultTextSize);
        if(mTestPaint.measureText(text) <= targetWidth) {
            this.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultTextSize);
            return;
        }

        // adjust text size using binary search for efficiency
        float hi = defaultTextSize;
        float lo = 2;
        final float threshold = 0.5f; // How close we have to be
        while (hi - lo > threshold) {
            float size = (hi + lo) / 2;
            mTestPaint.setTextSize(size);
            if(mTestPaint.measureText(text) >= targetWidth )
                hi = size; // too big
            else
                lo = size; // too small

        }

        // Use lo so that we undershoot rather than overshoot
        this.setTextSize(TypedValue.COMPLEX_UNIT_PX, lo);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int height = getMeasuredHeight();
        refitText(this.getText().toString(), parentWidth);
        this.setMeasuredDimension(parentWidth, height);
    }

    @Override
    protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
        refitText(text.toString(), this.getWidth());
    }

    @Override
    protected void onSizeChanged (int w, int h, int oldw, int oldh) {
        if (w != oldw) {
            refitText(this.getText().toString(), w);
        }
    }

    /*@Override
    protected void onDraw(Canvas canvas) {
        int yOffset = getHeight() - getBaseline()-20;
        canvas.translate(0, yOffset);
        super.onDraw(canvas);
    }*/
}
