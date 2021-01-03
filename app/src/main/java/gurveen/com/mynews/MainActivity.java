package gurveen.com.mynews;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> titles = new ArrayList<>();
    ArrayList<String> contents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DownloadTask downloadTask = new DownloadTask();
        try {
            downloadTask.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");

        } catch (Exception e) {
            e.printStackTrace();
        }

        ListView listView = findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titles);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), NewsActivity.class);
                intent.putExtra("content", contents.get(position));
                startActivity(intent);
            }
        });

        updateListView();
    }

    public void updateListView() {
        String[] projections = {DbContract.DbEntry.COLUMN_CONTENT_NAME, DbContract.DbEntry.COLUMN_TITLE_NAME};
        Cursor c = getContentResolver().query(DbContract.DbEntry.CONTENT_URI, null, null, null, null);

        int contentIndex = c.getColumnIndex(DbContract.DbEntry.COLUMN_CONTENT_NAME);
        int titleIndex = c.getColumnIndex(DbContract.DbEntry.COLUMN_TITLE_NAME);

        if (c.moveToFirst()) {
            titles.clear();
            contents.clear();

            do {
                titles.add(c.getString(titleIndex));
                contents.add(c.getString(contentIndex));

            } while (c.moveToNext());

            arrayAdapter.notifyDataSetChanged();
        }

    }



    public class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            String result = "";

            URL url;
            HttpsURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);

                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(inputStream);

                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                JSONArray jsonArray = new JSONArray(result);

                int numberOfStories = 20;

                if (jsonArray.length() < 20) {
                    numberOfStories = jsonArray.length();
                }

                int deleteInt = getContentResolver().delete(DbContract.DbEntry.CONTENT_URI, null, null);

                for (int i = 0; i < numberOfStories; i++) {
                    String articleId = jsonArray.getString(i);

                    url = new URL(" https://hacker-news.firebaseio.com/v0/item/" + articleId + ".json?print=pretty");

                    urlConnection = (HttpsURLConnection) url.openConnection();
                    urlConnection.connect();

                    inputStream = urlConnection.getInputStream();

                    reader = new InputStreamReader(inputStream);

                    String articleInfo = "";

                    data = reader.read();
                    while (data != -1) {
                        char current = (char) data;
                        articleInfo += current;
                        data = reader.read();
                    }

                    JSONObject jsonObject = new JSONObject(articleInfo);
                    if (!jsonObject.isNull("title") && !jsonObject.isNull("url")) {

                        String articleTitle = jsonObject.getString("title");
                        String articleUrl = jsonObject.getString("url");

                        url = new URL(articleUrl);
                        urlConnection = (HttpsURLConnection) url.openConnection();

                        inputStream = urlConnection.getInputStream();
                        reader = new InputStreamReader(inputStream);

                        data = reader.read();

                        String articleContent = "";
                        while (data != -1) {
                            char current = (char) data;
                            articleContent += current;
                            data = reader.read();
                        }

                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DbContract.DbEntry._ID, articleId);
                        contentValues.put(DbContract.DbEntry.COLUMN_TITLE_NAME, articleTitle);
                        contentValues.put(DbContract.DbEntry.COLUMN_CONTENT_NAME, articleContent);

                        Uri insertUri = getContentResolver().insert(DbContract.DbEntry.CONTENT_URI, contentValues);

                    }


                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            updateListView();
        }
    }



}
