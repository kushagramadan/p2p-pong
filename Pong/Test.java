import javax.swing.JFrame;
import java.net.*;
import java.io.*;

class ChatThread extends Thread {
        
        public MulticastSocket msocket;
        private DatagramPacket recv;
        private PongWindow wind;
        private String playerNum;
        private String currentServer;
        private int numberOFPlayers;            //initial number of players
        
        public ChatThread(MulticastSocket msock ,InetAddress group_no , int Port_no, PongWindow window, String playNum, String currServer, int numPlayers)
       {
                msocket= msock;
                wind = window;
                playerNum = playNum;
                currentServer = currServer;
                numberOFPlayers = numPlayers;
                start();                // start calls run
        }
        
        public void run()
        {
            int countPackets = 0;               //count server packets
            int countPackets1 = 0;
            int countPackets2 = 0;
            int countPackets3 = 0;
            int countPackets4 = 0;
            int delay = 300;
            boolean isAliveServer = true;
            boolean isAlive1 = true, isAlive2 = true, isAlive3 = true, isAlive4 = true;
            if(numberOFPlayers == 1)
            {
                isAlive1 = true;
                isAlive2 = false;
                isAlive3 = false;
                isAlive4 = false;
            }
            if(numberOFPlayers == 2)
            {
                isAlive1 = true;
                isAlive2 = true;
                isAlive3 = false;
                isAlive4 = false;
            }
            if(numberOFPlayers == 3)
            {
                isAlive1 = true;
                isAlive2 = true;
                isAlive3 = true;
                isAlive4 = false;
            }
            if(numberOFPlayers == 4)
            {
                isAlive1 = true;
                isAlive2 = true;
                isAlive3 = true;
                isAlive4 = true;
            }
            try{
                try{
                  while(true)
                  {
                     byte[] receiveData = new byte[1024];
                     DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                     msocket.receive(receivePacket);
                     //System.out.println("yoo!");
                     String sentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
                     //System.out.println("length"+sentence.trim().length());
                     
                        String[] data = sentence.trim().split(" ");
                        //System.out.println(data[0]);
                        if(!data[0].equals(currentServer))
                          countPackets++;
                        else if(data[0].equals(currentServer))
                          countPackets = 0;
                        if(countPackets>delay )        //shift server now
                        {
                            System.out.println("Server has exited!"); 
                            System.out.println("Next server is : "+nextServer(currentServer, isAlive1, isAlive2, isAlive3, isAlive4));
                            wind.changeServer(nextServer(currentServer, isAlive1, isAlive2, isAlive3, isAlive4));
                        }

                        if(!data[0].equals("p1") && isAlive1)
                            countPackets1++;
                        else if(data[0].equals("p1") && isAlive1)
                            countPackets1 = 0;
                        if(countPackets1>delay)
                        {
                            System.out.println("p1 has exited");
                            isAlive1 = false;
                            wind.changePlayerToAI("p1");
                        }

                        if(!data[0].equals("p2") && isAlive2)
                            countPackets2++;
                        else if(data[0].equals("p2") && isAlive2)
                            countPackets2 = 0;
                        if(countPackets2>delay)
                        {
                            System.out.println("p2 has exited");
                            isAlive2 = false;
                            wind.changePlayerToAI("p2");
                        }

                        if(!data[0].equals("p3") && isAlive3)
                            countPackets3++;
                        else if(data[0].equals("p3") && isAlive3)
                            countPackets3 = 0;
                        if(countPackets3>delay)
                        {
                            System.out.println("p3 has exited");
                            isAlive3 = false;
                            wind.changePlayerToAI("p3");
                        }

                        if(!data[0].equals("p4") && isAlive4)
                            countPackets4++;
                        else if(data[0].equals("p4") && isAlive4)
                            countPackets4 = 0;
                        if(countPackets4>delay)
                        {
                            System.out.println("p4 has exited");
                            isAlive4 = false;
                            wind.changePlayerToAI("p4");
                        }


                     
                     wind.setAllPos(sentence.trim());
                     //System.out.println("RECEIVED: " + sentence);
                  }
              } catch (SocketTimeoutException c) {System.out.println("Changing to AI as no packets received!"); wind.changePlayerToAI("p1");}
               } catch (Exception e) {System.out.println("Exception in thread!!");}
        }

        public String nextServer(String currentServer, boolean isAlive1, boolean isAlive2, boolean isAlive3, boolean isAlive4)
        {
            if(currentServer.equals("p1"))
            {
                if(isAlive2)
                    return "p2";
                else if(isAlive3)
                    return "p3";
                else
                    return "p4";
            }
            else if(currentServer.equals("p2"))
            {
                if(isAlive3)
                    return "p3";
                else
                    return "p4";
            }
            else if(currentServer.equals("p3"))
            {
                return "p4";
            }
            else
            {
                return "p4";            //the last player has quit
            }
        }
        
}

public class Test {

	public static void main (String[] args) throws Exception {
        String numberOFPlayers = args[0];           //initial number of players
        String inp2 = args[1];
        String playerNum = "p";
        playerNum = playerNum.concat(inp2);
        String currentServer = "p1";
		PongWindow window = new PongWindow (Integer.parseInt(numberOFPlayers), playerNum, currentServer);
		window.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		window.setVisible (true);
		MulticastSocket s = new MulticastSocket(1234);
        s.setSoTimeout(10000);
        InetAddress group = InetAddress.getByName("228.6.7.8");                     
        s.setReuseAddress(true);             
        s.setLoopbackMode(false);                  
        s.setTimeToLive(2);                  
        s.joinGroup(group);
        new ChatThread(s, group, 1234, window, playerNum, currentServer, Integer.parseInt(numberOFPlayers));
      /*while(true)
                  {
                     byte[] receiveData = new byte[1024];
                     DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                     s.receive(receivePacket);
                     System.out.println("yoo!");
                     String sentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
                     System.out.println("length"+sentence.trim().length());
                     window.setBallPos(sentence.trim());
                     System.out.println("RECEIVED: " + sentence);
                  }*/
	}
}
