/*  
 *  Copyright (C) 2010  Luca Wehrstedt
 *
 *  This file is released under the GPLv2
 *  Read the file 'COPYING' for more information
 */

import javax.swing.JFrame;
import javax.swing.Timer;



public class PongWindow extends JFrame {

	Pong content;
	String playerNum;
	String currentServer;

	public PongWindow (int numPlayers, String playNum, String currServer) {
		super ();
		
		setTitle ("Pong");
		setSize (700, 700);

		playerNum = playNum;
		currentServer = currServer;

		// assigning players to paddle on the basis of number of player in the game and rst to AI
		if(numPlayers == 4)
		{
			if(playerNum.equals("p1"))
				content = new Pong (Player.KEYBOARD, Player.UDP, Player.UDP, Player.UDP, playerNum, currentServer);
			else if(playerNum.equals("p2"))
				content = new Pong (Player.UDP, Player.KEYBOARD, Player.UDP, Player.UDP, playerNum, currentServer);
			else if(playerNum.equals("p3"))
				content = new Pong (Player.UDP, Player.UDP, Player.KEYBOARD, Player.UDP, playerNum, currentServer);
			else if(playerNum.equals("p4"))
				content = new Pong (Player.UDP, Player.UDP, Player.UDP, Player.KEYBOARD, playerNum, currentServer);
		}
		else if(numPlayers == 3)
		{
			if(playerNum.equals("p1"))
				content = new Pong (Player.KEYBOARD, Player.UDP, Player.UDP, Player.CPU_HARD_Y, playerNum, currentServer);
			else if(playerNum.equals("p2"))
				content = new Pong (Player.UDP, Player.KEYBOARD, Player.UDP, Player.CPU_HARD_Y, playerNum, currentServer);
			else if(playerNum.equals("p3"))
				content = new Pong (Player.UDP, Player.UDP, Player.KEYBOARD, Player.CPU_HARD_Y, playerNum, currentServer);
		}
		else if(numPlayers == 2)
		{
			if(playerNum.equals("p1"))
				content = new Pong (Player.KEYBOARD, Player.UDP, Player.CPU_HARD_Y, Player.CPU_HARD_Y, playerNum, currentServer);
			else if(playerNum.equals("p2"))
				content = new Pong (Player.UDP, Player.KEYBOARD, Player.CPU_HARD_Y, Player.CPU_HARD_Y, playerNum, currentServer);
		}
		else if(numPlayers == 1)
		{
			content = new Pong (Player.KEYBOARD, Player.CPU_HARD_X, Player.CPU_HARD_Y, Player.CPU_HARD_Y, playerNum, currentServer);
		}
		else 		//in case of 0. This is to join a game
		{
			if(playerNum.equals("p2"))
				content = new Pong (Player.UDP, Player.KEYBOARD, Player.CPU_HARD_Y, Player.CPU_HARD_Y, playerNum, currentServer);
			else if(playerNum.equals("p3"))
				content = new Pong (Player.UDP, Player.UDP, Player.KEYBOARD, Player.CPU_HARD_Y, playerNum, currentServer);
			else if(playerNum.equals("p4"))
				content = new Pong (Player.UDP, Player.UDP, Player.UDP, Player.KEYBOARD, playerNum, currentServer);
		}
		content.acceleration = false;
		getContentPane ().add (content);
		
		addMouseListener (content);
		addKeyListener (content);
		
		Timer timer = new Timer (20, content);
		timer.start ();
	}

	public void setAllPos(String s)
	{
	
		String[] data = s.split(" ");

			if(data[0].equals(currentServer))
			{
				content.externalBallX = data[1];
				content.externalBallY = data[2];
				content.externalBallSpeedX = data[3];
				content.externalBallSpeedY = data[4];
				content.scores[0] = Integer.parseInt(data[6]);
				content.scores[1] = Integer.parseInt(data[7]);
				content.scores[2] = Integer.parseInt(data[8]);
				content.scores[3] = Integer.parseInt(data[9]);
			}	

			if(data[0].equals("p1"))
			{
				content.udpPosition[0] = data[5];
				content.springHitsLeft[0] = Integer.parseInt(data[10]);
				content.isExtended[0] = Boolean.parseBoolean(data[11]);
			}
			else if(data[0].equals("p2"))
			{
				content.udpPosition[1] = data[5];
				content.springHitsLeft[1] = Integer.parseInt(data[10]);
				content.isExtended[1] = Boolean.parseBoolean(data[11]);
			}	
			else if(data[0].equals("p3"))
			{
				content.udpPosition[2] = data[5];
				content.springHitsLeft[2] = Integer.parseInt(data[10]);
				content.isExtended[2] = Boolean.parseBoolean(data[11]);
			}	
			else if(data[0].equals("p4"))
			{
				content.udpPosition[3] = data[5];
				content.springHitsLeft[3] = Integer.parseInt(data[10]);
				content.isExtended[3] = Boolean.parseBoolean(data[11]);
			}	
	}
	// changing plahyer to AI
	public void changePlayerToAI(String playerNo)
	{
		content.setPlayerToAI(playerNo);
	}
	// Changing server
	public void changeServer(String nextServer)
	{
		currentServer = nextServer;
		content.currentServer = nextServer;
	}
}
