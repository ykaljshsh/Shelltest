package com.azstudio.shelltest;

import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.azstudio.customwidget.MyWordView;
import com.azstudio.customwidget.QueryWordDialog;
import com.azstudio.customwidget.ShanbayApi;
import com.azstudio.model.ShanbayWord;
import com.azstudio.model.WordInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MyWordView.OnWordSelectListener{

    public String mArticle;
    private QueryWordDialog mDialog;
    public static Resources mResource = null;
    private String shanbayapi = "https://api.shanbay.com/bdc/search/?word=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //WordView mainContent = (WordView) findViewById(R.id.mainContent);
        MyWordView mainContent = (MyWordView) findViewById(R.id.mainContent);
        //WordAlignTextView mainContent = (WordAlignTextView) findViewById(R.id.mainContent);
        mResource = getResources();
        mArticle = getFromAssets("article1.txt");
        mDialog = new QueryWordDialog(MainActivity.this);
        mainContent.setText(mArticle, TextView.BufferType.SPANNABLE);
        mainContent.setOnWordSelectListener(this);
        //getEachWord(mainContent);
        mainContent.setMovementMethod(LinkMovementMethod.getInstance());
        Button btn = (Button) findViewById(R.id.btn_test2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ImgListActivity.class);
                //i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
            }
        });
    }

    @Override
    public void onWordSelect(String word) {
        popupDialog(word);
    }

    public String getFromAssets(String fileName){
        try {
            InputStreamReader inputReader = new InputStreamReader( getResources().getAssets().open(fileName), "UTF-8");
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while((line = bufReader.readLine()) != null)
                Result += line + "\n";
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void popupDialog(String word){
        getWordInfo2(word);
        //new getWordInfoAsynTask().execute(word);
    }

    private ShanbayWord getWordInfo2(String word){

        final ShanbayWord mword = new ShanbayWord();
        ShanbayApi api = new ShanbayApi();
        mword.word = word;
        Call<WordInfo> call = api.getDefault().getWordInfo(word);
        call.enqueue(new Callback<WordInfo>() {
            @Override
            public void onResponse(Call<WordInfo> call, Response<WordInfo> response) {
                //这里的response就可以提取数据了
                mword.status_code = response.body().getStatusCode();
                if (mword.status_code == 0){
                    mword.word_def_cn = response.body().getData().getCnDefinition().getDefn();
                    mword.voice_url_uk = response.body().getData().getUkAudio();
                    mword.word_pronun_uk = response.body().getData().getPronunciation();
                }
                mDialog.setContent(mword);
                mDialog.show();

            }

            @Override
            public void onFailure(Call<WordInfo> call, Throwable t) {
                // Log.e("MainActivity", t.toString());
            }
        });
        return mword;
    }

    private String[] getWordInfo(String word){
        JSONObject jOb;
        String result = null;
        DataInputStream dis = null;
        StringBuilder sb = null;
        String[] wordinfo = new String[8];
        wordinfo[0] = word;
        boolean isConnect = false;
        try{
            URL url = new URL(shanbayapi + word); //创建URL对象
            //返回一个URLConnection对象，它表示到URL所引用的远程对象的连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000); //设置连接超时为5秒
            conn.setRequestMethod("GET"); //设定请求方式
            conn.connect(); //建立到远程对象的实际连接
            //返回打开连接读取的输入流
            dis = new DataInputStream(conn.getInputStream());
            //判断是否正常响应数据
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                System.out.println("网络错误异常！!!!");
            }
            isConnect = true;

        }catch (Exception e) {
            Log.e("log_tag", "Error in http connection" + e.toString());
            isConnect = false;
        }
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(dis, "UTF-8"), 8);
            sb = new StringBuilder();
            sb.append(reader.readLine() + "\n");

            String line = "0";
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            dis.close();
            result = sb.toString();
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }
        // paring data

        if(isConnect){
            try {
                jOb = new JSONObject(result);
                JSONObject jb;
                int statusCode = 1;
                statusCode = jOb.getInt("status_code");
                switch (statusCode){
                    case 0:
                        jb = jOb.getJSONObject("data");
                        jb = jb.getJSONObject("pronunciations");
                        wordinfo[1] = jb.getString("uk");
                        wordinfo[2] = jb.getString("us");
                        jb = jOb.getJSONObject("data");
                        jb = jb.getJSONObject("cn_definition");
                        wordinfo[3] = jb.getString("defn");
                        jb = jOb.getJSONObject("data");
                        jb = jb.getJSONObject("audio_addresses");
                        JSONArray ja;
                        ja = jb.getJSONArray("uk");
                        wordinfo[5] = ja.getString(0);
                        wordinfo[7] = "0";
                        break;
                    default:
                        wordinfo[7] = "1";
                        break;
                }
            } catch (JSONException e1) {
                Toast.makeText(getBaseContext(), "出现错误"
                        ,Toast.LENGTH_LONG).show();
            }
        }

        return wordinfo;
    }

    private class getWordInfoAsynTask extends AsyncTask<String, Integer, String[]> {

        @Override
        protected void onPreExecute () {
            super.onPreExecute();
        }

        @Override
        protected String[] doInBackground (String... params) {
            return getWordInfo(params[0]);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // TODO Auto-generated method stub

        }

        @Override
        protected void onPostExecute (String[] result) {
            mDialog.setContent(result);
            mDialog.show();
            super.onPostExecute(result);
        }


    }
}
