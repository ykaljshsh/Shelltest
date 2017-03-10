package com.azstudio.shelltest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.azstudio.model.ImgAdapter;
import com.azstudio.util.TestImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ImgListActivity extends AppCompatActivity {
    private String urlFile = "imgurls.json";
    private ImgAdapter mImgAdapter;
    private List<String[]> mImgUrls = new ArrayList<>();
    private ListView mImgListView;
    private boolean imgswitch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_list);
        mImgListView = (ListView) findViewById(R.id.list_img);
        mImgAdapter = new ImgAdapter(this, mImgListView);
        mImgUrls = getImgUrls(urlFile);
        mImgAdapter.setData(mImgUrls.get(0));
        mImgListView.setAdapter(mImgAdapter);
        mImgListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImgAdapter madapter = (ImgAdapter) parent.getAdapter();
                TestImageLoader mImgLoader = new TestImageLoader(parent.getContext());
                mImgLoader.LoadImageByMe(madapter.getData()[position], (ImageView) view.findViewById(R.id.img));
            }
        });

        Button btn_refresh = (Button) findViewById(R.id.btn_refreshimg);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!imgswitch){
                    mImgAdapter.setData(mImgUrls.get(1));
                    mImgListView.setAdapter(mImgAdapter);
                }
                else {
                    mImgAdapter.setData(mImgUrls.get(0));
                    mImgListView.setAdapter(mImgAdapter);
                }
                imgswitch = !imgswitch;

            }
        });
    }

    private List<String[]> getImgUrls(String fileName){
        List<String[]> result = getFromAssets(fileName);
        return result;
    }

    public List<String[]> getFromAssets(String fileName){
        List<String[]> data = new ArrayList<>();
        JSONObject jOb;
        try {
            InputStreamReader inputReader = new InputStreamReader( getResources().getAssets().open(fileName), "UTF-8");
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line="";
            String Result="";
            while((line = bufReader.readLine()) != null)
                Result += line;
            jOb = new JSONObject(Result);
            JSONObject jb;
            JSONArray ja;
            jb = jOb.getJSONObject("img");
            ja = jb.getJSONArray("orgin");
            String[] sa = new String[ja.length()];
            for (int i=0; i<ja.length(); i++){
                sa[i] = ja.getString(i);
            }
            data.add(sa); // 加入起始图片组链接
            jb = jOb.getJSONObject("img");
            ja = jb.getJSONArray("new");
            sa = new String[ja.length()];
            for (int i=0; i<ja.length(); i++){
                sa[i] = ja.getString(i);
            }
            data.add(sa); // 加入更新图片组链接
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
