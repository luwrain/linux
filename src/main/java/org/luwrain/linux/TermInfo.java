
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

    public void read()
    {
	StringBuilder b = new StringBuilder();
	try {
	final StringReader r = new StringReader(text);
	try {
	    for(int n = r.read();n >= 0;n = r.read())
	    {
		final char c = (char)n;
		switch(c)
		{
		case ' ':
		    if (b.length() > 0)
			b.append(' ');
		    continue;
		case '\t':
		    continue;
		case ',':
		    processItem(new String(b));
		    b = new StringBuilder();
		    continue;
		default:
		    b.append(c);
		}
	    }
	}
	finally {
	    r.close();
	}
	}
	catch(IOException e)
	{
	    throw new RuntimeException(e);
	}
    }

    private void processItem(String text)
    {
	System.out.println(text);
    }


    static public void main(String[] args) throws Exception
    {
	TermInfo t = 	new TermInfo();
	t.read();
    }
}
