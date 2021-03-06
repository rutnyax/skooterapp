package com.skooterapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.skooterapp.data.Post;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FavoritesActivity extends BaseActivity {

    protected final static String LOG_TAG = FlagActivity.class.getSimpleName();
    private ArrayList<Post> mPostsList = new ArrayList<Post>();
    private PostAdapter mPostsAdapter;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

        activateToolbarWithHomeEnabled("Favorites");

        mListView = (ListView) findViewById(R.id.list_favorites);

        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", Integer.toString(userId));

        String url = substituteString(getResources().getString(R.string.user_favorites), params);

        SkooterJsonArrayRequest jsonArrayRequest = new SkooterJsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    mPostsList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        Post postObject = Post.parsePostFromJSONObject(response.getJSONObject(i));
                        mPostsList.add(postObject);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, "Error processing Json Data");
                }
                mPostsAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(LOG_TAG, error.getMessage());
            }
        });

        AppController.getInstance().addToRequestQueue(jsonArrayRequest, "favorite_post");

        mPostsAdapter = new PostAdapter(this, R.layout.list_view_post_row, mPostsList, true);

        mListView.setAdapter(mPostsAdapter);
        mListView.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FavoritesActivity.this, ViewPostActivity.class);
                intent.putExtra(SKOOTER_POST, mPostsList.get(position));
                startActivity(intent);
            }
        });
    }

    protected void onPause() {
        super.onPause();
        //closing transition animations
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_favorites, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
