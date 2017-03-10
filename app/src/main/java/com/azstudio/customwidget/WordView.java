package com.azstudio.customwidget;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatEditText;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.Toast;

import com.azstudio.shelltest.R;

import java.util.ArrayList;
import java.util.List;


public class WordView extends AppCompatEditText {
    private final static String TAG = "WordView";
    private SpannableString mSpannableString;
    private String mSelectedWord;
    private OnWordSelectListener mOnWordSelectListener;
    private ForegroundColorSpan mForegroundColorSpan = new ForegroundColorSpan(Color.WHITE);
    private BackgroundColorSpan mBackgroundColorSpan = new BackgroundColorSpan(R.color.colorPrimary);

    public WordView(Context context) {
        super(context);
        initialize();
    }

    public WordView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public void setOnWordSelectListener(OnWordSelectListener listener) {
        mOnWordSelectListener = listener;
    }

    private void initialize() {
        setGravity(Gravity.TOP);
        setBackgroundColor(Color.WHITE);
    }

    @Override
    protected void onCreateContextMenu(ContextMenu menu) {
        //不做任何处理，为了阻止长按的时候弹出上下文菜单
    }

    @Override
    public boolean getDefaultEditable() {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (mWords == null) {
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                clearSpan();
                break;
            case MotionEvent.ACTION_MOVE:
                break;

            case MotionEvent.ACTION_UP:
                trySelectWord(event);
                break;
            case MotionEvent.ACTION_CANCEL:
                //clearSelectedWord();
                break;
        }
        return true;
    }

    public void trySelectWord(MotionEvent event) {
        Layout layout = getLayout();
        if (layout == null) {
            return;
        }
        int line  = layout.getLineForVertical(getScrollY() + (int) (event.getY()+ this.getLineSpacingExtra()/2 - this.getPaddingTop()));
        if (line >= 0){
            final int index = layout.getOffsetForHorizontal(line, (int) event.getX());
            Word selectedWord = getWord(index);

            if (selectedWord != null) {
                mSpannableString.setSpan(mForegroundColorSpan,
                        selectedWord.getStart(), selectedWord.getEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                mSpannableString.setSpan(mBackgroundColorSpan,
                        selectedWord.getStart(), selectedWord.getEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                setText(mSpannableString);
                mSelectedWord = getText().subSequence(selectedWord.getStart(), selectedWord.getEnd()).toString();
                mOnWordSelectListener.onWordSelect(mSelectedWord);
            }
        }

    }

    public void clearSelectedWord() {
        clearSpan();
        setText(mSpannableString);
        showSelectedWord(mSelectedWord);
    }

    private void showSelectedWord(String selectedWord) {
        if (selectedWord != null) {
            Toast.makeText(getContext(), selectedWord,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void clearSpan() {
        ForegroundColorSpan[] spans = mSpannableString.getSpans(0, getText().length(), ForegroundColorSpan.class);
        for (int i = 0; i < spans.length; i++) {
            mSpannableString.removeSpan(spans[i]);
        }
    }

    private Word getWord(final int index) {
        if (mWords == null) {
            return null;
        }

        for (Word w : mWords) {
            if (w.isIn(index)) {
                return w;
            }
        }

        return null;
    }


    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        mSpannableString = SpannableString.valueOf(getEditableText());
        mWords = getWords(text);
    }


    private List<Word> mWords;

    /**
     * getWords 从文本中取词的方法
     * @param s 文本序列
     * @return 词汇表
     */
    public List<Word> getWords(CharSequence s) {

        if (s == null) {
            return null;
        }

        List<Word> result = new ArrayList<Word>();
        int start = -1;
        int i = 0;
        for (; i < s.length(); i++) {
            char c = s.charAt(i); //取当前位置字母
            if (c == ' ' || !Character.isLetter(c)) { //截止到空格或标点为止
                if (start != -1) {
                    result.add(new Word(start, i));// From ( 0, 4 )
                }
                start = -1;
            } else {
                if (start == -1) {
                    start = i;
                }
            }
        }

        if (start != -1) {
            result.add(new Word(start, i));
        }

        Log.d(TAG, result.toString());

        return result;

    }

    private class Word {
        public Word(final int start, final int end) {
            this.mStart = start;
            this.mEnd = end;
        }

        private int mStart;
        private int mEnd;

        public int getStart() {
            return this.mStart;
        }

        public int getEnd() {
            return this.mEnd;
        }

        public boolean isIn(final int index) {
            if (index >= getStart() && index <= getEnd()) {
                return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return "( " + getStart() + ", " + getEnd() + " )";
        }
    }



    public interface OnWordSelectListener {
        public void onWordSelect(String word);
    }
}

