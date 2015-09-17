/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.linux.speech;

import java.net.*;
import java.io.*;
import org.luwrain.speech.BackEnd;

public class VoiceMan implements BackEnd
{
    private Socket sock;
    private PrintStream output;
    private int defaultPitch = 50;
    private int defaultRate = 50;

    @Override public String init(String[] cmdLine)
    {
	final String res = connect("localhost", 5511);
	if (res == null)
	    return null;
	return "connecting to VoiceMan at localhost:5511" + res;
    }

    public void close()
    {
	if (output != null)
	    output.close();
	try {
	    if (sock != null)
		sock.close();
	}
	catch(IOException e)
	{
	    e.printStackTrace();
	}
	output = null;
	sock = null;
    }

    @Override public void say(String text)
    {
	if (output == null)
	    return;
	output.println("T:" + text);
	output.flush();
    }

    @Override public void say(String text, int pitch)
    {
	if (output == null)
	    return;
	output.println("P:" + pitch);
	output.println("T:" + text);
	output.println("P:" + defaultPitch);
	output.flush();
    }

    @Override public void say(String text,
			      int pitch,
			      int rate)
    {
	if (output == null)
	    return;
	output.println("P:" + pitch);
	output.println("R:" + rate);
	output.println("T:" + text);
	output.println("P:" + defaultPitch);
	output.println("R:" + defaultRate);
	output.flush();
    }

    @Override public void sayLetter(char letter)
    {
	if (output == null)
	    return;
	String s = "L:";
	s += letter;
	output.println(s);
	output.flush();
    }

    @Override public void sayLetter(char letter, int pitch)
    {
	if (output == null)
	    return;
	String s = "L:";
	s += letter;
	output.println("P:" + pitch);
	output.println(s);
	output.println("P:" + defaultPitch);
	output.flush();
    }

    @Override public void sayLetter(char letter,
				    int pitch,
				    int rate)
    {
	if (output == null)
	    return;
	String s = "L:";
	s += letter;
	output.println("P:" + pitch);
	output.println("R:" + rate);
	output.println(s);
	output.println("P:" + defaultPitch);
	output.println("R:" + defaultRate);
	output.flush();
    }

    public void silence()
    {
	if (output == null)
	    return;
	output.println("S:");
	output.flush();
    }

    @Override public void setDefaultPitch(int value)
    {
	if (output == null)
	    return;
	defaultPitch = value;
	if (defaultPitch < 0)
	    defaultPitch = 0;
	if (defaultPitch > 100)
	    defaultPitch = 100;
	String s = "P:" + defaultPitch;
	output.println(s);
	output.flush();
    }

    @Override public void setDefaultRate(int value)
    {
	if (output == null)
	    return;
	defaultRate = value;
	if (defaultRate < 0)
	    defaultRate = 0;
	if (defaultRate > 100)
	    defaultRate = 100;
	String s = "R:" + defaultRate;
	output.println(s);
	output.flush();
    }

    private String connect(String host, int port)
    {
	try {
	    sock = new Socket(host, port);
	    output = new PrintStream(sock.getOutputStream());
	}
	catch(IOException e)
	{
	    sock = null;
	    output = null;
	    e.printStackTrace();
	    return e.getMessage();
	}
	return null;
    }
}
