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

#ifndef GREETER_SCREEN_H
#define GREETER_SCREEN_H

class Screen
{
public:
  virtual ~Screen() {close();}

public:
  void init()
  {
    initscr();
    //    init_pair(1, COLOR_WHITE, COLOR_BLACK);
    //  start_color();
    curs_set(FALSE);
    noecho();
    cbreak();
    keypad(stdscr, TRUE);
    ::clear();
  }

  void close()
  {
    endwin();
  }

  void begin()
  {
    ::clear();
  }

  void end()
  {
    ::refresh();
  }

  void put(int x, int y, const std::string& str)
  {
    mvprintw(y, x, str.c_str());
  }

  int getWidth()
  {
    int x, y;
    getmaxyx(stdscr, y, x);
    return x + 1;
  }

  int getHeight()
  {
    int x, y;
    getmaxyx(stdscr, y, x);
    return y + 1;
  }
};

#endif //GREETER_SCREEN_H
