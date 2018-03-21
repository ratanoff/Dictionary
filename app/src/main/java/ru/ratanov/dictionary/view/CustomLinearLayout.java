package ru.ratanov.dictionary.view;


import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomLinearLayout extends LinearLayout {

    private static final String TAG = CustomLinearLayout.class.getSimpleName();
    private TextView mChild1;
    private int mNewTextSize;

    public CustomLinearLayout(Context context) {
        super(context);
    }

    public CustomLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mChild1 = (TextView) getChildAt(0);
        int childHeight = mChild1.getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        int parentHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        Log.i(TAG, "onMeasure: " + parentHeight + "(" + getMeasuredHeight() + ")-" + childHeight + "(" + mChild1.getMeasuredHeight() + ")" + mChild1.getLineCount());

        int lineCount = mChild1.getLineCount();
        mNewTextSize = (int) mChild1.getTextSize() / lineCount / 2;
        Log.i(TAG, "onMeasure: " + mNewTextSize);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        mChild1.setTextSize(mNewTextSize);
        invalidate();
        requestLayout();
    }
}
