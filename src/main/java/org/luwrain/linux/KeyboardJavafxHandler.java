/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.linux;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import org.luwrain.core.*;
import org.luwrain.base.*;
import org.luwrain.core.events.KeyboardEvent;
import org.luwrain.interaction.KeyboardHandler;

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
	KeyboardEvent.Special code = null;
	//	System.exit(event.getCode());
	switch(event.getCode())
	{
	    // Functions keys
	case F1:
	    code=KeyboardEvent.Special.F1;
	    break;
	case F2:
	    code=KeyboardEvent.Special.F2;
	    break;
	case F3:
	    code=KeyboardEvent.Special.F3;
	    break;
	case F4:
	    code=KeyboardEvent.Special.F4;
	    break;
	case F5:
	    code=KeyboardEvent.Special.F5;
	    break;
	case F6:
	    code=KeyboardEvent.Special.F6;
	    break;
	case F7:
	    code=KeyboardEvent.Special.F7;
	    break;
	case F8:
	    code=KeyboardEvent.Special.F8;
	    break;
	case F9:
	    code=KeyboardEvent.Special.F9;
	    break;
	case F10:
	    code=KeyboardEvent.Special.F10;
	    break;
	case F11:
	    code=KeyboardEvent.Special.F11;
	    break;
	case F12:
	    code=KeyboardEvent.Special.F12;
	    break;
	case LEFT:
	    code=KeyboardEvent.Special.ARROW_LEFT;
	    break;
	case RIGHT:
	    code=KeyboardEvent.Special.ARROW_RIGHT;
	    break;
	case UP:
	    code=KeyboardEvent.Special.ARROW_UP;
	    break;
	case DOWN:
	    code=KeyboardEvent.Special.ARROW_DOWN;
	    break;
	case HOME:
	    code=KeyboardEvent.Special.HOME;
	    break;
	case END:
	    code=KeyboardEvent.Special.END;
	    break;
	case INSERT:
	    code=KeyboardEvent.Special.INSERT;
	    break;
	case PAGE_DOWN:
	    code=KeyboardEvent.Special.PAGE_DOWN;
	    break;
	case PAGE_UP:
	    code=KeyboardEvent.Special.PAGE_UP;
	    break;
	case WINDOWS:
	    code=KeyboardEvent.Special.WINDOWS;
	    break;
	case CONTEXT_MENU:
	    code=KeyboardEvent.Special.CONTEXT_MENU;
	    break;
	case CONTROL:
	    code=KeyboardEvent.Special.CONTROL;
	    break;
	case SHIFT:
	    code=KeyboardEvent.Special.SHIFT;
	    break;
	case ALT:
	    code=KeyboardEvent.Special.LEFT_ALT;
	    break;
	case ALT_GRAPH:
	    code=KeyboardEvent.Special.RIGHT_ALT;
	    break;
	default:
	    return;
	}
	consumer.enqueueEvent(new KeyboardEvent(code, shiftPressed, controlPressed, leftAltPressed));
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
	KeyboardEvent.Special code = null;
	if(keychar.equals(KeyCode.BACK_SPACE.impl_getChar()))
	    code=KeyboardEvent.Special.BACKSPACE; else
	    if(keychar.equals(KeyCode.ENTER.impl_getChar())||keychar.equals("\n")||keychar.equals("\r")) 
		code=KeyboardEvent.Special.ENTER; else 
		if(keychar.equals(KeyCode.ESCAPE.impl_getChar())) 
		    code=KeyboardEvent.Special.ESCAPE; else
		    if(keychar.equals(KeyCode.DELETE.impl_getChar())) 
			code=KeyboardEvent.Special.DELETE; else 
			if(keychar.equals(KeyCode.TAB.impl_getChar())) 
			    code=KeyboardEvent.Special.TAB; else
			{
			    // FIXME: javafx characters return as String type we need a char (now return first symbol)
			    char c = event.getCharacter().charAt(0);
			    final KeyboardEvent emulated=new KeyboardEvent(c, shiftPressed,controlPressed,leftAltPressed);
			    consumer.enqueueEvent(emulated);
			    return;
			}
	//	final int _code=code;
	consumer.enqueueEvent(new KeyboardEvent(code, 
						shiftPressed,controlPressed,leftAltPressed));
    }
}
