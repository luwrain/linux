
package org.luwrain.app.term2;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.linux.*;

final class Terminal implements Lines
{
    private final TermInfo termInfo;
    private Vector<String> lines = new Vector();
    private int hotPointX = 0;
    private int hotPointY = -1;
    private String seq = "";

    Terminal(TermInfo termInfo)
    {
	NullCheck.notNull(termInfo, "termInfo");
    this.termInfo = termInfo;
}

@Override public int getLineCount()
{
    return lines.size();
}

@Override public String getLine(int index)
{
    if (index >= lines.size())
	return "";
    return lines.get(index);
}

void newCh(char ch)
    {
	if (lines.isEmpty())
	    lines.add("");
	this.seq += ch;
	final String res = termInfo.find(seq);
	if (res == null)
	{
	    lines.set(lines.size() - 1, lines.get(lines.size() - 1) + seq);
	    seq = "";
	    return;
	}
	if (res.isEmpty())
	    return;
		    lines.set(lines.size() - 1, lines.get(lines.size() - 1) + res);
	    seq = "";
    }
}
