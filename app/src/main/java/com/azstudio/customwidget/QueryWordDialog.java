package com.azstudio.customwidget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azstudio.shelltest.R;
import com.azstudio.util.Player;

/**
 * Created by YKA-SFB on 2017/3/8.
 */

public class QueryWordDialog {

    private Context context;
    private Dialogcallback dialogcallback;
    private Player mPlayer;
    private Dialog dialog;
    private TextView mWord, mPronun, mDefin;
    private ImageButton mVoice_us, mVoice_uk;
    private String[] mWordInfo = new String[8]; //{单词，英音，美音，中义，英义，英音发音url，美音发音url}
    private String mPlay_url_us = new String(), mPlay_url_uk = new String();


    /**
     * init the queryword_dialog
     * @return
     */
    public QueryWordDialog(Context con) {
        this.context = con;
        dialog = new Dialog(context, R.style.queryword_dialog);
        RelativeLayout root = (RelativeLayout) LayoutInflater.from(context).inflate(
                R.layout.layout_queryword, null);
        dialog.setContentView(root);
        dialog.setCanceledOnTouchOutside(true);
        mWord = (TextView) root.findViewById(R.id.word);
        mPronun = (TextView) root.findViewById(R.id.pronun);
        mDefin = (TextView) root.findViewById(R.id.cn_defin);
        mVoice_uk = (ImageButton) root.findViewById(R.id.pronun_voice_uk);
        mPlayer = new Player();
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = -20; // 新位置Y坐标
        lp.width = (int) context.getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0, 0);
        //lp.height = ;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);

    }
    /**
     * 设定一个interface接口,使 queryword_dialog 可以处理activity定义的事情
     * @author RoyYu
     *
     */
    public interface Dialogcallback {
        public void dialogdo(String string);
    }
    public void setDialogCallback(Dialogcallback dialogcallback) {
        this.dialogcallback = dialogcallback;
    }
    /**
     * @category Set The Content of TextViews
     * */
    public void setContent(String[] content) {
        mWordInfo = content;
        mWord.setText(mWordInfo[0]);
        if (mWordInfo[7].equals("0")){
            mPronun.setText("/"+mWordInfo[1]+"/");
            //mPronun.setText("英音["+mWordInfo[1]+"] 美音["+mWordInfo[2]+"]");
            mDefin.setText(mWordInfo[3]);
            mPlay_url_uk = mWordInfo[5];
            //mPlay_url_us = mWordInfo[6];
            mVoice_uk.setOnClickListener(new PlayVoiceListener());
        }
        else {
            mPronun.setText("");
            mDefin.setText("无释义");
        }

    }

    public void show() {
        dialog.show();
    }
    public void hide() {
        dialog.hide();
    }
    public void dismiss() {
        dialog.dismiss();
    }

    public class PlayVoiceListener implements View.OnClickListener {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        mPlayer.playUrl(mPlay_url_uk);
                    }
                }).start();
        }
    }


}
