

package org.luwrain.os;

public class Init
{
    private static final String LUWRAIN_LINUX_LIBRARY_NAME = "luwrainlinux";

    public static void init()
    {
	System.loadLibrary(LUWRAIN_LINUX_LIBRARY_NAME);
    }

    public static void main(String args[])
    {
	init();
	try {
	    Terminal t = new Terminal();
	    t.open("date");
	    }
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }
}
