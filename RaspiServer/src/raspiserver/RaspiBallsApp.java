package raspiserver;

import java.io.BufferedReader;
import java.io.IOException;
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
	
	public RaspiBallsApp( List<Raspi> raspiList )
	{	
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
			ServerSocket serverSocket = new ServerSocket( 5001 );
			Socket clientSocket = serverSocket.accept();
			
			PrintWriter writer = new PrintWriter( clientSocket.getOutputStream() );
			writer.println( "connection ready" );
	    	System.out.println( "connection ready" );
			
		    BufferedReader reader = new BufferedReader(
		            new InputStreamReader(clientSocket.getInputStream()));

			Command command = null;
			
		    String inputString = reader.readLine();
		    while( inputString != null )
		    {
		    	System.out.println( inputString );
		    	
		    	String[] commands = inputString.split(" ");
		    	if( commands[0].equals( "command" ) )
		    	{
		    		String commandString = commands[1];
		    		if( commands.length == 3 )
		    		{
		    			int steps = Integer.parseInt( commands[2] );
		    			command = new Command( commandString, steps );
		    		}
		    		
		    		else
		    		{
		    			command = new Command( commandString );
		    		}
		    		
					for( Raspi raspi : raspiList )
					{
						raspi.queueCommand( command );
					}
		    	}
		    	
		    	//patterns not yet implemented
//		    	else if( commands[0].equals("pattern") )
//		    	{
//		    		Pattern pattern = null;
//		    		
//		    		if( commands[1].equals("snake") )
//		    		{
//		    			pattern = new Snakes();
//		    		}
//		    		
//		    		if( pattern != null )
//		    		{
//			    		int[][] changes = new int[SIZE][SIZE];
//			    		
//			    		for( int x = 0; x < zPos.length; x++ )
//			    		{
//			    			for( int y = zPos[0].length - 1; y >= 0; y-- )
//			    			{
//			    				double target = DEPTH/2D + DEPTH * Math.max( -1, Math.min( 1, pattern.calculate(DEPTH,SIZE,timer,x,y,(int)zPos[x][y]) ) );
//			    				double change = (target - zPos[x][y]); //Math.min( Math.abs(speed), Math.max( -Math.abs(speed), (target-raspi.z)*(DEPTH/800d) ) );
//	
//			    				double target = currentPattern[0].calc(DEPTH,SIZE,timer*DEPTH*speed/10d,x,y,(int)zPos[x][y]);
//			    				double change = Math.min( Math.abs(speed), Math.max( -Math.abs(speed), (target-zPos[x][y])*(DEPTH/800d) ) );
//			    				changes[x][y] = (int)Math.signum( (int)(change/10) );
//			    			}
//			    		}
//	
//			    		for( int x = 0; x < zPos.length; x++ )
//			    		{
//			    			for( int y = zPos[0].length - 1; y >= 0; y-- )
//			    			{
//			    				zPos[x][y] += changes[x][y]*speed;
//			    				zPos[x][y] = Math.max( 0, Math.min( zPos[x][y], DEPTH ) );
//			    			}
//			    		}
//			    		
//			    		for( Raspi raspi : raspiList )
//			    		{
//			    			int x = raspi.x;
//			    			int y = raspi.y;
//			    			
//			    			raspi.move(changes[x][y]);
//			    		}
//		    		}
//		    	}
		    	
		    	inputString = reader.readLine();
		    }
		    
		    reader.close();
		    writer.close();
			serverSocket.close();
		}
		
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void main( String[] args )
	{
		List<Raspi> raspis = Raspi.getActiveRaspis();
		
		new RaspiBallsApp( raspis );
	}
}
