import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
 import javax.swing.JFrame;
import java.net.*;
import javax.imageio.ImageIO;
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
                    //chacking whether the player is alive or not and if he was server then shift the server to next player
                     byte[] receiveData = new byte[1024];
                     DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                     msocket.receive(receivePacket);
                    
                     String sentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    
                     
                        String[] data = sentence.trim().split(" ");
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

//main Swing class that draws the initial window
public class SwingControlDemo {
    
   private JFrame mainFrame;
   private JLabel headerLabel;
   private JLabel statusLabel;
   private JPanel controlPanel;
    public JTextField textField1;
    public JTextField textField2;
    public String s1;
    public String s2;
    public Image background_image;
   public SwingControlDemo(){
      prepareGUI();
      try{
      background_image = ImageIO.read(new File("final.jpg"));
    }catch(IOException e){System.out.println("Image not found!");}
   }

   public static void main(String[] args) throws Exception{
      SwingControlDemo  swingControlDemo = new SwingControlDemo();      
      swingControlDemo.showButtonDemo();
   }

   private void prepareGUI(){
      mainFrame = new JFrame("Online Ping Pong Game");
      mainFrame.setSize(400,400);
      mainFrame.setLayout(new GridLayout(3, 1));
      //mainFrame.setBackground(Color.RED);
      mainFrame.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent windowEvent){
            System.exit(0);
         }        
      });    
      headerLabel = new JLabel("", JLabel.CENTER);        
      statusLabel = new JLabel("",JLabel.CENTER);    

      statusLabel.setSize(350,100);
      controlPanel = new JPanel();
      controlPanel.setLayout(new FlowLayout());

      mainFrame.add(headerLabel);
      mainFrame.add(controlPanel);
      mainFrame.add(statusLabel);
      mainFrame.setVisible(true);  
   }

   private void showButtonDemo()throws Exception{

      headerLabel.setText("PING PONG ");

       controlPanel.setLayout(new GridBagLayout());
       GridBagConstraints c = new GridBagConstraints();
       c.gridx = 0;
       c.gridy = 0;
       controlPanel.add(new JLabel("Number of players   "), c);
       c.gridx = 1;
      c.gridy = 0;
      textField1 = new JTextField(8);
      textField1.setBounds(5, 5, 80, 50);      
      controlPanel.add(textField1, c);
      c.gridx = 0;
      c.gridy = 1;
      controlPanel.add(new JLabel("Player number   "), c);
      c.gridx = 1;
      c.gridy = 1;
      textField2 = new JTextField(8);
      textField2.setBounds(5, 5, 80, 50);
      controlPanel.add(textField2, c);
      c.gridx = 0;
      c.gridy = 5;
      final JButton button = new JButton("Submit");
       controlPanel.add(new JLabel());
       controlPanel.add(button,c);
       mainFrame.setVisible(true);
       //on clicking submit button game is enabled 
    button.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
           
             try {
               if (e.getSource() == button) {
                  s1 = textField1.getText();
                  s2 = textField2.getText();
               showNewFrame2(s1,s2);
               statusLabel.setText("Submit Button clicked !!");}
             } catch (Exception exp){System.out.println("Exception here!!!!");}
             
             
         }          
      }); 

   }
 // new game window with the given number of players and reaming will be AI
private void showNewFrame2(String s1, String s2) throws Exception {

try{
    String numberOFPlayers = s1;           //initial number of players
        String inp2 = s2;                  // player number which he takes
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
     }
     catch (Exception e) {
            System.out.println("Exception in thread!!");
         }

   
   }


}
