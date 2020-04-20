
package org.luwrain.app.term2;

import org.luwrain.linux.term.*;
import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.util.*;

import org.luwrain.app.term.Strings;

public class TermApp implements Application, Actions
{
    static private final String STRINGS_NAME = "luwrain.term";

    private Luwrain luwrain;
    private Strings strings;
    private final Base base = new Base();
    private NavigationArea area;
    private String desiredDir;

    private int oldHotPointX = -1;
    private int oldHotPointY = -1;

    public TermApp(String desiredDir)
    {
	NullCheck.notNull(desiredDir, "desiredDir");
	this.desiredDir = desiredDir;
    }

    @Override public InitResult onLaunchApp(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	Log.debug("term2", "first");
	try {
	final Object o = luwrain.i18n().getStrings(STRINGS_NAME);
	if (o == null || !(o instanceof Strings))
	    return new InitResult(InitResult.Type.NO_STRINGS_OBJ, STRINGS_NAME);
	strings = (Strings)o;
	this.luwrain = luwrain;
	Log.debug("term2", "before init");
	if (!base.init(luwrain))
	    return new InitResult(InitResult.Type.FAILURE);
	createArea();
	if (!base.start(desiredDir, this))
	    return new InitResult(InitResult.Type.FAILURE);
	}
	catch(Throwable e)
	{
	    Log.error("term2", e.getClass().getName() + ":" + e.getMessage());
	    return new InitResult(e);
	}
	return new InitResult();
    }

    @Override public void notify(String text,
				 int hotPointX, int hotPointY,
				 boolean bell)
    {
	if (bell)
	    luwrain.playSound(Sounds.TERM_BELL);
	if (text != null && !text.trim().isEmpty())
	{
	    if (text.length() == 1)
		luwrain.speakLetter(text.charAt(0)); else
		luwrain.speak(text);
	}
	if (hotPointX != oldHotPointX || hotPointY != oldHotPointY)
	{
	    if (text == null || text.isEmpty())
	    {
		final String line = area.getLine(hotPointY);
		if (line != null && hotPointX < line.length())
		    luwrain.speakLetter(line.charAt(hotPointX));
	    }
	    area.setHotPoint(hotPointX, hotPointY);
	    oldHotPointX = hotPointX;
	    oldHotPointY = hotPointY;
	}
	luwrain.onAreaNewContent(area);
    }

    private void createArea()
    {
	final Actions actions = this;
	final Terminal term = base.getTerm();
	final Strings s = strings;
	area = new NavigationArea(new DefaultControlContext(luwrain)){
		@Override public int getLineCount()
		{
		    return term.getLineCount() >= 1?term.getLineCount():1;
		}
		@Override public String getLine(int index)
		{
		    if (index >= term.getLineCount())
			return "";
		    final String line = term.getLine(index);
		    return line != null?line:"";
		}
		@Override public boolean onInputEvent(KeyboardEvent event)
		{
		    if (event.isSpecial() && !event.isModified())
			switch(event.getSpecial())
			{
			case ENTER:
			    term.write(new byte[]{(byte)'\n'});
			    return true;
			case BACKSPACE:
			    term.write(new byte[]{(byte)'\b'});
			    return true;
			case TAB:
			    term.write(new byte[]{(byte)'\t'});
			    return true;
			case 			    ALTERNATIVE_ARROW_LEFT:
			    term.writeCode(Terminal.Codes.ARROW_LEFT);
			    return true;
			case 			    ALTERNATIVE_ARROW_RIGHT:
			    term.writeCode(Terminal.Codes.ARROW_RIGHT);
			    return true;
			case 			    ALTERNATIVE_ARROW_UP:
			    term.writeCode(Terminal.Codes.ARROW_UP);
			    return true;
			case 			    ALTERNATIVE_ARROW_DOWN:
			    term.writeCode(Terminal.Codes.ARROW_DOWN);
			    return true;
			}
		    if (!event.isSpecial())
		    {
			if (event.getChar() == ' ')
			{
			    final String lastWord = TextUtils.getLastWord(getLine(getHotPointY()), getHotPointX());
			    if (lastWord != null && !lastWord.trim().isEmpty())
				luwrain.speak(lastWord);
			}
			term.write(event.getChar());
			return true;
		    }
		    //FIXME:
		    return super.onInputEvent(event);
		}
		@Override public boolean onSystemEvent(EnvironmentEvent event)
		{
		    switch(event.getCode())
		    {
		    case CLOSE:
			actions.closeApp();
			return true;
		    default:
			return super.onSystemEvent(event);
		    }
		}
		@Override public String getAreaName()
		{
		    return strings.areaName();
		}
	    };
    }

    @Override public AreaLayout getAreaLayout()
    {
	return new AreaLayout(area);
    }

    @Override public void closeApp()
    {
	base.close();
	luwrain.closeApp();
    }

    @Override public String getAppName()
    {
	return strings.appName();
    }
}
