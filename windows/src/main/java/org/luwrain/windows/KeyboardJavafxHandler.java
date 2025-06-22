/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>
   Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

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

package org.luwrain.windows;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import org.luwrain.core.*;
import org.luwrain.core.events.InputEvent;
import org.luwrain.core.events.InputEvent.Special;
import org.luwrain.interaction.KeyboardHandler;

@SuppressWarnings({"deprecation","restriction"}) 
class KeyboardJavafxHandler implements KeyboardHandler
{
    private EventConsumer consumer;

    private boolean leftAltPressed = false;
    private boolean rightAltPressed = false;
    private boolean controlPressed = false;
    private boolean shiftPressed = false;

    @Override public void setEventConsumer(EventConsumer consumer)
    {
	this.consumer = consumer;
    }

    @Override public void onKeyPressed(Object obj)
    {
	final KeyEvent event=(KeyEvent)obj;
	if (consumer == null)
	    return;
	controlPressed=event.isControlDown();
	shiftPressed=event.isShiftDown();
	leftAltPressed=event.isAltDown();
	Special code=null;
	switch(event.getCode())
	{
	    // Functions keys
	case F1:code=Special.F1;break;
	case F2:code=Special.F2;break;
	case F3:code=Special.F3;break;
	case F4:code=Special.F4;break;
	case F5:code=Special.F5;break;
	case F6:code=Special.F6;break;
	case F7:code=Special.F7;break;
	case F8:code=Special.F8;break;
	case F9:code=Special.F9;break;
	case F10:code=Special.F10;break;
	case F11:code=Special.F11;break;
	case F12:code=Special.F12;break;
	    // Arrows;
	case LEFT:code=Special.ARROW_LEFT;break;
	case RIGHT:code=Special.ARROW_RIGHT;break;
	case UP:code=Special.ARROW_UP;break;
	case DOWN:code=Special.ARROW_DOWN;break;
	    // Jump keys;
	case HOME:code=Special.HOME;break;
	case END:code=Special.END;break;
	case INSERT:code=Special.INSERT;break;
	case PAGE_DOWN:code=Special.PAGE_DOWN;break;
	case PAGE_UP:code=Special.PAGE_UP;break;
	case WINDOWS:code=Special.WINDOWS;break;
	case CONTEXT_MENU:code=Special.CONTEXT_MENU;break;
	    // modificators
	case CONTROL:code=Special.CONTROL;break;
	case SHIFT:code=Special.SHIFT;break;
	case ALT:code=Special.LEFT_ALT;break;
	case ALT_GRAPH:code=Special.RIGHT_ALT;break;
	default:
	    final String ch=event.getText();
	    if(!shiftPressed&&(leftAltPressed||rightAltPressed)&&!ch.isEmpty())
	    {
	    	final InputEvent emulated=new InputEvent(ch.toLowerCase().charAt(0),shiftPressed,controlPressed,leftAltPressed);
	    	consumer.enqueueEvent(emulated);
	    }
	    return;
	}
	consumer.enqueueEvent(new InputEvent(code,shiftPressed,controlPressed,leftAltPressed));
    }

    @Override public void onKeyReleased(Object obj)
    {
	final KeyEvent event = (KeyEvent)obj;
	if (consumer == null)
	    return;
	controlPressed=event.isControlDown();
	shiftPressed=event.isShiftDown();
	leftAltPressed=event.isAltDown();
    }

    @Override public void onKeyTyped(Object obj)
    {
	final KeyEvent event = (KeyEvent)obj;
	if (consumer == null)
	    return;
	controlPressed=event.isControlDown();
	shiftPressed=event.isShiftDown();
	leftAltPressed=event.isAltDown();
	final String keychar=event.getCharacter();
	Special code;
	if(keychar.equals("\b"))
	    code=Special.BACKSPACE; else
	    if(keychar.equals("\n") || keychar.equals("\r")) 
		code=Special.ENTER; else 
		if(keychar.equals("\u001b"))
		    code=Special.ESCAPE; else
		    if(keychar.equals("\u007f"))
			code=Special.DELETE; else 
			if(keychar.equals("\t"))
			    code=Special.TAB; else
			{
			    // FIXME: javafx characters return as String type we need a char (now return first symbol)
			    final InputEvent emulated=new InputEvent(event.getCharacter().charAt(0),shiftPressed,controlPressed,leftAltPressed);
			    consumer.enqueueEvent(emulated);
			    return;
			}
	consumer.enqueueEvent(new InputEvent(code, shiftPressed,controlPressed,leftAltPressed));
    }
}
