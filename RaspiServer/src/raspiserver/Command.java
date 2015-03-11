package raspiserver;

public class Command
{
	public static final Command HOME = new Command( "home" );
//	public static final Command AUTO_ON = new Command( "auto_on" );
	public static final Command RESET_POSITION = new Command( "auto_off" );
	public static final Command UPDATEX = new Command( "xpos" );
	public static final Command UPDATEY = new Command( "ypos" );

	public static final Command MOVE( int steps )
	{
		if( steps < 0 ) return UP( -steps );
		else 			return DOWN( steps );
	}
	
	public static final Command DOWN( int steps )
	{
		Command command = new Command( "down", steps );
		return command;
	}

	public static final Command UP( int steps )
	{
		Command command = new Command( "up", steps );
		return command;
	}
	
	private String string;
	
	public Command( String string )
	{
		this.string = string;
	}

	public Command( String string, int value )
	{
		this.string = string + "?steps=" + value;
	}
	
	public String getString()
	{
		return string;
	}
}
