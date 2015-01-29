
package org.luwrain.linux;

import java.io.*;
import java.util.concurrent.*;

import org.luwrain.speech.BackEnd;

public class CommandBackEnd implements BackEnd
{
    private static final int BACKGROUND_THREAD_DELAY = 100;


class Chunk
{
    public String text = "";
    public int pitch = 50;
    public int rate = 0;

    public Chunk(String text,
		 int pitch,
int rate)
    {
	if (text == null)
	    throw new NullPointerException("text may not be null");
	this.text = text;
	this.pitch = pitch;
	this.rate = rate;
    }
}

    private String cmd;
    private ProcessGroup pg = new ProcessGroup();
    private LinkedBlockingQueue<Chunk> chunks = new LinkedBlockingQueue<Chunk>(1024);
    private int defaultPitch = 50;
    private int defaultRate = 50;

    public CommandBackEnd()
    {
    }

    public CommandBackEnd(String cmd)
    {
	this.cmd = cmd;
	if (cmd == null)
	    throw new NullPointerException("cmd may not be null");
	if (cmd.isEmpty())
	    throw new IllegalArgumentException("cmd may not be empty");
    }

    @Override public String init(String[] cmdLine)
    {
	//FIXME:
	return null;
    }

    @Override public void say(String text)
    {
	if (text == null)
	    throw new NullPointerException("text may not be null");
	if (text.trim().isEmpty())
	    return;
	if (!pg.busy())
	{
	    pg.run(constructCmd(defaultPitch, defaultRate), text);
	    return;
	}
	try {
	    //	    System.out.println("busy");
	    chunks.put(new Chunk(text, defaultPitch, defaultRate));
	}
	catch(InterruptedException e)
	{
	    e.printStackTrace();
	}
    }

    @Override public void sayLetter(char letter)
    {
    }

    @Override public void silence()
    {
	chunks.clear();
	pg.stop();
    }

    private void runBkgThread()
    {
	final ProcessGroup p = pg;
	final LinkedBlockingQueue<Chunk> c = chunks;
	Runnable r = new Runnable(){
		private ProcessGroup pg = p;
		private LinkedBlockingQueue<Chunk> chunks = c;
		@Override public void run()
		{
		    while (true)
		    {
			try {
			    Thread.sleep(BACKGROUND_THREAD_DELAY);
			}
			catch (InterruptedException e)
			{
			    e.printStackTrace();
			}
			if (chunks.isEmpty())
			    continue;
			if (pg.busy())
			    continue;
			try {
			    Chunk chunk = chunks.take();
			    pg.run(constructCmd(chunk.pitch, chunk.rate), chunk.text);
			}
			catch (InterruptedException e)
			{
			    e.printStackTrace();
			}
		    }
		}
	    };
	new Thread(r).start();
    }

    @Override public void setPitch(int value)
    {
	//FIXME:
    }

    private String constructCmd(int pitch, int rate)
    {
	return cmd;
    }

    public static void main(String[] args)
    {
	if (args.length < 1)
	{
	    System.err.println("You should provide a command to speak with as a first command line option!");
	    System.exit(1);
	}
	System.loadLibrary("luwrainlinux");
	CommandBackEnd backend = new CommandBackEnd(args[0]);
	backend.runBkgThread();
	String line = "";
	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	while(true)
	{
	    System.out.print("What to say?>");
	    try 
	    {
		line = br.readLine();
	    } 
	    catch (IOException e) 
	    {
		e.printStackTrace();
		System.exit(1);
	    }
	    if (line.isEmpty())
		backend.silence(); else
		if (line.equals("quit"))
		{
		    backend.silence();
		    System.exit(1);
		} else
		{
		    System.out.println("Speaking \'" + line + "\'");
		    backend.say(line);
		}
	}
    }
}
