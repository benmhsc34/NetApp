package com.openclassrooms.netapp.Controllers.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.openclassrooms.netapp.Controllers.Activities.DetailActivity;
import com.openclassrooms.netapp.Models.GithubUser;
import com.openclassrooms.netapp.Models.GithubUserInfo;
import com.openclassrooms.netapp.R;
import com.openclassrooms.netapp.Utils.GithubService;
import com.openclassrooms.netapp.Utils.GithubStreams;
import com.openclassrooms.netapp.Utils.GithubUsers;
import com.openclassrooms.netapp.Utils.GithubUsersResponse;
import com.openclassrooms.netapp.Utils.NetworkAsyncTask;
import com.openclassrooms.netapp.Utils.RESTAdapter;
import com.openclassrooms.netapp.Views.GithubUserAdapter;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;
import java.util.Observable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class DetailFragment extends Fragment implements GithubUserAdapter.Listener {
    private Disposable disposable;
    private List<GithubUser> githubUsers;
    GithubUserAdapter adapter;

    @BindView(R.id.followersNumberTextView)
    TextView followersTextView;
    @BindView(R.id.profileImageView)
    CircleImageView profileImageView;
    @BindView(R.id.followingNumberTextView)
    TextView followingTextView;
    @BindView(R.id.repositoriesNumberTextView)
    TextView repoTextView;
    @BindView(R.id.usernameTextView)
    TextView usernameTextView;

    RequestManager glide;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, view);

        Bundle bundle = getArguments();
        GithubUser user = (GithubUser) bundle.getSerializable("userSelected");

        RESTAdapter restAdapter = new RESTAdapter();
        restAdapter.getGithubService().gettFollowing(user.getLogin()).enqueue(new Callback<List<GithubUser>>() {
            @Override
            public void onResponse(Call<List<GithubUser>> call, Response<List<GithubUser>> response) {
                if (response.isSuccessful()){
                    Log.d("sizeoflist", response.body().size() + "");
                    followingTextView.setText(response.body().size() + "");

                }
            }

            @Override
            public void onFailure(Call<List<GithubUser>> call, Throwable t) {

            }
        });

        restAdapter.getGithubService().getFollowers(user.getLogin()).enqueue(new Callback<List<GithubUser>>() {
            @Override
            public void onResponse(Call<List<GithubUser>> call, Response<List<GithubUser>> response) {
                if (response.isSuccessful()){
                    followersTextView.setText(response.body().size() + "");
                }
            }

            @Override
            public void onFailure(Call<List<GithubUser>> call, Throwable t) {
                followersTextView.setText(t.getLocalizedMessage());

            }
        });

        restAdapter.getGithubService().getRepos(user.getLogin()).enqueue(new Callback<List<GithubUser>>() {
            @Override
            public void onResponse(Call<List<GithubUser>> call, Response<List<GithubUser>> response) {
                if  (response.isSuccessful()){
                    repoTextView.setText(response.body().size() + "");
                }
            }

            @Override
            public void onFailure(Call<List<GithubUser>> call, Throwable t) {

            }
        });


        assert user != null;
        usernameTextView.setText(user.getLogin());

        Picasso.get().load(user.getAvatarUrl()).into(profileImageView);
        this.executeHttpRequestWithRetrofit();


        return view;
    }


    private void executeHttpRequestWithRetrofit() {
        Bundle bundle = getArguments();
        GithubUser user = (GithubUser) bundle.getSerializable("userSelected");
        GithubStreams.streamFetchUserInfos(user.getLogin());
    }

    // 2 - Override callback methods

    @Override
    public void onResponse(List<GithubUser> users) {
        // 2.1 - When getting response, we update UI
        if (users != null) {
            this.updateUIWithListOfUsers(users);
        }
    }

    @Override
    public void onFailure() {
        // 2.2 - When getting error, we update UI
        this.updateUIWhenStopingHTTPRequest("An error happened !");
    }


    // 3 - Update UI showing only name of users
    private void updateUIWithListOfUsers(List<GithubUser> users) {
        StringBuilder stringBuilder = new StringBuilder();
        for (GithubUser user : users) {
            stringBuilder.append("-" + user.getLogin() + "\n");
        }
        updateUIWhenStopingHTTPRequest(stringBuilder.toString());
        Log.d("mhsc", stringBuilder.toString());
    }


    private void updateUIWhenStopingHTTPRequest(String response) {
        this.repoTextView.setText(response);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void doInBackground() {

    }

    @Override
    public void onPostExecute(String json) {

    }

    @Override
    public void onClickDeleteButton(int position) {

    }
}

