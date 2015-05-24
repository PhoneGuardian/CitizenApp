package it.polimi.guardian.citizenapp;

import java.io.Serializable;

public class Comment implements Serializable
{
		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		String id;
		String id_event;
		String username;
		String comment_date;
		String comment_text;
		
		
	    @Override
	    public String toString()
	    { 
	      return "Marker [id="+id+", id_event=" + id_event + ", username=" + username + ", comment_date=" + comment_date +", comment_text=" + comment_text +"]";
	    }

}
