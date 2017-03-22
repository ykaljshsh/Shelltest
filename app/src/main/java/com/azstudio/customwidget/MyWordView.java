package com.azstudio.customwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatTextView;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.TextView;

import com.azstudio.shelltest.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 两端对齐的text view，可以设置最后一行靠左，靠右，居中对齐
 *
 * @author YD
 */
public class MyWordView extends AppCompatTextView {
    private float textHeight; // 单行文字高度
    private float textLineSpaceExtra = 0; // 额外的行间距
    private int width; // textView宽度
    private Align align = Align.ALIGN_LEFT; // 默认最后一行左对齐
    private boolean firstCalc = true , firstDraw = true;  // 初始化计算

    private float lineSpacingMultiplier = 1.0f;
    private float lineSpacingAdd = 0.0f;

    private int originalHeight = 0; //原始高度
    private int originalLineCount = 0; //原始行数
    private int originalPaddingBottom = 0; //原始paddingBottom
    private OnWordSelectListener mOnWordSelectListener;
    private boolean setPaddingFromMe = false;
    private String newtext = new String();

    // 尾行对齐方式
    public enum Align {
        ALIGN_LEFT, ALIGN_CENTER, ALIGN_RIGHT  // 居中，居左，居右,针对段落最后一行
    }

    public MyWordView(Context context) {
        super(context);
    }

    public MyWordView(Context context, AttributeSet attrs) {
        super(context, attrs);

        lineSpacingMultiplier = attrs.getAttributeFloatValue("http://schemas.android" + "" +
                ".com/apk/res/android", "lineSpacingMultiplier", 1.0f);

        int[] attributes = new int[]{android.R.attr.lineSpacingExtra};

        TypedArray arr = context.obtainStyledAttributes(attrs, attributes);

        lineSpacingAdd = arr.getDimensionPixelSize(0, 0);

        originalPaddingBottom = getPaddingBottom();

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MyWordView);

        int alignStyle = ta.getInt(R.styleable.MyWordView_align, 0);
        switch (alignStyle) {
            case 1:
                align = Align.ALIGN_CENTER;
                break;
            case 2:
                align = Align.ALIGN_RIGHT;
                break;
            default:
                align = Align.ALIGN_LEFT;
                break;
        }

        ta.recycle();
    }


    List<int[]> textwidth = new ArrayList<>();
    StringBuilder SrcStr;
    int linex;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        linex = getLineCount();
        Layout layout = getLayout();


        //首先进行高度调整
        if (firstCalc) {
            width = getMeasuredWidth();
            String text = getText().toString();
            TextPaint paint = getPaint();

            for (int i=0 ; i< linex; i++){
                int start = layout.getLineStart(i);
                int end = layout.getLineEnd(i);
                int[] width = {start, end};
                textwidth.add(width);

            }

            //使用替代textview计算原始高度与行数
            measureTextViewHeight(text, paint.getTextSize(), getMeasuredWidth() -
                    getPaddingLeft() - getPaddingRight());

            //获取行高
            textHeight = 1.0f * originalHeight / originalLineCount;

            textLineSpaceExtra = textHeight * (lineSpacingMultiplier - 1) + lineSpacingAdd;

            setPaddingFromMe = true;
            //调整textview的paddingBottom来缩小底部空白
            setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(),
                    originalPaddingBottom );

            firstCalc = false;


        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //clearHLWord();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                trySelectWord(event);
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    private List<List<Word>> mWords = new ArrayList<>();
    private Word selectedWord = null;
    private int selectLine = 0;
    private boolean doHighlight = false;

    public void trySelectWord(MotionEvent event) {
        Layout layout = getLayout();
        if (layout == null) {
            return;
        }
        int line  = layout.getLineForVertical(getScrollY()
                + (int) (event.getY()+ this.getLineSpacingExtra()/2));
        if (line >= 0){
             final float x_offset = event.getX();
            selectedWord = getWord(x_offset, line);
            if (selectedWord != null) {
                mSelectedWord = new String(selectedWord.mWord.replace("-"," "));
                selectLine = line;
                doHighlight = true;
                setText(SrcStr); //刷新文字绘制
                mOnWordSelectListener.onWordSelect(mSelectedWord);
            }
        }

    }

    private float firstHeight;

    private void highLightWord(Canvas canvas){
        if (selectedWord != null){
            TextPaint paint = getPaint();
            float drawY = selectLine * textHeight + firstHeight;
            int defaultcolor = getCurrentTextColor();
            paint.setColor(getResources().getColor(R.color.colorPrimaryDark));
            canvas.drawRect(new Rect((int) selectedWord.getStart() ,
                    (int) ((selectLine - 1)*textHeight + firstHeight + 12) ,
                    (int) (selectedWord.getEnd()), (int) (drawY + 20)), paint);
            paint.setColor(Color.WHITE);
            canvas.drawText(mSelectedWord, selectedWord.getStart() ,drawY,paint);
            paint.setColor(defaultcolor);
        }
    }



    private int defaultcolor;

    @Override
    protected void onDraw(Canvas canvas) {

        drawWholeText(canvas);
        if (doHighlight){
            highLightWord(canvas);
        }

        if(firstDraw){
            firstDraw = false;
        }

    }

    private void drawWholeText(Canvas canvas){

        TextPaint paint = getPaint();
        defaultcolor = getCurrentTextColor();
        paint.setColor(defaultcolor);
        paint.drawableState = getDrawableState();

        width = getMeasuredWidth();

        Paint.FontMetrics fm = paint.getFontMetrics();
        firstHeight = getTextSize() - (fm.bottom - fm.descent + fm.ascent - fm.top);

        int gravity = getGravity();
        if ((gravity & 0x1000) == 0) { // 是否垂直居中
            firstHeight = firstHeight + (textHeight - firstHeight) / 2;
        }

        int paddingTop = getPaddingTop();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        width = width - paddingLeft - paddingRight;

        float drawSpacingX = paddingLeft;
        for (int i=0; i< linex ;i++){
            float drawY = i * textHeight + firstHeight;
            CharSequence cs = SrcStr.subSequence(textwidth.get(i)[0],textwidth.get(i)[1]);
            List<Word> linewords = new ArrayList<>();

            if (i+1 <linex){ //每段最后一行的处理,不做对齐处理
                String[] sa = cs.toString().split(" ");

                int[] widthinfo = new int[sa.length];
                int length = textwidth.get(i+1)[1] - textwidth.get(i+1)[0];
                int fitwidth = 0, addwidth = 0;
                if ( length <=1 ){
                    fitwidth = (int) paint.measureText(" ");
                    addwidth = 0;
                }
                else {
                    int clinerealwidth =0;
                    for (int j=0; j< widthinfo.length; j++){
                        widthinfo[j] = (int) paint.measureText(sa[j]);
                        clinerealwidth += widthinfo[j];
                    }
                    int slots = widthinfo.length - 1;
                    if (slots > 0){
                        fitwidth = (width - clinerealwidth) / (widthinfo.length - 1);
                        addwidth = width - clinerealwidth - fitwidth * (widthinfo.length - 1);
                    }
                    else {
                        fitwidth = (int) paint.measureText(" ");
                        addwidth = 0;
                    }
                }

                float drawX = 0 + drawSpacingX;

                for (int k = 0 ; k < widthinfo.length ; k++){
                    List<Word> currentWords = new ArrayList<>();

                    currentWords = getWords(sa[k].toString(), drawX);

                    if (k+1>=widthinfo.length){
                        drawX += addwidth;
                    }
                    if (currentWords != null){
                        if (firstDraw){
                            linewords.addAll(currentWords);
                        }

                    }
                    canvas.drawText(sa[k].toString(), drawX ,drawY,paint);

                    drawX += paint.measureText(sa[k].toString()) + fitwidth;

                }

            }
            mWords.add(linewords);
        }
    }


    /**
     * 设置尾行对齐方式
     *
     * @param align 对齐方式
     */
    public void setAlign(Align align) {
        this.align = align;
        invalidate();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        SrcStr = new StringBuilder(text);
        newtext = text.toString().replace(" ","");


    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        if (!setPaddingFromMe) {
            originalPaddingBottom = bottom;
        }
        setPaddingFromMe = false;
        super.setPadding(left, top, right, bottom);
    }


    /**
     * 获取文本实际所占高度，辅助用于计算行高,行数
     *
     * @param text        文本
     * @param textSize    字体大小
     * @param deviceWidth 屏幕宽度
     */
    private void measureTextViewHeight(String text, float textSize, int deviceWidth) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        int widthMeasureSpec = MeasureSpec.makeMeasureSpec(deviceWidth, MeasureSpec.EXACTLY);
        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        textView.measure(widthMeasureSpec, heightMeasureSpec);
        originalLineCount = textView.getLineCount();
        originalHeight = textView.getMeasuredHeight();
    }

    private String mSelectedWord;


    public List<Word> getWords(String s, float start_x){
        if (!(s.length() == 1 && !Character.isLetter(s.charAt(0)))) {
            List<Word> result = new ArrayList<>();
            int orginlength = s.length();
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i); //取当前位置字母
                if (!Character.isLetter(c) && c != '-') { //截止到空格或非连线标点为止
                    if (i == 0 && orginlength > 1) { //处理以符号开头单词结束的情况
                        start_x += getPaint().measureText(s.substring(i, i + 1));
                        s = s.substring(i + 1);
                    } else if (i != 0 && i + 1 < orginlength) {
                        Word currentword = new Word(start_x, start_x +
                                getPaint().measureText(s.substring(0, i)), s.substring(0, i));
                        result.add(currentword);
                        List<Word> innerresult = new ArrayList<>();
                        innerresult = getWords(s.substring(i, s.length()), currentword.getEnd());
                        if (innerresult != null) {
                            result.addAll(innerresult);
                        }
                        return result;
                    }
                    else if ( i+1 >= orginlength){ //以非连线的符号结束

                        Word currentword = new Word(start_x, start_x +
                                getPaint().measureText(s.substring(0, i)), s.substring(0, i));
                        result.add(currentword);
                        return result;
                    }
                }
                //普通单词
                if (i + 1 >= s.length()) {
                    Word currentword;
                    if( !Character.isLetter(s.charAt(i)) ){ //剔除单词末尾的符号
                        currentword = new Word(start_x, start_x +
                                getPaint().measureText(s.substring(0,i)), s.substring(0, i));
                    }
                    else {
                        currentword = new Word(start_x, start_x + getPaint().measureText(s), s);
                    }

                    result.add(currentword);
                }
            }
            return result;
        }
        return null;
    }

    private Word getWord(final float x_offset, final int line) {
        if (mWords == null) {
            return null;
        }

        for (Word w : mWords.get(line)) {
            if (w.isIn(x_offset)) {
                return w;
            }
        }

        return null;
    }

    private class Word {
        public Word(final int start, final int end) {
            this.mStart = start;
            this.mEnd = end;
        }

        public Word(final float start, final float end, final String word) {
            this.mStart = start;
            this.mEnd = end;
            this.mWord = word;
        }

        private float mStart;
        private float mEnd;
        private String mWord;

        public float getStart() {
            return this.mStart;
        }

        public float getEnd() {
            return this.mEnd;
        }

        public boolean isIn(final float x_offset) {
            if (x_offset >= getStart() && x_offset <= getEnd()) {
                return true;
            }
            return false;
        }

        public boolean isIn(final int index) {
            if (index >= getStart() && index <= getEnd()) {
                return true;
            }
            return false;
        }

        public boolean equals(Word word){

            if(word != null){
                if (this.getStart() == word.getStart() && this.mWord.equals(word.mWord)){
                    return true;
                }
            }
            return false;

        }

        @Override
        public String toString() {
            return "( " + getStart() + ", " + getEnd() + " )";
        }
    }

    public void setOnWordSelectListener(OnWordSelectListener listener) {
        mOnWordSelectListener = listener;
    }

    public interface OnWordSelectListener {
        public void onWordSelect(String word);
    }
}