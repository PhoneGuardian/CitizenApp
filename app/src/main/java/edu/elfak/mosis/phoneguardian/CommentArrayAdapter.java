package edu.elfak.mosis.phoneguardian;

import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
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
    RatingBar rate = (RatingBar) convertView.findViewById(R.id.rate_list_item);
    ImageView imageView = (ImageView) convertView.findViewById(R.id.comment_picture_list_item);
    
    username.setText(s.username_comment);
    date.setText(s.time);
    comment.setText(s.comment);
    rate.setRating(Float.parseFloat(s.rate));
    try
    {
		 URL url = new URL("http://nikolamilica10.site90.com/photos_of_comments/"+s.id_marker+"_"+s.id+".jpg");
		 Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
		 imageView.setImageBitmap(bmp);
    }
    catch(Exception e)
    {
   	 
    };
    
    return convertView;
  }
}
