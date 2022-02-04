package com.example.horizonlabsampapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.horizonlabsampapp.Splash.ip;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    SwipeMenuListView listView;
    SwipeRefreshLayout swipeRefreshLayout;
    JSONParser jParser = new JSONParser();
    JSONArray datadatabase = null;
    boolean stat=false,tryCatch=false;
    List<String> feed_id,
            feed_header,
            feed_details,
            date_added,
            status,
            videolink,
            videoimage,
            poster;

    MyCustomAdapter dataAdapter = null;
    ArrayList<ListHolder> listHolder = new ArrayList<ListHolder>();
    ListHolder loo;
    public static String selectedFeed="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        initListView();
        onRefresh();


    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        new getFeed().execute();
    }

    void initListView(){
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                openItem.setWidth(200);
                openItem.setTitle("Share");
                openItem.setTitleSize(14);
                openItem.setIcon(getResources().getDrawable(R.drawable.baseline_share_blue_400_24dp));
                openItem.setTitleColor(Color.WHITE);
                openItem.setBackground(getResources().getDrawable(R.drawable.buttonclick));
                menu.addMenuItem(openItem);
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                deleteItem.setWidth(200);
                deleteItem.setTitle("Bookmark");
                deleteItem.setTitleSize(14);
                deleteItem.setIcon(getResources().getDrawable(R.drawable.baseline_bookmark_add_red_400_24dp));
                deleteItem.setTitleColor(Color.WHITE);
                deleteItem.setBackground(getResources().getDrawable(R.drawable.buttonclick));
                menu.addMenuItem(deleteItem);
            }
        };


        listView.setMenuCreator(creator);

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        
                        break;
                    case 1:

                        break;
                }
                
                return false;
            }
        });

        listView.setSwipeDirection(SwipeMenuListView.DIRECTION_RIGHT);

        listView.setCloseInterpolator(new BounceInterpolator());
        listView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                listView.smoothOpenMenu(position);
            }

            @Override
            public void onSwipeEnd(int position) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ListHolder thValue = listHolder.get(position);
                Intent i = new Intent(MainActivity.this,DetailsActivity.class);
                i.putExtra("feed_id",thValue.getFeed_id());
                i.putExtra("feed_header",thValue.getFeed_header());
                i.putExtra("feed_details",thValue.getFeed_details());
                i.putExtra("date_added",thValue.getDate_added());
                i.putExtra("videolink",thValue.getVideolink());
                i.putExtra("videoimage",thValue.getVideoimage());
                i.putExtra("poster",thValue.getPoster());
                startActivity(i);

            }
        });
    }

    class getFeed extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            feed_id=new ArrayList<>();
            feed_header=new ArrayList<>();
            feed_details=new ArrayList<>();
            date_added=new ArrayList<>();
            status=new ArrayList<>();
            videolink=new ArrayList<>();
            videoimage=new ArrayList<>();
            poster=new ArrayList<>();
        }

        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            JSONObject json = jParser.makeHttpRequest(ip+"horizon_feed.php", "GET", params);

            try {
                int success = json.getInt("success");
                if (success == 1) {
                    stat=true;
                    datadatabase = json.getJSONArray("feed");
                    for(int z=0;z<datadatabase.length();z++) {
                        JSONObject data = datadatabase.getJSONObject(z);
                        feed_id.add(data.getString("feed_id"));
                        feed_header.add(data.getString("feed_header"));
                        feed_details.add(data.getString("feed_details"));
                        date_added.add(data.getString("date_added"));
                        status.add(data.getString("status"));
                        videolink.add(data.getString("videolink"));
                        videoimage.add(data.getString("videoimage"));
                        poster.add(data.getString("poster"));
                    }

                }
                else{
                    stat=false;
                    tryCatch=false;
                }
            }
            catch (JSONException e) {
                stat=false;
                tryCatch=true;
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(String file_url) {
            if(stat){
                insertData();
            }else{
                swipeRefreshLayout.setRefreshing(false);
                if(tryCatch){
                    Toast.makeText(getApplicationContext(), "Connection error", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    void insertData(){

        listHolder = new ArrayList<ListHolder>();

        if(feed_id.size()>0){
            for (int x = 0; x < feed_id.size(); x++) {

                loo = new ListHolder(
                        feed_id.get(x),
                        feed_header.get(x),
                        feed_details.get(x),
                        date_added.get(x),
                        status.get(x),
                        videolink.get(x),
                        videoimage.get(x),
                        poster.get(x));
                listHolder.add(loo);

            }

            dataAdapter = new MyCustomAdapter(R.layout.list_data, listHolder);
            listView.setAdapter(dataAdapter);
        }

        swipeRefreshLayout.setRefreshing(false);
    }

    private class MyCustomAdapter extends ArrayAdapter<ListHolder> {

        private ArrayList<ListHolder> loo;

        public MyCustomAdapter(int textViewResourceId,
                               ArrayList<ListHolder> loo) {
            super((Context)MainActivity.this, textViewResourceId, loo);
            this.loo = new ArrayList<ListHolder>();
            this.loo.addAll(loo);
        }

        private class ViewHolder {

            TextView txtName,txtTitle,txtDetails;
            CircleImageView imgView;
            LinearLayout linLay;
            VideoView videoView;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.list_data, null);

                holder = new ViewHolder();
                holder.txtName =  convertView.findViewById(R.id.txtName);
                holder.txtTitle = convertView.findViewById(R.id.txtTitle);
                holder.txtDetails = convertView.findViewById(R.id.txtDetails);
                holder.imgView =  convertView.findViewById(R.id.imgView);
                holder.linLay = convertView.findViewById(R.id.linLay);
                holder.videoView = convertView.findViewById(R.id.vidView);
                convertView.setTag(holder);

            }

            else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.linLay.setTag(convertView);
            ListHolder thValue = loo.get(position);
            if(thValue.getStatus().matches("1")) {

                holder.videoView.setVisibility(View.VISIBLE);
                holder.videoView.setVideoURI(Uri.parse(thValue.getVideolink()));
                holder.videoView.start();
                holder.txtName.setText(thValue.getPoster());
                holder.txtTitle.setText(thValue.getFeed_header());
                holder.txtDetails.setText(thValue.getFeed_details());
                Glide.with(getApplicationContext())
                        .load(thValue.getVideoimage())
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(holder.imgView);

            }else {
                holder.videoView.setVisibility(View.GONE);
                holder.txtName.setText(thValue.getPoster());
                holder.txtTitle.setText(thValue.getFeed_header());
                holder.txtDetails.setText(thValue.getFeed_details());
                Glide.with(getApplicationContext())
                        .load(thValue.getVideoimage())
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(holder.imgView);
            }


            return convertView;
        }
    }



}