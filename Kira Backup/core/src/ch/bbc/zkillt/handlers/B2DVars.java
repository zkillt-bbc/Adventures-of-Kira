package ch.bbc.zkillt.handlers;

public class B2DVars {
	
	// pixel per meter ratio
	public static final float PPM = 100;
	
	// category bits
	public static final short BIT_PLAYER = 2; 		// 0000 0000 0000 0010
	public static final short BIT_GROUND = 4; 		// 0000 0000 0000 0100
	public static final short BIT_SCHRAEG = 8; 		// 0000 0000 0000 1000	
	public static final short BIT_WATER = 16;		// 0000 0000 0001 0000
	public static final short BIT_BACKGROUND = 32; 	// 0000 0000 0010 0000
	public static final short BIT_COIN = 64; 		// 0000 0000 0100 0000
	public static final short BIT_ENEMY = 128; 		// 0000 0000 1000 0000

}
