/*
   Copyright 2012-2020 Michael Pozhidaev <msp@luwrain.org>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.linux;

import java.io.*;
import java.util.*;

import org.luwrain.core.*;

public final class TermInfo
{
final String text;
    private String termName = null;
    private Set<String> values = new HashSet();
    private Map<Character, Map<String, String> > seqs = new HashMap();

    TermInfo() throws IOException
    {
	final Process p = new ProcessBuilder("infocmp", "linux").start();
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

    void read()
    {
	StringBuilder b = new StringBuilder();
	try {
	final StringReader r = new StringReader(text);
	try {
	    //This builder being created indicates that we are in the char code reading mode
	    StringBuilder codeBuilder = null;
	    for(int n = r.read();n >= 0;n = r.read())
	    {
		final char c = (char)n;
		if (codeBuilder != null)
		{
		    if (c >= '0' && c <= '0')
		    {
			codeBuilder.append(c);
			continue;
		    }
		    //The char code sequence ends here, constructing the char and continuing as usual
		    b.append(buildChar(new String(codeBuilder)));
		    codeBuilder = null;
		}
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
		case '\\':
		    {
			int nn = r.read();
			if (nn < 0)
			{
			    b.append('\\');
			    return;
			}
			final char cc = (char)nn;
			switch(cc)
			{
			case 'e':
			case 'E':
			    b.append((char)27);
			    continue;
			}
			if (cc < '0' || cc > '9')
			{
			    b.append(cc);
			    continue;
			}
			//Activating the mode of reading the char code
			codeBuilder = new StringBuilder();
			codeBuilder.append(cc);
			continue;
		    }
		default:
		    b.append(c);
		}
	    }
	}
	finally {
	    r.close();
	    if (b.length() > 0)
		processItem(new String(b));
	}
	}
	catch(IOException e)
	{
	    throw new RuntimeException(e);
	}
    }

    private String buildChar(String code)
    {
	try {
	    return Character.toString((char)Integer.parseInt(code));
	}
	catch(NumberFormatException e)
	{
	    return "";
	}
    }

    private void processItem(String text)
    {
	final int pos = text.indexOf("=");
	if (pos > 0 && pos < text.length() - 1)
	{
	    final String name = text.substring(0, pos);
	    final String value = text.substring(pos + 1);
	    final Character c = new Character(value.charAt(0));
	    Map<String, String> m = seqs.get(c);
	    if (m == null)
		m = new HashMap();
	    seqs.put(c, m);
	    m.put(value, name);
	    return;
	}
	if (termName == null)
	{
	    termName = text;
	    return;
	}
	values.add(text);
    }

    public String find(String seq)
    {
	NullCheck.notEmpty(seq, "seq");
	final Map<String, String> m = seqs.get(new Character(seq.charAt(0)));
	if (m == null)
	    return null;
	final String s = m.get(seq);
	if (s != null)
	    return s;
	for(Map.Entry<String, String> e: m.entrySet())
	    if (e.getKey().startsWith(seq))
		return "";
	return null;
	    }

    String getTermName()
    {
	return termName != null?termName:"";
    }
}
