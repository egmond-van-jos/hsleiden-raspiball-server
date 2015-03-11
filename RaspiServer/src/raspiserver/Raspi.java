package raspiserver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class Raspi implements Runnable
{
	public static final int RANGE_MIN = 10;
	public static final int RANGE_MAX = 240;
	public static final int PORT = 5000;
	
	private Thread thread;
	
	public int x;
	public int y;
	public final String ip;
	
	private Queue<Command> commandQueue;
	private boolean alive = true;
	private boolean ready = false;
	
	public Raspi( String ip )
	{
		this.ip = ip;
		
		this.x = Integer.parseInt( this.sendCommand( Command.UPDATEX ) );
		this.y = Integer.parseInt( this.sendCommand( Command.UPDATEY ) );
		
		this.commandQueue = new LinkedList<Command>();
		this.queueCommand( Command.RESET_POSITION );
		
		this.thread = new Thread( this );
		this.thread.start();
	}
	
	public boolean isReady()
	{
		return this.ready;
	}
	
	public void move( int steps )
	{
		queueCommand( Command.MOVE(steps) );
	}
	
	public void queueCommand( Command command )
	{
		this.commandQueue.add( command );
	}

	@Override
	public void run() {

		while( alive == true )
		{
			Command command = this.commandQueue.poll();
			if( command != null )
			{
				ready = false;
				
				sendCommand( command );
				
				if( command == Command.RESET_POSITION )
				{
					try {
						Thread.sleep( 5000 );
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					sendCommand( Command.HOME );
					sendCommand( Command.DOWN(500) );
				}
				
				ready = true;
			}

			
			try {
				Thread.sleep( 1 );
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	private String sendCommand( Command command )
	{
		String returnString = null;
		
		try
		{
			URL url = new URL( this.ip + "/" + command.getString() );
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setConnectTimeout(5);
			
			System.out.println( "Sending command raspi " + this.ip + ": " + command.getString() );
	
		    InputStream is = conn.getInputStream();
			BufferedReader rd = new BufferedReader( new InputStreamReader( is ) );
			
			returnString = new String();
			String line = null;
			while( (line = rd.readLine()) != null )
			{
				returnString += line;
			}
		}
		
		catch ( Exception e ) { System.out.println( "ERROR raspi " + this.ip + ": " + e.getMessage() ); }
		
		return returnString;
	}
	


	public static List<Raspi> getActiveRaspis()
	{
		List<Raspi> raspiList = new ArrayList<Raspi>();
		
		for( int i = 0; i < RANGE_MAX; i++ )
		{
			try
			{
				String ip = "http://192.168.1." + (i + RANGE_MIN) + ":" + PORT;

				URL url = new URL( ip );
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				conn.setConnectTimeout(15);

			    InputStream is = conn.getInputStream();
				BufferedReader rd = new BufferedReader( new InputStreamReader( is ) );

				String result = new String();
				String line = null;
				while( (line = rd.readLine()) != null )
				{
					result += line;
				}
				
				if( result != null && result.equals("") == false )
				{
					raspiList.add( new Raspi( ip ) );
				}
				
				rd.close();
			}
			
			catch( Exception e ) { System.out.println( "ERROR raspi " + i + ": " + e.getMessage() ); }
		}

		System.out.println( "Active raspis: " + raspiList.size() );
		return raspiList;
	}
}
