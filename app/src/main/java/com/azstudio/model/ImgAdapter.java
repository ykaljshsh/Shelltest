package com.azstudio.model;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import com.azstudio.shelltest.R;
import com.azstudio.util.TestImageLoader;
import com.azstudio.util.ViewHolder;

public class ImgAdapter extends BaseAdapter{

    private LayoutInflater mInflater;
    private Context mContext;
    public ViewHolder holder = null;
    public String[] img_urls;
    private TestImageLoader mImgLoader;


    public ImgAdapter(Context context, ListView listView) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        mImgLoader = new TestImageLoader(mContext);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return img_urls.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_img, null);
        }
        holder = new ViewHolder();
        convertView.setTag(position);
        holder.img = (ImageView) convertView.findViewById(R.id.img);
        mImgLoader.LoadImage(img_urls[position], holder.img);

        return convertView;

    }

    public void setData(String[] data) {
        img_urls = data;
    }

    public String[] getData() {
        return img_urls;
    }

}
