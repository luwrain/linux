
package org.luwrain.linux;

import java.io.*;
import java.util.*;

class TermInfo
{
final String text;

    TermInfo() throws IOException
    {
	final Process p = new ProcessBuilder("infocmp").start();
	p.getOutputStream().close();
		    	final BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
				final StringBuilder b = new StringBuilder();
	try {

	String line = r.readLine();
	while(line != null)
	{
	    if (!line.isEmpty() && !line.startsWith("#"))
	    b.append(line);
	    line = r.readLine();
	}
	}
	finally {
	    r.close();
	}
	try {
	p.waitFor();
	}
		catch(InterruptedException e)
	{
	    Thread.currentThread().interrupt();
	}
	if (p.exitValue() != 0)
	    throw new IOException("Unable to read the terminfo database, exit value is " + String.valueOf(p.exitValue()));
	this.text = new String(b);
    }


    static public void main(String[] args) throws Exception
    {
	TermInfo t = 	new TermInfo();
	System.out.println(t.text);
    }
}
