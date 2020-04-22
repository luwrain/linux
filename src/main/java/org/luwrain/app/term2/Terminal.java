
package org.luwrain.app.term2;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.linux.*;

final class Terminal implements Lines
{
    static private final String LOG_COMPONENT = App.LOG_COMPONENT;

    static private final String BELL = Character.toString((char)7);

    private final Luwrain luwrain;
    private final TermInfo termInfo;
    private Vector<String> lines = new Vector();
    private int hotPointX = 0;
    private int hotPointY = -1;
    private String seq = "";
    private StringBuilder speaking = new StringBuilder();

    Terminal(Luwrain luwrain, TermInfo termInfo)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(termInfo, "termInfo");
	this .luwrain = luwrain;
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
	    if (seq.equals(BELL))
	    {
		luwrain.playSound(Sounds.TERM_BELL);
	    } else
	    switch(seq)
	    {
	    case "\n":
		lines.add("");
		if (speaking.length() > 0)
		{
		    luwrain.speak(luwrain.getSpeakableText(new String(speaking), Luwrain.SpeakableTextType.PROGRAMMING));
		    speaking = new StringBuilder();
		}
		break;
	    default:
		speaking.append(seq);
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
