package it.polimi.guardian.citizenapp;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CommentArrayAdapter extends ArrayAdapter<Comment>{

	public final Context context;
	private final Comment[] comments;
	private LayoutInflater inflater;
	private int layout;

  public CommentArrayAdapter(Context context,int layout, Comment[] values) {
    super(context, layout, values);
    
    this.context = context;
    this.comments = values;
    this.layout = layout;
    inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }

@Override
  public View getView(int position, View convertView, ViewGroup parent)
{
   
    convertView = inflater.inflate(layout, null);
    
    Comment s = comments[position];
    TextView username = (TextView) convertView.findViewById(R.id.username_comment_list_item);
    TextView date = (TextView) convertView.findViewById(R.id.date_comment_list_item);
    TextView comment = (TextView) convertView.findViewById(R.id.comment_list_item);
    
    username.setText(s.username);
    date.setText(s.comment_date);
    comment.setText(s.comment_text);
    
    return convertView;
  }
}
