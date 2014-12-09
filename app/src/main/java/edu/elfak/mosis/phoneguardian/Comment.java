package edu.elfak.mosis.phoneguardian;

import java.io.Serializable;

public class Comment implements Serializable
{
		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		String id;
		String id_marker;
		String username_comment;
		String time;
		String rate;
		String comment;
		
		
	    @Override
	    public String toString()
	    { 
	      return "Marker [id="+id+", id_marker=" + id_marker + ", username_comment=" + username_comment + ", time=" + time +", rate=" + rate +", comment=" + comment +"]";
	    }

}
