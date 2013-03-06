package com.example.solstice;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class FeedListAdapter extends ArrayAdapter<FeedItem> {
	List<FeedItem> items = null;

	public FeedListAdapter(Activity activity, List<FeedItem> items) {
		super(activity, 0, items);
		this.items = items;
	}

	/*
	 * Makes 3 new TextViews for Title, Date published, and Author.
	 * setText from item.
	 */
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.list_item, null);
		
		TextView title = (TextView) rowView.findViewById(R.id.list_item_title);
		TextView pubDate = (TextView) rowView.findViewById(R.id.list_item_detail);
		TextView author = (TextView) rowView.findViewById(R.id.list_item_author);
		
		title.setText(items.get(position).getTitle());
		pubDate.setText(items.get(position).getDatePublished());
		author.setText(items.get(position).getAuthor());
		
		
		

		return rowView;

	}

}