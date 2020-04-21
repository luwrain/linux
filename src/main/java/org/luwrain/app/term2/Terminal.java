
package org.luwrain.app.term2;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.linux.*;

final class Terminal implements Lines
{
    static private final String LOG_COMPONENT = App.LOG_COMPONENT;

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
	    switch(seq)
	    {
	    case "\n":
		lines.add("");
		break;
	    default:
		lines.set(lines.size() - 1, lines.get(lines.size() - 1) + seq);
	    }
	    seq = "";
	    return;
	}
	if (res.isEmpty())
	    return;
	seq = "";
	switch(res)
	{
	case "color":
	    return;
	default:
	    Log.warning(LOG_COMPONENT, "unknown command: '" + res + "'");
	}
    }
}
