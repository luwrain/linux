
package org.luwrain.linux;

import java.io.*;

public class PointerInputListening
{
    	static final int STEP_X = 30;
    	static final int STEP_Y = 20;

        static int posX = 0;
    static int posY = 0;

    public static void main(String[] arg) throws Exception
    {
	final DataInputStream s = new DataInputStream(new FileInputStream("/dev/input/mice"));
	do {
	    final int code = s.readUnsignedByte();
	    final int x = s.readByte();
	    final int y = s.readByte();
	    if ((code & 8) > 0)
		onOffset(x, y);
	} while(true);
    }

    static void onOffset(int x, int y)
    {
	posX += x;
	posY += y;
	boolean step = false;
	do {
	    step = false;
	    while (posX > STEP_X)
	    {
		System.out.println("right");
		posX -= STEP_X;
		step = true;
	    }
	    while (posY > STEP_Y)
	    {
		System.out.println("up");
		posY -= STEP_Y;
		step = true;
	    }
	    while (posX < -1 * STEP_X)
	    {
		System.out.println("left");
		posX += STEP_X;
		step = true;
	    }
	    while (posY < -1 * STEP_Y)
	    {
		System.out.println("down");
		posY += STEP_Y;
		step = true;
	    }
	} while(step);
	//	System.out.println("next");
    }
}
