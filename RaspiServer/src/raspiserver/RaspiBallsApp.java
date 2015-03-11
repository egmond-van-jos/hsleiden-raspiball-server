package raspiserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;


public class RaspiBallsApp
{
	private final int SIZE = 14;
	private final int DEPTH = 150;
	
	private double timer = 0;
	private double speed = 0.1;
	
	private double[][] zPos;
	
	private ServerSocket serverSocket;
	private PrintWriter writer;
	private BufferedReader reader;
	
	public RaspiBallsApp()
	{	
		List<Raspi> raspiList = null;
		zPos = new double[SIZE][SIZE];
		for( int i = 0; i < zPos.length; i++ )
		{
			for( int j = 0; j < zPos[0].length; j++ )
			{
				zPos[i][j] = 0;
			}
		}

		try
		{
			//start listening to port 5001 (accept() waits until connection is made)
			serverSocket = new ServerSocket( 5001 );
			Socket clientSocket = serverSocket.accept();
			
			//create the writer to write back to the client
			writer = new PrintWriter( clientSocket.getOutputStream() );
	    	System.out.println( "connection ready" );
			
	    	//create the reader to catch incoming input from the client
	    	InputStream is = clientSocket.getInputStream();
		    reader = new BufferedReader(
		            new InputStreamReader( is ));

		    //notify client we're ready
			print( "connection ready, type help for help" );

		    String inputString = reader.readLine();
		    while( inputString.equals("quit") == false )
		    {
		    	//if the list is null, check again for active raspis
		    	if( raspiList == null )
		    	{
		    		raspiList = Raspi.getActiveRaspis();
		    	}
		    	
		    	//print and split incoming input
		    	System.out.println( inputString );
		    	String[] commands = inputString.split(" ");

		    	if( commands[0].equals( "help" ) )
		    	{
		    		print( "Input: help" );
		    		print( "Input: restart" );
		    		print( "Input: command [type] [arguments (optional)]" );
		    	}
		    	
		    	if( commands[0].equals( "restart" ) )
		    	{
		    		raspiList = null;
		    	}
		    	
		    	if( commands[0].equals( "command" ) )
		    	{
		    		doCommand( raspiList, commands );
		    	}
		    	
		    	//patterns not yet implemented
//		    	else if( commands[0].equals("pattern") )
//		    	{
//		    		doPattern( commands );
//		    	}
		    	
		    	inputString = reader.readLine();
		    	
		    	writer.flush();
		    }

		    print( "exitting raspiballs server jar" );
		    
		    reader.close();
		    writer.close();
			serverSocket.close();
		}
		
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void print( String message )
	{
		System.out.println( message );
		writer.println( message );
	}
	
	public void doCommand( List<Raspi> raspiList, String[] commands )
	{
		Command command = null;
				
		if( commands.length == 3 )
		{
			int steps = Integer.parseInt( commands[2] );
			command = new Command( commands[1], steps );
		}
		
		if( commands.length == 2 )
		{
			command = new Command( commands[1] );
		}
		
		if( command == null )
		{
			
		}
		
		else
		{
			for( Raspi raspi : raspiList )
			{
				raspi.queueCommand( command );
			}
		}
	}
	
//	public Pattern doPattern( List<Raspi> raspiList, String[] commands )
//	{
//		Pattern pattern = null;
//		
//		if( commands[1].equals("snake") )
//		{
//			pattern = new Snakes();
//		}
//		
//		if( pattern != null )
//		{
//    		int[][] changes = new int[SIZE][SIZE];
//    		
//    		for( int x = 0; x < zPos.length; x++ )
//    		{
//    			for( int y = zPos[0].length - 1; y >= 0; y-- )
//    			{
//    				double target = DEPTH/2D + DEPTH * Math.max( -1, Math.min( 1, pattern.calculate(DEPTH,SIZE,timer,x,y,(int)zPos[x][y]) ) );
//    				double change = (target - zPos[x][y]); //Math.min( Math.abs(speed), Math.max( -Math.abs(speed), (target-raspi.z)*(DEPTH/800d) ) );
//
//    				double target = currentPattern[0].calc(DEPTH,SIZE,timer*DEPTH*speed/10d,x,y,(int)zPos[x][y]);
//    				double change = Math.min( Math.abs(speed), Math.max( -Math.abs(speed), (target-zPos[x][y])*(DEPTH/800d) ) );
//    				changes[x][y] = (int)Math.signum( (int)(change/10) );
//    			}
//    		}
//
//    		for( int x = 0; x < zPos.length; x++ )
//    		{
//    			for( int y = zPos[0].length - 1; y >= 0; y-- )
//    			{
//    				zPos[x][y] += changes[x][y]*speed;
//    				zPos[x][y] = Math.max( 0, Math.min( zPos[x][y], DEPTH ) );
//    			}
//    		}
//    		
//    		for( Raspi raspi : raspiList )
//    		{
//    			int x = raspi.x;
//    			int y = raspi.y;
//    			
//    			raspi.move(changes[x][y]);
//    		}
//		}
//	}

	public static void main( String[] args )
	{
		new RaspiBallsApp();
	}
}
