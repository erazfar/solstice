package com.example.solstice;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class FeedReaderActivity extends Activity implements OnItemClickListener {
	
	private static final String LOG_TAG = "FeedReaderActivity";
	private static final String FEED_URL = "http://blog.solstice-mobile.com/feeds/posts/default?alt=rss";
	private static final int PARSE_SUCCESS = 0x0;
	private static final int PARSE_FAIL = 0x1;

	private List<FeedItem> items;
	private ProgressBar progress;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed_reader);

		items = new ArrayList<FeedItem>();
		progress = (ProgressBar) findViewById(R.id.main_progress);
		startTask(); // Make the feed request
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.d(LOG_TAG, "Item " + position + " was clicked.");
		
		//opens a link in the browser
		Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(items.get(position).getUrl()));
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Builds the menu containing one "refresh" button
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.refresh:
			startTask();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}
	/**
	 * Checks to see if an Internet connection is available
	 * @return true if connection is available, false if it is not
	 */
	private boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnectedOrConnecting();
	}
	
	private void setListAdapter(ListAdapter adapter) {
		ListView list = (ListView) findViewById(R.id.main_list);
		list.setOnItemClickListener(this);
		list.setAdapter(adapter);
	}
	
	private void startTask() {
		if (isOnline()) {
			new ParseTask().execute(FEED_URL);				
		} else {
			Toast.makeText(getApplicationContext(), "No internet connection found.", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Parses the feed XML on a background thread.
	 */
	private class ParseTask extends AsyncTask<String, Integer, Integer> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress.setVisibility(View.VISIBLE);
		}

		@Override
		protected Integer doInBackground(String... params) {
			
			try {				
				String url = params[0];
				XmlHandler handler = new XmlHandler();
				items = handler.getLatestArticles(url);
				
			} catch (Exception e) {
				return PARSE_FAIL;	
			}
			
			return PARSE_SUCCESS;	
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			progress.setVisibility(View.GONE);
			
			// Add the items to a list adapter and display them in the list.
			setListAdapter(new FeedListAdapter(FeedReaderActivity.this, items));
		}

	}
}
