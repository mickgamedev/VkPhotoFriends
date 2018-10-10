package ru.yandex.dunaev.mick.vkphotofriends;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class PhotoFriendActivity extends AppCompatActivity {

    public static String EXTRA_PHOTO_URL = "photo url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_friend);
        Intent intent = getIntent();
        String url = intent.getStringExtra(EXTRA_PHOTO_URL);
        if(!url.isEmpty()){
            Picasso.get().load(url).into((ImageView) findViewById(R.id.photo));
        }
    }
}
