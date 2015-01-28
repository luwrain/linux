
package org.luwrain.linux;

import java.io.*;

import org.luwrain.speech.BackEnd;

public class CommandBackEnd implements BackEnd
{
    private String cmd;
    private Process process;

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
	if (process != null)
	    silence();
	String[] args = new String[3];
	args[0] = "/bin/sh";
	args[1] = "-c";
	args[2] = "echo proba | RHVoice | aplay";
	try {
	    process = Runtime.getRuntime().exec(args);
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	}
    }

    @Override public void sayLetter(char letter)
    {
    }

    @Override public void silence()
    {
	if (process == null)
	    return;
	System.out.println("silence");
	process.destroy();
	process = null;
    }

    @Override public void setPitch(int value)
    {
	//FIXME:
    }

    public static void main(String[] args)
    {
	if (args.length < 1)
	{
	    System.err.println("You should provide a command to speak with as a first command line option!");
	    System.exit(1);
	}
	CommandBackEnd backend = new CommandBackEnd(args[0]);
	String line = "";
	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	while(true)
	{
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
		    backend.say(line);
	}
    }
}
