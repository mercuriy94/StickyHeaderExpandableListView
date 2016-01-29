package com.mercuriy94.stickyheaderexpandablelistview;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MyExpandableAdapter extends CustomExpandableListAdapter {

	private Activity activity;
	private ArrayList<Object> childtems;
	private LayoutInflater inflater;
	private ArrayList<View> listChildrens = new ArrayList<>();
	private ArrayList<String> parentItems, child;



	public MyExpandableAdapter(ArrayList<String> parents, ArrayList<Object> childern) {
		this.parentItems = parents;
		this.childtems = childern;
	}

	public void setInflater(LayoutInflater inflater, Activity activity) {
		this.inflater = inflater;
		this.activity = activity;
	}

	
	@Override
	public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

		child = (ArrayList<String>) childtems.get(groupPosition);
		
		TextView textView = null;
		
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.group, null);
		}

		LinearLayout container = (LinearLayout)convertView.findViewById(R.id.container);
		if(childPosition == 17){
			//container.getLayoutParams().height = 500;
		}
		
		textView = (TextView) convertView.findViewById(R.id.textView1);
		textView.setText(child.get(childPosition));

		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Toast.makeText(activity, child.get(childPosition),
						Toast.LENGTH_SHORT).show();
			}
		});
		listChildrens.add(childPosition,convertView);
		return convertView;
	}
	
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.row, null);
		}
		TextView text_item = (TextView)convertView.findViewById(R.id.text_group);
		text_item.setText(parentItems.get(groupPosition));

		
		return convertView;
	}



	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return listChildrens.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return ((ArrayList<String>) childtems.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return null;
	}



	@Override
	public int getGroupCount() {
		return parentItems.size();
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);

	}

	private int lastExpandedPosition = -1;
	@Override
	public void onGroupExpanded(int groupPosition) {
		super.onGroupExpanded(groupPosition);

	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	@Override
	public View getGroupHeaderView(int group, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.row, null);
		}

		TextView text_item = (TextView)convertView.findViewById(R.id.text_group);
		text_item.setText(parentItems.get(group));

		return convertView;
	}
}
