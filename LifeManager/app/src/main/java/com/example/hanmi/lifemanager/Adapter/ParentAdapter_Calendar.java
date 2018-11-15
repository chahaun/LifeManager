package com.example.hanmi.lifemanager.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.hanmi.lifemanager.R;
import com.example.hanmi.lifemanager.VO.ParentListData;

import java.util.ArrayList;

public class ParentAdapter_Calendar extends BaseExpandableListAdapter {

    private static final int PARENT_ROW = R.layout.calendar_parent_row;
    private static final int CHILD_ROW = R.layout.calendar_child_row;
    private Context context;
    private ArrayList<ParentListData> data;
    private LayoutInflater inflater = null;
    private ExpandableListView listView;

    public ParentAdapter_Calendar(Context context, ArrayList<ParentListData> data){
        this.data = data;
        this.context = context;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(PARENT_ROW, parent, false);
        }
        //여기의 convertView는 부모리스트
        TextView Work_manager = (TextView) convertView.findViewById(R.id.tv_work_Manager);
        Work_manager.setText(data.get(groupPosition).getName());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(CHILD_ROW, parent, false);
        }
        //여기의 convertView는 자식리스트
        TextView Schedule_Name = (TextView) convertView.findViewById(R.id.tv_schedule_name);
        TextView Probability = (TextView) convertView.findViewById(R.id.tv_probability);
        Schedule_Name.setText(data.get(groupPosition).childListData.get(childPosition).getTitle());
        Probability.setText(" 성공률 : "+data.get(groupPosition).childListData.get(childPosition).getProbability()+"%");

        return convertView;
    }
    @Override
    public int getGroupCount() {
        return data.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) { return data.get(groupPosition).childListData.size(); }

    @Override
    public Object getGroup(int groupPosition) {
        return data.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return data.get(groupPosition).childListData.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
