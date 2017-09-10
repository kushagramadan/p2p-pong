/*  
 *  Copyright (C) 2010  Luca Wehrstedt
 *
 *  This file is released under the GPLv2
 *  Read the file 'COPYING' for more information
 */

import javax.swing.JOptionPane;

public class Player {

	public static final int CPU_EASY_X = 0;
	public static final int CPU_HARD_X = 1;
	public static final int CPU_EASY_Y = 2;
	public static final int CPU_HARD_Y = 3;
	public static final int MOUSE = 4;
	public static final int KEYBOARD = 5;
	public static final int UDP = 6;
	
	private int type;
	public int position = 0;
	public int destination = 0;
	public int points = 0;
	public int height=0;
	public boolean hasLost=false;
	public Player (int type) {
		if (type < 0 || type > 6) {
			type = CPU_EASY_X;
			JOptionPane.showMessageDialog (null, "Some errors in player definition");
		}
		this.type = type;
	}
	// returns the type of player
	public int getType () {
		return type;
	}
	//set the type of player
	public void setType(int t)
	{
		type = t;
	}

}
