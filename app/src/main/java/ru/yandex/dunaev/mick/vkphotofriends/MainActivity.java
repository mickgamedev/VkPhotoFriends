package ru.yandex.dunaev.mick.vkphotofriends;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;
import com.vk.sdk.util.VKUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        VKSdk.login(this,VKScope.FRIENDS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                // Пользователь успешно авторизовался
                Log.v("VK","Пользователь авторизовался");

                VKRequest request = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS, "photo_200,photo_max_orig",
                        VKApiConst.EXTENDED,1));
                request.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        final VKList<VKApiUser> users = (VKList<VKApiUser>)response.parsedModel;
                        if(users == null) return;
                        for(VKApiUser apiUser : users){
                            String name = apiUser.first_name;
                            String last_name = apiUser.last_name;
                            String photo_200 = apiUser.photo_200;
                            String photo_max_orig = apiUser.photo_max_orig;

                            Log.v("Friend",name + " " + last_name);
                        }

                        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler);

                        recyclerView.setAdapter(new RecyclerView.Adapter() {
                            @NonNull
                            @Override
                            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                                CardView cv = (CardView)LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friend_card,viewGroup,false);
                                return new RecyclerView.ViewHolder(cv) {};
                            }

                            @Override
                            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
                                CardView cv = (CardView)viewHolder.itemView;

                                cv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(MainActivity.this,PhotoFriendActivity.class);
                                        intent.putExtra(PhotoFriendActivity.EXTRA_PHOTO_URL,users.get(i).photo_max_orig);
                                        startActivity(intent);
                                    }
                                });

                                ImageView photo = (ImageView)cv.findViewById(R.id.imageFriend);
                                TextView name = (TextView)cv.findViewById(R.id.textName);
                                TextView lastName = (TextView)cv.findViewById(R.id.textLastName);
                                name.setText(users.get(i).first_name);
                                lastName.setText(users.get(i).last_name);
                                Picasso.get().load(users.get(i).photo_200).into(photo);


                            }

                            @Override
                            public int getItemCount() {
                                return users.size();
                            }
                        });

                        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this,2));
                    }
                });

            }
            @Override
            public void onError(VKError error) {
                // Произошла ошибка авторизации (например, пользователь запретил авторизацию)
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
