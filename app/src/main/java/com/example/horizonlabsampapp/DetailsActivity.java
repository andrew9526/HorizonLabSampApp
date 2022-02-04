package com.example.horizonlabsampapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

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

public class DetailsActivity extends AppCompatActivity {

    JSONParser jParser = new JSONParser();
    JSONArray datadatabase = null;
    boolean stat=false,tryCatch=false;
    Bundle extras;
    List<String> comments,
            user_id,
            date_added,
            name;

    MyCustomAdapter dataAdapter = null;
    ArrayList<CommentHolder> commentHolder = new ArrayList<CommentHolder>();
    CommentHolder loo;
    String tname,temail,tcontact,tcivil_status,tage,tsex,tlocation,selectedID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        extras = getIntent().getExtras();

        TextView txtName =  findViewById(R.id.txtName);
        TextView txtTitle = findViewById(R.id.txtTitle);
        TextView txtDetails = findViewById(R.id.txtDetails);
        CircleImageView imgView =  findViewById(R.id.imgView);
        VideoView videoView = findViewById(R.id.vidView);


            if(extras != null) {

                txtName.setText(extras.getString("poster"));
                txtTitle.setText(extras.getString("feed_header"));
                txtDetails.setText(extras.getString("feed_details"));
                Glide.with(getApplicationContext())
                        .load(extras.getString("videoimage"))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(imgView);
                videoView.setVideoURI(Uri.parse(extras.getString("videolink")));
                videoView.start();
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                            @Override
                            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                                /*
                                 * add media controller
                                 */
                                MediaController mediaController = new MediaController(DetailsActivity.this);
                                videoView.setMediaController(mediaController);
                                /*
                                 * and set its position on screen
                                 */
                                mediaController.setAnchorView(videoView);
                            }
                        });
                    }
                });

            }

        new getDetails().execute();
    }

    class getDetails extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            comments = new ArrayList<>();
            user_id = new ArrayList<>();
            date_added = new ArrayList<>();
            name = new ArrayList<>();

        }

        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("feed_id",extras.getString("feed_id")));
            JSONObject json = jParser.makeHttpRequest(ip+"horizon_details.php", "GET", params);

            try {
                int success = json.getInt("success");
                if (success == 1) {
                    stat=true;
                    datadatabase = json.getJSONArray("feed_details");
                    for(int z=0;z<datadatabase.length();z++) {
                        JSONObject data = datadatabase.getJSONObject(z);
                        comments.add(data.getString("comments"));
                        user_id.add(data.getString("user_id"));
                        date_added.add(data.getString("date_added"));
                        name.add(data.getString("name"));
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
                if(tryCatch){
                    Toast.makeText(getApplicationContext(), "Connection error", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }


    class getProfile extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("user_id",selectedID));
            Log.i("ryan Biugos",params+"");
            JSONObject json = jParser.makeHttpRequest(ip+"horizon_profile.php", "GET", params);

            try {
                int success = json.getInt("success");
                if (success == 1) {
                    stat=true;
                    datadatabase = json.getJSONArray("user_profile");
                    for(int z=0;z<datadatabase.length();z++) {
                        JSONObject data = datadatabase.getJSONObject(z);
                        tname=data.getString("name");
                        temail=data.getString("email");
                        tcontact=data.getString("contact");
                        tcivil_status=data.getString("civil_status");
                        tage=data.getString("age");
                        tsex=data.getString("sex");
                        tlocation=data.getString("location");
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
                showProfile(tname,
                            temail,
                            tcontact,
                            tcivil_status,
                            tage,
                            tsex,
                            tlocation);
            }else{
                if(tryCatch){
                    Toast.makeText(getApplicationContext(), "Connection error", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    void insertData(){

        ListView listView = findViewById(R.id.commentList);
        commentHolder = new ArrayList<CommentHolder>();

        if(comments.size()>0){
            for (int x = 0; x < comments.size(); x++) {

                loo = new CommentHolder(
                        comments.get(x),
                        user_id.get(x),
                        date_added.get(x),
                        name.get(x));
                commentHolder.add(loo);

            }

            dataAdapter = new MyCustomAdapter(R.layout.list_comment, commentHolder);
            listView.setAdapter(dataAdapter);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedID = user_id.get(i);
                new getProfile().execute();
            }
        });


    }

    private class MyCustomAdapter extends ArrayAdapter<CommentHolder> {

        private ArrayList<CommentHolder> loo;

        public MyCustomAdapter(int textViewResourceId,
                               ArrayList<CommentHolder> loo) {
            super((Context)DetailsActivity.this, textViewResourceId, loo);
            this.loo = new ArrayList<CommentHolder>();
            this.loo.addAll(loo);
        }

        private class ViewHolder {

            TextView txtName,txtDetails;
            CircleImageView imgView;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            MyCustomAdapter.ViewHolder holder = null;

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.list_comment, null);

                holder = new MyCustomAdapter.ViewHolder();
                holder.txtName =  convertView.findViewById(R.id.txtName);
                holder.txtDetails = convertView.findViewById(R.id.txtDetails);
                holder.imgView =  convertView.findViewById(R.id.imgView);
                convertView.setTag(holder);

            }

            else {
                holder = (MyCustomAdapter.ViewHolder) convertView.getTag();
            }

            CommentHolder thValue = loo.get(position);

                holder.txtName.setText(thValue.getName());
                holder.txtDetails.setText(thValue.getComments());
                Glide.with(getApplicationContext())
                        .load(getResources().getDrawable(R.drawable.baseline_person_grey_200_24dp))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(holder.imgView);

            return convertView;
        }
    }

    void showProfile(String tName,
                     String tEmail,
                     String tContact,
                     String tCivilStatus,
                     String tAge,
                     String tSex,
                     String tAddress){

        final Dialog userProfile = new Dialog(DetailsActivity.this);
        userProfile.requestWindowFeature(Window.FEATURE_NO_TITLE);
        userProfile.setContentView(R.layout.dialog_profile);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(userProfile.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        userProfile.getWindow().setAttributes(lp);

        CircleImageView imgView= userProfile.findViewById(R.id.imgView);
        TextView txtName = userProfile.findViewById(R.id.txtName);
        TextView txtEmail = userProfile.findViewById(R.id.txtEmail);
        TextView txtContact = userProfile.findViewById(R.id.txtContact);
        TextView txtCivilStatus = userProfile.findViewById(R.id.txtCivilStatus);
        TextView txtAge = userProfile.findViewById(R.id.txtAge);
        TextView txtSex = userProfile.findViewById(R.id.txtSex);
        TextView txtAddress = userProfile.findViewById(R.id.txtAddress);


        txtName.setText(tName);
        txtEmail.setText(tEmail);
        txtContact.setText(tContact);
        txtCivilStatus.setText(tCivilStatus);
        txtAge.setText(tAge);
        txtSex.setText(tSex);
        txtAddress.setText(tAddress);

        userProfile.show();
    }

}