

import javax.swing.JPanel;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.*;
import javax.imageio.ImageIO;
import java.awt.event.MouseListener;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.Polygon;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import java.io.*;
import java.net.*;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

public class Pong extends JPanel implements ActionListener, MouseListener, KeyListener {
	 public static final int PREF_W = 400;
  public static final int PREF_H = PREF_W;
	private static final int RADIUS = 10; 
	private static int START_SPEED = 6; 
	private static final int ACCELERATION = 125; 
	private static final int SPEED = 12; 
	private int HEIGHT = 50; 
	private static final int WIDTH = 20;
	private static final int TOLERANCE = 5;
	private static final int PADDING = 0;
	private static final int TRIANGLE_SIZE = 50;
	private Image backgroundImage;	
	public Player player1;
	public Player player2;
	public Player player3;
	public Player player4;
	
	private boolean new_game = true;
	
	public int ball_x;
	private int ball_y;
	public double ball_x_speed;
	public double ball_y_speed;
	
	public boolean acceleration = true;
	private int ball_acceleration_count;
	
	private boolean mouse_inside = false;
	private boolean key_up = false;
	private boolean key_down = false;
	private boolean key_left = false;
	private boolean key_right = false;
	private boolean springHit = false;
	private int numberOfSpringHits = 3;
	private boolean paddle_size = false, increased_once=false;
	private int numberOfframes=0;
	private int maxNumberOfFrames = 500;
	private static final int MAX_POINTS = 10;
	private static final double SPRING_STRENGTH = 1.35;
	public int popUpTimeLeft[];
	public String [] udpPosition;
	public int [] springHitsLeft;
	public int p1SpringHitsLeft, p2SpringHitsLeft, p3SpringHitsLeft, p4SpringHitsLeft;
	public int [] scores;
	public boolean [] isExtended;
	public String externalBallX = Integer.toString(getWidth()/2);
	public String externalBallY = Integer.toString(getHeight()/2);
	public String externalBallSpeedX = "0.0";
	public String externalBallSpeedY = "0.0";
	public String playerNum;
	public String currentServer;
	private TriangleShape triangleShape1;
	private TriangleShape triangleShape2;
	private TriangleShape triangleShape3;
	private TriangleShape triangleShape4;	
	public int state = 1;
	public MulticastSocket s;
	public InetAddress group;
	
	// Constructor
	public Pong (int p1_type, int p2_type, int p3_type, int p4_type, String playNum, String currServer) {
		super ();
		setBackground (new Color (215, 91, 19));
		numberOfframes=0;
		player1 = new Player (p1_type);
		player2 = new Player (p2_type);
		player3 = new Player (p3_type);
		player4 = new Player (p4_type);
		playerNum = playNum;
		currentServer = currServer;
		udpPosition = new String[4];
		springHitsLeft = new int[4];
		scores = new int[4];
		isExtended = new boolean[4];
		popUpTimeLeft = new int[4];
		try{
			backgroundImage = ImageIO.read(new File("final.jpg"));				//setting background image of the panel
		}catch(IOException e){System.out.println("Image not found!");}
		for(int i = 0; i<4; i++)
		{
			udpPosition[i] = "100";
		}
		for(int i = 0; i<4; i++)											//initialising number of spring hit of each player
		{
			springHitsLeft[i] = numberOfSpringHits;
		}
		p1SpringHitsLeft = numberOfSpringHits;
		p2SpringHitsLeft = numberOfSpringHits;
		p3SpringHitsLeft = numberOfSpringHits;
		p4SpringHitsLeft = numberOfSpringHits;
		for(int i = 0; i<4; i++)										//initialising score of each player
		{
			scores[i] = 0;
			popUpTimeLeft[i] = 100;
		}
		for(int i = 0; i<4; i++)
		{
			isExtended[i] = false;
		}
		try{
			s = new MulticastSocket(1234);
        	group = InetAddress.getByName("228.6.7.8");                     
        	s.setReuseAddress(true);             
        	s.setLoopbackMode(false);                  
        	s.setTimeToLive(2);                  
        	s.joinGroup(group);
    	}catch(Exception e){System.out.println("Constructor Exception!");}
	}
	
	// Compute destination of the paddle
	private void computeDestinationX (Player player) {
		int base;
		if (ball_x_speed > 0)
			player.destination = ball_y + (getWidth() - PADDING - WIDTH - RADIUS - ball_x) * (int)((ball_y_speed) /(ball_x_speed));
		else if(ball_x_speed != 0)
			player.destination = ball_y - (ball_x - PADDING - WIDTH - RADIUS) * (int)((ball_y_speed) / (ball_x_speed));
		else 
			player.destination = ball_y;
		
		if (player.destination <=HEIGHT+TRIANGLE_SIZE +PADDING)
			//player.destination = 2 * PADDING - player.destination;
			player.destination = HEIGHT+TRIANGLE_SIZE +PADDING;
		
		if (player.destination >= getHeight() - HEIGHT- TRIANGLE_SIZE - PADDING) {
			player.destination -= RADIUS;
			if ((player.destination / (getHeight() - 2 * RADIUS)) % 2 == 0)
				player.destination = player.destination % (getHeight () - 2 * RADIUS);
			else
				player.destination = getHeight() - 2 * RADIUS - player.destination % (getHeight () - 2 * RADIUS);
			player.destination += RADIUS;
		}
	}
	//compute destination of y oriented paddles
	private void computeDestinationY (Player player) {
		int base;
		if (ball_y_speed > 0)
			player.destination = ball_x + (getHeight() - PADDING - WIDTH - RADIUS - ball_y) * (int)((ball_x_speed) / (ball_y_speed));
		else if(ball_y_speed != 0)
			player.destination = ball_x - (ball_y - PADDING - WIDTH - RADIUS) * (int)((ball_x_speed) /(ball_y_speed));
		else
			player.destination = ball_x ;

		if (player.destination <= HEIGHT+TRIANGLE_SIZE+PADDING)
			player.destination =  HEIGHT+TRIANGLE_SIZE +PADDING;

		if (player.destination > getWidth() -HEIGHT+ TRIANGLE_SIZE+ PADDING) {
			player.destination -= RADIUS;
			if ((player.destination / (getWidth() - 2 * RADIUS)) % 2 == 0)
				player.destination = player.destination % (getWidth () - 2 * RADIUS);
			else
				player.destination = getWidth() - 2 * RADIUS - player.destination % (getWidth () - 2 * RADIUS);
			player.destination += RADIUS;
		}
	}
	
	// Set new position of the player
	private void movePlayer (Player player, int destination,boolean width) {
		int distance = Math.abs (player.position - destination);
		int max_destination;
		if(width)
			max_destination=getWidth();
		else
			max_destination=getHeight();
		if (distance != 0) {
			int direction = - (player.position - destination) / distance;
			
			if (distance > SPEED)
				distance = SPEED;
			
			player.position += direction * distance;
			//int height= HEIGHT+10*player.getHeight();
			int height=HEIGHT;
			if((player.equals(player1) && isExtended[0]) || (player.equals(player2) && isExtended[1]) || (player.equals(player3) && isExtended[2]) || (player.equals(player4) && isExtended[3]))
				height=HEIGHT+30;
			//System.out.println("Move height "+height);
			if (player.position - height- TRIANGLE_SIZE - PADDING < 0)
				player.position = height+ TRIANGLE_SIZE +PADDING;
			if (player.position + height+ TRIANGLE_SIZE +PADDING > max_destination)
				player.position = max_destination - height- TRIANGLE_SIZE - PADDING;
		}
	}
	
	// Compute player position 
	private void computePosition (Player player,boolean width, String playerNum) {

		// MOUSE
		if (player.getType() == Player.MOUSE) {
			if (mouse_inside) {
				int cursor = getMousePosition().y;
				movePlayer (player, cursor,width);
			}
		}
		// KEYBOARD 
		else if (player.getType() == Player.KEYBOARD) {
			if(playerNum.equals("p1") || playerNum.equals("p2"))
			{
				if (key_up && !key_down) {
					movePlayer (player, player.position - SPEED,width);
				}
				else if (key_down && !key_up) {
					movePlayer (player, player.position + SPEED,width);
				}
			}
			else
			{
				if (key_left && !key_right) {
				movePlayer (player, player.position - SPEED,width);
				}
				else if (key_right && !key_left) {
				movePlayer (player, player.position + SPEED,width);
				}
			}
			
		}

			/*try{
			MulticastSocket s = new MulticastSocket(1234);
            InetAddress group = InetAddress.getByName("228.6.7.8");                     
            s.setReuseAddress(true);             
            s.setLoopbackMode(false);                  
            s.setTimeToLive(2);                  
            s.joinGroup(group);
            String data = Integer.toString(player.position);
            DatagramPacket dp = new DatagramPacket(data.getBytes(), data.length(),group, 1234);
            s.send(dp);
            //System.out.println("Packet sent!!");
      		}
      		catch(Exception e){System.out.println("exceptionnnnn!");}*/
		//UDP
		/*else if (player.getType() == Player.UDP) {
			movePlayer(player,Integer.parseInt(udpPosition));
		}*/
		// CPU HARD
		else if (player.getType() == Player.CPU_HARD_X || player.getType() == Player.CPU_HARD_Y) {
			movePlayer (player, player.destination,width);
		}
		// CPU EASY
		else if (player.getType() == Player.CPU_EASY_X || player.getType() == Player.CPU_EASY_Y) {
			movePlayer (player, ball_y,width);
		}
	}

	public int getBallPos(String value)
	{
		return Integer.parseInt(value);
	}
	//set player to AI
	public void setPlayerToAI(String playerNo)
	{
		if(playerNo.equals("p1"))
		{
			player1.setType(Player.CPU_HARD_X);
		}
		else if(playerNo.equals("p2"))
		{
			player2.setType(Player.CPU_HARD_X);
		}
		else if(playerNo.equals("p3"))
		{
			player3.setType(Player.CPU_HARD_Y);
		}
		else if(playerNo.equals("p4"))
		{
			player4.setType(Player.CPU_HARD_Y);
		}
	}
	// return the position of player
	public int getPosition(String playerNo)
	{
		if(playerNo.equals("p1"))
			return player1.position;
		else if(playerNo.equals("p2"))
			return player2.position;
		else if(playerNo.equals("p3"))
			return player3.position;
		else
			return player4.position;
	}
	// returns the number of spring hits left of a player
	public int getPlayerSprintHits(String playerNo)
	{
		if(playerNo.equals("p1"))
			return p1SpringHitsLeft;
		else if(playerNo.equals("p2"))
			return p2SpringHitsLeft;
		else if(playerNo.equals("p3"))
			return p3SpringHitsLeft;
		else
			return p4SpringHitsLeft;
	}
	// returns the number of a player
	public int getPlayerNum(String playerNo)
	{
		if(playerNo.equals("p1"))
			return 0;
		else if(playerNo.equals("p2"))
			return 1;
		else if(playerNo.equals("p3"))
			return 2;
		else
			return 3;
	}
	
	// Draw
	public void paintComponent (Graphics g) {
		
		Toolkit.getDefaultToolkit().sync();
		acceleration=true;
		super.paintComponent (g);
		g.drawImage(backgroundImage, 0, 0, this);
		
	
		if (new_game) {
			ball_x = getWidth () / 2;
			ball_y = getHeight () / 2;
			
			if(playerNum.equals(currentServer))
			{
				double phase = Math.random () * Math.PI / 2 - Math.PI / 4;
				while((Math.abs(phase)<0.2) || (Math.abs(Math.abs(phase)-Math.PI/2)<0.2))
					phase = Math.random () * Math.PI / 2 - Math.PI / 4; 
				
				ball_x_speed = (int)(Math.cos (phase) * START_SPEED);
				ball_y_speed = (int)(Math.sin (phase) * START_SPEED);			
			}
			else
			{
				ball_x = Integer.parseInt(externalBallX);
				ball_y = Integer.parseInt(externalBallY);
				ball_x_speed = Double.parseDouble(externalBallSpeedX);
				ball_y_speed = Double.parseDouble(externalBallSpeedY);
			}
			
			ball_acceleration_count = 0;
			
			if (player1.getType() == Player.CPU_HARD_X || player1.getType() == Player.CPU_EASY_X) {
				player1.position = getHeight () / 2;
				computeDestinationX (player1);
			}
			if (player2.getType() == Player.CPU_HARD_X || player2.getType() == Player.CPU_EASY_X) {
				player2.position = getHeight () / 2;
				computeDestinationX (player2);
			}
			if (player3.getType() == Player.CPU_HARD_Y || player3.getType() == Player.CPU_EASY_Y) {
				player3.position = getWidth () / 2;
				computeDestinationY (player3);
			}
			if (player4.getType() == Player.CPU_HARD_Y || player4.getType() == Player.CPU_EASY_Y) {
				player4.position = getWidth () / 2;
				computeDestinationY (player4);
			}
			
			new_game = false;
		}

		if(player1.getType() == Player.UDP)
		{
			movePlayer(player1,Integer.parseInt(udpPosition[0]),false);
		}
		if(player2.getType() == Player.UDP)
		{
			movePlayer(player2,Integer.parseInt(udpPosition[1]),false);
		}
		if(player3.getType() == Player.UDP)
		{
			movePlayer(player3,Integer.parseInt(udpPosition[2]),true);
		}
		if(player4.getType() == Player.UDP)
		{
			movePlayer(player4,Integer.parseInt(udpPosition[3]),true);
		}

		//ball_x = (ball_x + Integer.parseInt(externalBallX))/2;
		//ball_y = (ball_y + Integer.parseInt(externalBallY))/2;
		//ball_x_speed = (ball_x_speed + Double.parseDouble(externalBallSpeedX))/2;
		//ball_y_speed = (ball_y_speed + Double.parseDouble(externalBallSpeedY))/2;
		
		
		if (player1.getType() == Player.MOUSE || player1.getType() == Player.KEYBOARD || ball_x_speed < 0)
			computePosition (player1,false,playerNum);
		
	
		if (player2.getType() == Player.MOUSE || player2.getType() == Player.KEYBOARD || ball_x_speed > 0)
			computePosition (player2,false,playerNum);

		if (player3.getType() == Player.MOUSE || player3.getType() == Player.KEYBOARD || ball_y_speed < 0)
			computePosition (player3,true,playerNum);
		
		if (player4.getType() == Player.MOUSE || player4.getType() == Player.KEYBOARD || ball_y_speed > 0)
			computePosition (player4,true,playerNum);

		
		
		if(playerNum.equals(currentServer))
		{
			ball_x += ball_x_speed;
			ball_y += ball_y_speed;
		}
		else
		{
			ball_x = Integer.parseInt(externalBallX);
			ball_y = Integer.parseInt(externalBallY);
			ball_x_speed = Double.parseDouble(externalBallSpeedX);
			ball_y_speed = Double.parseDouble(externalBallSpeedY);
			//ball_x += ball_x_speed;
			//ball_y += ball_y_speed;
		}

		//if there is a spring hit
		if(playerNum.equals(currentServer))
		{  
			if(p1SpringHitsLeft>springHitsLeft[0] && p1SpringHitsLeft>0)
			{
				System.out.println("!!!!!!!!!!!!!!!!!!!!!!!      SPRING HIT     !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				ball_x_speed*=1.5;
				ball_y_speed*=1.5;
				p1SpringHitsLeft--;
			}
			if(p2SpringHitsLeft>springHitsLeft[1] && p2SpringHitsLeft>0)
			{
				ball_x_speed*=1.5;
				ball_y_speed*=1.5;
				p2SpringHitsLeft--;
			}
			if(p3SpringHitsLeft>springHitsLeft[2] && p3SpringHitsLeft>0)
			{
				ball_x_speed*=1.5;
				ball_y_speed*=1.5;
				p3SpringHitsLeft--;
			}
			if(p4SpringHitsLeft>springHitsLeft[3] && p4SpringHitsLeft>0)
			{
				ball_x_speed*=1.5;
				ball_y_speed*=1.5;
				p4SpringHitsLeft--;
			}
		}
		else
		{
			if(p1SpringHitsLeft>springHitsLeft[0] && p1SpringHitsLeft>0)
			{
				p1SpringHitsLeft--;
			}
			if(p2SpringHitsLeft>springHitsLeft[1] && p2SpringHitsLeft>0)
			{
				p2SpringHitsLeft--;
			}
			if(p3SpringHitsLeft>springHitsLeft[2] && p3SpringHitsLeft>0)
			{
				p3SpringHitsLeft--;
			}
			if(p4SpringHitsLeft>springHitsLeft[3] && p4SpringHitsLeft>0)
			{
				p4SpringHitsLeft--;
			}
		}

		//paddle size;
		if(paddle_size){
				numberOfframes++;
		}

		isExtended[getPlayerNum(playerNum)] = (paddle_size && (numberOfframes<=maxNumberOfFrames));

		//accleration of the ball is handled here
			if (acceleration) {
			ball_acceleration_count ++;
			if (ball_acceleration_count == ACCELERATION) {
				if (Math.hypot (ball_x_speed, ball_y_speed) >14 ) {acceleration=false;}
				else {
				ball_x_speed = ball_x_speed + (int)ball_x_speed / Math.hypot ((int)ball_x_speed, (int)ball_y_speed) * 2;
				ball_y_speed = ball_y_speed + (int)ball_y_speed / Math.hypot ((int)ball_x_speed, (int)ball_y_speed) * 2;
				ball_acceleration_count = 0;}
			}
		}
		try{
			String data = "";
            data = data.concat(playerNum);
            data = data.concat(" ");
            String ballX = Integer.toString(ball_x);
            data = data.concat(ballX);
            data = data.concat(" ");
            String ballY = Integer.toString(ball_y);
            data = data.concat(ballY);
            data = data.concat(" ");
            String ballSpeedX = Double.toString(ball_x_speed);
            data = data.concat(ballSpeedX);
            data = data.concat(" ");
            String ballSpeedY = Double.toString(ball_y_speed);
            data = data.concat(ballSpeedY);
            data = data.concat(" ");
            String paddlePos = Integer.toString(getPosition(playerNum));
            data = data.concat(paddlePos);
            data = data.concat(" ");
            String points1 = Integer.toString(player1.points);
            data = data.concat(points1);
            data = data.concat(" ");
            String points2 = Integer.toString(player2.points);
            data = data.concat(points2);
            data = data.concat(" ");
            String points3 = Integer.toString(player3.points);
            data = data.concat(points3);
            data = data.concat(" ");
            String points4 = Integer.toString(player4.points);
            data = data.concat(points4);
            data = data.concat(" ");
            String spHitsLeft = Integer.toString(getPlayerSprintHits(playerNum));
            data = data.concat(spHitsLeft);
            data = data.concat(" ");
            String isExt = Boolean.toString(isExtended[getPlayerNum(playerNum)]);
            data = data.concat(isExt);
            DatagramPacket dp = new DatagramPacket(data.getBytes(), data.length(),group, 1234);
            s.send(dp);
            
      		}
      		catch(Exception e){System.out.println("exceptionnnnn!");}

      		//for corner cases Bottom LEFT
			if((ball_y- ball_x - getHeight()+ TRIANGLE_SIZE+14.2) >=0){
			double x_temp,y_temp;
			x_temp= (ball_x_speed+ball_y_speed+ Math.abs(ball_x_speed- ball_y_speed))/2;
			y_temp = (ball_x_speed+ball_y_speed- Math.abs(ball_x_speed- ball_y_speed))/2;
			ball_x_speed=x_temp;
			ball_y_speed = y_temp;

			if (player1.getType() == Player.CPU_HARD_X)
					computeDestinationX (player1);
			if (player2.getType() == Player.CPU_HARD_X)
					computeDestinationX (player2);
			if (player3.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player3);
			if (player4.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player4);
		}
		//corner case TOP RIGHT
		else if((ball_y-ball_x+(getWidth()-TRIANGLE_SIZE-14.2))<=0){
			double x_temp,y_temp;
			x_temp= (ball_x_speed+ball_y_speed- Math.abs(ball_x_speed- ball_y_speed))/2;
			y_temp = (ball_x_speed+ball_y_speed+ Math.abs(ball_x_speed- ball_y_speed))/2;
			ball_x_speed=x_temp;
			ball_y_speed = y_temp;
			if (player1.getType() == Player.CPU_HARD_X)
					computeDestinationX (player1);
			if (player2.getType() == Player.CPU_HARD_X)
					computeDestinationX (player2);
			if (player3.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player3);
			if (player4.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player4);
		}
		// corner case Top Left
		else if ((ball_x+ball_y- TRIANGLE_SIZE-14.2) <=0) {
			double x_temp,y_temp;
			x_temp = ((ball_x_speed- ball_y_speed)+Math.abs(ball_x_speed+ ball_y_speed))/2;
			y_temp = (-1*(ball_x_speed- ball_y_speed)+Math.abs(ball_x_speed+ ball_y_speed))/2;
			ball_x_speed=x_temp;
			ball_y_speed = y_temp;
			if (player1.getType() == Player.CPU_HARD_X)
					computeDestinationX (player1);
			if (player2.getType() == Player.CPU_HARD_X)
					computeDestinationX (player2);
			if (player3.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player3);
			if (player4.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player4);
		} 
		//corner case  Bottom Right
		else if((ball_y+ball_x-(getWidth()+getHeight()-TRIANGLE_SIZE-14.2))>=0){
			double x_temp,y_temp;
			x_temp = ((ball_x_speed- ball_y_speed)- Math.abs(ball_x_speed+ ball_y_speed))/2;
			y_temp = ((ball_y_speed- ball_x_speed)-Math.abs(ball_x_speed+ ball_y_speed))/2;
			ball_x_speed=x_temp;
			ball_y_speed = y_temp;
			if (player1.getType() == Player.CPU_HARD_X)
					computeDestinationX (player1);
			if (player2.getType() == Player.CPU_HARD_X)
					computeDestinationX (player2);
			if (player3.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player3);
			if (player4.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player4);
		} 
		
		// Border-collision LEFT
		else if ((ball_x <= PADDING + WIDTH + RADIUS)){
			int collision_point;
			if(ball_x_speed!=0)
			collision_point = ball_y + (int)(ball_y_speed / ball_x_speed * (PADDING + WIDTH + RADIUS - ball_x));
			else 
			 collision_point = ball_y;

			int heightA;
			if(isExtended[0]){
				heightA=HEIGHT+30;
			}
			else
				heightA=HEIGHT;
			//collision with paddle of player1
			if (collision_point > player1.position - heightA - TOLERANCE && 
			    collision_point < player1.position + heightA + TOLERANCE) {
				ball_x = 2 * (PADDING + WIDTH + RADIUS) - ball_x;
				ball_x_speed = Math.abs (ball_x_speed);

				if(Math.abs(ball_x_speed)>0.3)
				{
				ball_y_speed -= Math.sin ((double)(player1.position - ball_y) / HEIGHT * Math.PI / 4)
				                * Math.hypot (ball_x_speed, ball_y_speed) * 0.01;}


				if(springHit && playerNum.equals("p1"))
				{
					ball_x_speed*=SPRING_STRENGTH;
					ball_y_speed*=SPRING_STRENGTH;
					p1SpringHitsLeft--;
				}
				if (player2.getType() == Player.CPU_HARD_X)
					computeDestinationX (player2);
				if (player3.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player3);
				if (player4.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player4);
			}
			//side ways collisiion of ball with player 1
			else if(ball_x<PADDING+RADIUS+WIDTH && (((ball_y+RADIUS>=player1.position-HEIGHT) && (ball_y+RADIUS <= player1.position-HEIGHT+RADIUS)) || ((ball_y - RADIUS <=player1.position+HEIGHT) && (ball_y-RADIUS >= player1.position+HEIGHT- RADIUS ))) ){
				ball_y_speed = -1*ball_y_speed;
				if(springHit)
					START_SPEED+=50; 
				if (player2.getType() == Player.CPU_HARD_X)
					computeDestinationX (player2);
				if (player3.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player3);
				if (player4.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player4);
			}
			else if((ball_x <= PADDING + RADIUS)){
				player1.points ++;
				ball_x_speed = Math.abs (ball_x_speed);  
				if(springHit)
					START_SPEED+=50;           //To reflect the ball appropriately
				if (player2.getType() == Player.CPU_HARD_X)
					computeDestinationX (player2);
				if (player3.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player3);
				if (player4.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player4);

			}
		}
		
		// Border-collision RIGHT
		
		 else if ((ball_x >= getWidth() - PADDING - WIDTH - RADIUS)){
			int collision_point;
			if(ball_x_speed!=0)
			collision_point = ball_y - (int)(ball_y_speed / ball_x_speed * (ball_x - getWidth() + PADDING + WIDTH + RADIUS));
			else
			collision_point = ball_y;
			int heightB;
			// special powert to increase the height of paddle
			if(isExtended[1]){
				heightB=HEIGHT+30;
			}
			else
				heightB=HEIGHT;
			//collision with paddle of player2
			if (collision_point > player2.position - heightB - TOLERANCE && 
			    collision_point < player2.position + heightB + TOLERANCE) {
				ball_x = 2 * (getWidth() - PADDING - WIDTH - RADIUS ) - ball_x;
				ball_x_speed = -1 * Math.abs (ball_x_speed);

				if(Math.abs(ball_x_speed)>0.3)
				{
					ball_y_speed -= Math.sin ((double)(player2.position - ball_y) / HEIGHT * Math.PI / 4)          
				                * Math.hypot (ball_x_speed, ball_y_speed) * 0.01;}
				else
					ball_y_speed=-1*ball_y_speed;

		
				ball_y_speed -= Math.sin ((double)(player2.position - ball_y) / HEIGHT * Math.PI / 4)          
				                * Math.hypot (ball_x_speed, ball_y_speed) * 0.01;

				if (player1.getType() == Player.CPU_HARD_X)
					computeDestinationX (player1);
				if (player3.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player3);
				if (player4.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player4);
				//special power spring hit
				if(springHit && playerNum.equals("p2"))
				{
					ball_x_speed*=SPRING_STRENGTH;
					ball_y_speed*=SPRING_STRENGTH;
					p2SpringHitsLeft--;
				}
			}
			//sideways collision of ball with player 2
			else if(ball_x> getWidth()-(PADDING+RADIUS+WIDTH) && (((ball_y+RADIUS>=player2.position-HEIGHT) && (ball_y+RADIUS <= player2.position-HEIGHT+RADIUS)) || ((ball_y - RADIUS <=player2.position+HEIGHT) && (ball_y-RADIUS >= player2.position+HEIGHT-RADIUS))) ){
				ball_y_speed = -1*ball_y_speed;
				if (player1.getType() == Player.CPU_HARD_X)
					computeDestinationX (player1);
				if (player3.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player3);
				if (player4.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player4);
			}
			else if(ball_x >= getWidth() - PADDING - RADIUS) {
				player2.points ++;
				ball_x_speed = -1 * Math.abs (ball_x_speed);          //To reflect the ball appropriately
				if (player1.getType() == Player.CPU_HARD_X)
					computeDestinationX (player1);
				if (player3.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player3);
				if (player4.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player4);
			}
		}
		
		//Border collision BOTTOM
		else if ((ball_y <= PADDING + WIDTH + RADIUS)){
			int collision_point;
			if(ball_y_speed!=0)
				 collision_point = ball_x + (int)(ball_x_speed / ball_y_speed * (PADDING + WIDTH + RADIUS - ball_y));
			else
				 collision_point = ball_x;
			int heightC;
			if(isExtended[2]){
				heightC=HEIGHT+30;
			}
			else
				heightC=HEIGHT;
			// collision of ball with paddle of player3
			if (collision_point > player3.position - heightC - TOLERANCE && 
			    collision_point < player3.position + heightC + TOLERANCE) {
				ball_y = 2 * (PADDING + WIDTH + RADIUS) - ball_y;
				ball_y_speed = Math.abs (ball_y_speed);
				if(Math.abs(ball_y_speed)>0.3)
				{
					
					ball_x_speed -= Math.sin ((double)(player3.position - ball_x) / HEIGHT * Math.PI / 4)
				                * Math.hypot (ball_x_speed, ball_y_speed) * 0.01;}
				else

				if(!((ball_y < PADDING + WIDTH + RADIUS) && (ball_y>= PADDING + RADIUS)) && ball_y_speed>0.5){
			
				ball_x_speed -= Math.sin ((double)(player3.position - ball_x) / HEIGHT * Math.PI / 4)
				                * Math.hypot (ball_x_speed, ball_y_speed) * 0.01;
				}else

					ball_x_speed= -1*ball_x_speed;

				if (player4.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player4);
				if (player2.getType() == Player.CPU_HARD_X)
					computeDestinationX (player2);
				if (player1.getType() == Player.CPU_HARD_X)
					computeDestinationX (player1);
				//special power spring hit
				if(springHit && playerNum.equals("p3"))
				{
					ball_x_speed*=SPRING_STRENGTH;
					ball_y_speed*=SPRING_STRENGTH;
					p3SpringHitsLeft--;
				}
			}
			//sideways collision of ball with paddle of player3
			else if(ball_y < (PADDING+RADIUS+WIDTH) && (((ball_x+RADIUS>=player3.position-HEIGHT) && (ball_x+RADIUS <= player3.position-HEIGHT+RADIUS)) || ((ball_x - RADIUS <=player3.position+HEIGHT) && (ball_x - RADIUS >= player3.position+ HEIGHT- RADIUS))) ){
	
				ball_x_speed = -1*ball_x_speed;
				//ball_y_speed= 0;
				if (player4.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player4);
				if (player2.getType() == Player.CPU_HARD_X)
					computeDestinationX (player2);
				if (player1.getType() == Player.CPU_HARD_X)
					computeDestinationX (player1);
			}
			else if(ball_y <= PADDING + RADIUS) {
				player3.points ++;
				ball_y_speed = Math.abs (ball_y_speed);             //To reflect the ball appropriately
				if (player4.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player4);
				if (player2.getType() == Player.CPU_HARD_X)
					computeDestinationX (player2);
				if (player1.getType() == Player.CPU_HARD_X)
					computeDestinationX (player1);
			}
		}
		
		// Border-collision TOP
		else if ((ball_y >= getHeight() - PADDING - WIDTH - RADIUS)){
			int collision_point;
			if(ball_y_speed!=0)
			 collision_point = ball_x - (int)(ball_x_speed / ball_y_speed * (ball_y - getHeight() + PADDING + WIDTH + RADIUS));
			else
			 collision_point = ball_x;
			int heightD;
			if(isExtended[2]){
				heightD=HEIGHT+30;
			}
			else
				heightD=HEIGHT;
			int height;
			//collision from paddle of player4
			if (collision_point > player4.position - heightD - TOLERANCE && 
			    collision_point < player4.position + heightD + TOLERANCE) {
				ball_y = 2 * (getHeight() - PADDING - WIDTH - RADIUS ) - ball_y;
				ball_y_speed = -1 * Math.abs (ball_y_speed);
				//for grazing collison

				if(Math.abs(ball_y_speed)>0.3)
				{
					
				ball_x_speed -= Math.sin ((double)(player4.position - ball_x) / HEIGHT * Math.PI / 4)
				                * Math.hypot (ball_x_speed, ball_y_speed) * 0.01;}
				else
					ball_x_speed= -1*ball_x_speed;

				ball_x_speed -= Math.sin ((double)(player4.position - ball_x) / HEIGHT * Math.PI / 4)
				                * Math.hypot (ball_x_speed, ball_y_speed) * 0.01;
				
				if (player3.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player3);
				if (player2.getType() == Player.CPU_HARD_X)
					computeDestinationX (player2);
				if (player1.getType() == Player.CPU_HARD_X)
					computeDestinationX (player1);

				if(springHit && playerNum.equals("p4"))
				{
					ball_x_speed*=SPRING_STRENGTH;
					ball_y_speed*=SPRING_STRENGTH;
					p4SpringHitsLeft--;
				}
			}
			// sideways collsion of ball with paddle of player4
			else if(ball_y < getHeight()-(PADDING+RADIUS+WIDTH) && (((ball_x+RADIUS>=player4.position-HEIGHT) && (ball_x+RADIUS <= player4.position-HEIGHT+RADIUS)) || ((ball_x - RADIUS <=player4.position+HEIGHT) && (ball_x - RADIUS >= player4.position+HEIGHT- RADIUS))) ){
				ball_x_speed = -1*ball_x_speed;
				if (player3.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player3);
				if (player2.getType() == Player.CPU_HARD_X)
					computeDestinationX (player2);
				if (player1.getType() == Player.CPU_HARD_X)
					computeDestinationX (player1);
			}
			else if(ball_y >= getHeight() - PADDING - RADIUS){
				player4.points ++;
				ball_y_speed = -1 * Math.abs (ball_y_speed);          //To reflect the ball appropriately
				if (player3.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player3);
				if (player2.getType() == Player.CPU_HARD_X)
					computeDestinationX (player2);
				if (player1.getType() == Player.CPU_HARD_X)
					computeDestinationX (player1);
			}
		}

		else{
				if (player1.getType() == Player.CPU_HARD_X)
					computeDestinationX (player1);
				if (player2.getType() == Player.CPU_HARD_X)
					computeDestinationX (player2);
				if (player3.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player3);
				if (player4.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player4);
		}

	
		//drawing trianles at corner of board
		triangleShape1 = new TriangleShape(
                    new Point2D.Double(0, 0),
                    new Point2D.Double(TRIANGLE_SIZE,0),
                    new Point2D.Double(0, TRIANGLE_SIZE)
            );

    	 triangleShape2 = new TriangleShape(
                    new Point2D.Double(getWidth()-TRIANGLE_SIZE, 0),
                    new Point2D.Double(getWidth(), 0),
                    new Point2D.Double(getWidth(), TRIANGLE_SIZE)
            );
    	 triangleShape3 = new TriangleShape(
                    new Point2D.Double(0,getHeight()),
                    new Point2D.Double(0, getHeight()-TRIANGLE_SIZE),
                    new Point2D.Double(TRIANGLE_SIZE,getHeight())
            );
    	 triangleShape4 = new TriangleShape(
                    new Point2D.Double(getWidth(), getHeight()),
                    new Point2D.Double(getWidth()-TRIANGLE_SIZE, getHeight()),
                    new Point2D.Double(getWidth(), getHeight()-TRIANGLE_SIZE)
            );
		g.setColor (new Color(45,92,239));
		//during spring hit color of the paddle is changed
		if(playerNum.equals("p1") && springHit && p1SpringHitsLeft>0)
			g.setColor (new Color(255,255,0));
		
		int height1=HEIGHT;
		if(isExtended[0]){
			height1=HEIGHT+30;
		}
		Graphics2D g2d = (Graphics2D) g.create();
		
		//making paddles of players
		g.fillRect (PADDING, player1.position - height1, WIDTH, (height1) * 2);
		g.setColor (new Color(45,92,239));
		if(playerNum.equals("p2") && springHit && p2SpringHitsLeft>0)
			g.setColor (new Color(255,255,0));
		int height2=HEIGHT;
		if(isExtended[1]){
			height2=HEIGHT+30;
		}

		g.fillRect (getWidth() - PADDING - WIDTH, player2.position - height2, WIDTH, (height2) * 2);
		g.setColor (new Color(45,92,239));

		int height3 = HEIGHT;
		if(isExtended[2]){
			height3=HEIGHT+30;
		}

		if(playerNum.equals("p3") && springHit && p3SpringHitsLeft>0)
			g.setColor (new Color(255,255,0));
		g.fillRect (player3.position - height3, PADDING, (height3)*2, WIDTH);
		g.setColor (new Color(45,92,239));
		int height4 = HEIGHT;
		if(isExtended[3]){
			height4=HEIGHT+30;
		}
		if(playerNum.equals("p4") && springHit && p4SpringHitsLeft>0)
			g.setColor (new Color(255,255,0));
		g.fillRect (player4.position - height4, getHeight() - PADDING - WIDTH, (height4)*2, WIDTH);
		g.setColor (new Color(45,92,239));

		g.setColor (Color.WHITE);
		g.fillOval (ball_x - RADIUS, ball_y - RADIUS, RADIUS*2, RADIUS*2);
		

		
            g2d.setColor (new Color(192,192,192));
            g2d.fill(triangleShape1);
            g2d.fill(triangleShape2);
            g2d.fill(triangleShape3);
            g2d.fill(triangleShape4);
            g2d.dispose();
        g.setColor(Color.WHITE);
        //displaying the scores of players and checking whether the player has lost all its misses
        if(!playerNum.equals(currentServer))
        {
			player1.points = scores[0];
			player2.points = scores[1];
			player3.points = scores[2];
			player4.points = scores[3];	
		}

		if((player1.getType() == Player.KEYBOARD || player1.getType() == Player.UDP) && (player1.points>=MAX_POINTS))
		{
			player1.setType(Player.CPU_HARD_X);
			player1.hasLost = true;
		}
		if(player2.points>=MAX_POINTS)
		{
			player2.setType(Player.CPU_HARD_X);
			player2.hasLost = true;
		}
		if(player3.points>=MAX_POINTS)
		{
			player3.setType(Player.CPU_HARD_Y);
			player3.hasLost = true;
		}
		if(player4.points>=MAX_POINTS)
		{
			player4.setType(Player.CPU_HARD_Y);
			player4.hasLost = true;
		}


		if(player1.getType() == Player.KEYBOARD || player1.getType() == Player.UDP)
			g.drawString (player1.points+" ", 50, getHeight()/2);
		if(player2.getType() == Player.KEYBOARD || player2.getType() == Player.UDP)
			g.drawString (player2.points+" ", getWidth() - 50, getHeight()/2);
		if(player3.getType() == Player.KEYBOARD || player3.getType() == Player.UDP)
			g.drawString(player3.points+" ", getWidth()/2, 50);
		if(player4.getType() == Player.KEYBOARD || player4.getType() == Player.UDP)
			g.drawString(player4.points+" ", getWidth()/2, getHeight() - 50);

		 //indicates whether the player is out or not
		if(player1.hasLost && popUpTimeLeft[0]>0)
		{
			popUpTimeLeft[0]--;
			if(playerNum.equals("p1"))
				g.drawString("You are OUT", 25, getHeight()/2);
			else
				g.drawString("Player 1 is OUT", 25, getHeight()/2);
		}
		if(player2.hasLost && popUpTimeLeft[1]>0)
		{
			popUpTimeLeft[1]--;
			if(playerNum.equals("p2"))
				g.drawString("You are OUT", getWidth() - 150, getHeight()/2);
			else
				g.drawString("Player 2 is OUT", getWidth() - 150, getHeight()/2);
		}
		if(player3.hasLost && popUpTimeLeft[2]>0)
		{
			popUpTimeLeft[2]--;
			if(playerNum.equals("p3"))
				g.drawString("You are OUT", getWidth()/2, 50);
			else
				g.drawString("Player 3 is OUT", getWidth()/2, 50);
		}
		if(player4.hasLost && popUpTimeLeft[3]>0)
		{
			popUpTimeLeft[3]--;
			if(playerNum.equals("p4"))
				g.drawString("You are OUT", getWidth()/2, getHeight() - 50);
			else
				g.drawString("Player 4 is OUT", getWidth()/2, getHeight() - 50);
		}
	}
	//class to draw triangle
	public class TriangleShape extends Path2D.Double {

        public TriangleShape(Point2D... points) {
            moveTo(points[0].getX(), points[0].getY());
            lineTo(points[1].getX(), points[1].getY());
            lineTo(points[2].getX(), points[2].getY());
            closePath();
        }

    }
    @Override
      public Dimension getPreferredSize() {
         return new Dimension(PREF_W, PREF_H);
      }
	// New frame
	public void actionPerformed (ActionEvent e) {
		repaint ();
	}
	
	// Mouse inside
	public void mouseEntered (MouseEvent e) {
		mouse_inside = true;
	}
	
	// Mouse outside
	public void mouseExited (MouseEvent e) {
		mouse_inside = false;
	}
	
	// Mouse pressed
	public void mousePressed (MouseEvent e) {}
	
	// Mouse released
	public void mouseReleased (MouseEvent e) {}
		
	// Mouse clicked
	public void mouseClicked (MouseEvent e) {}
	
	// Key pressed
	public void keyPressed (KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP)
			key_up = true;
		else if (e.getKeyCode() == KeyEvent.VK_DOWN)
			key_down = true;
		else if (e.getKeyCode() == KeyEvent.VK_LEFT)
			key_left = true;
		else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
			key_right = true;
		else if (e.getKeyCode() == KeyEvent.VK_SPACE && numberOfSpringHits>0)
			springHit = true;
		else if(e.getKeyCode()== KeyEvent.VK_ENTER){
			paddle_size= true;
		}
	}
	
	// Key released
	public void keyReleased (KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP)
			key_up = false;
		else if (e.getKeyCode() == KeyEvent.VK_DOWN)
			key_down = false;
		else if (e.getKeyCode() == KeyEvent.VK_LEFT)
			key_left = false;
		else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
			key_right = false;
		else if (e.getKeyCode() == KeyEvent.VK_SPACE)
			springHit = false;
		else if(e.getKeyCode()== KeyEvent.VK_ENTER)
			paddle_size= false;
	}
	
	// Key released
	public void keyTyped (KeyEvent e) {}
}
