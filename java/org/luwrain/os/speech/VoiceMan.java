/*
   Copyright 2012-2014 Michael Pozhidaev <msp@altlinux.org>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.os.speech;

import java.net.*;
import java.io.*;
import org.luwrain.os.SpeechBackEnd;

public class VoiceMan implements SpeechBackEnd
{
    private Socket sock;
    private PrintStream output;

    public boolean connect(String host, int port)
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
	    return false;
	}
	return true;
    }

    public void close()
    {
	//	try {
	    if (output != null)
		output.close();
	    /*
	}
	catch(IOException e)
	{
	    e.printStackTrace();
	}
	    */
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

    public void say(String text)
    {
	if (output == null)
	    return;
	silence();
	//FIXME:must be sure no \n;
	output.println("T:" + text);
	output.flush();
    }

    public void sayLetter(char letter)
    {
	if (output == null)
	    return;
	silence();
	String s = "L:";
	s += letter;
	output.println(s);
	output.flush();
    }

    public void setPitch(int value)
    {
	if (output == null)
	    return;
	String s = "P:" + value;
	output.println(s);
	output.flush();
    }

    public void silence()
    {
	if (output == null)
	    return;
	output.println("S:");
	output.flush();
    }
}
