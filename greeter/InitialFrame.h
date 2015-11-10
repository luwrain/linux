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

#ifndef GREETER_INITIAL_FRAME_H
#define GREETER_INITIAL_FRAME_H

#include"Frame.h"
#include"Instance.h"

class InitialFrame: public Frame
{
public:
  InitialFrame(Instance& instance, const std::string& introCmd, const StringVector& text)
    : m_instance(instance),
      m_introCmd(introCmd),
      m_text(text) {}

public:
  void introduce()
  {
    m_instance.player.run(m_introCmd);
  }

  void drawScreen()
  {
    const size_t maxLen = maxLength(m_text);
    Screen& screen = m_instance.screen;
    const int width = screen.getWidth();
    const int height = screen.getHeight();
    int x = (width / 2) - (maxLen / 2);
    int y = (height / 2) - (m_text.size() / 2);
    if (x < 0)
      x = 0;
    if (y < 0)
      y = 0;
    screen.begin();
    for (StringVector::size_type i = 0;i < m_text.size();++i)
      screen.put(x, y + i, m_text[i]);
    screen.end();
  }

private: 
  Instance& m_instance;
  const std::string& m_introCmd;
  StringVector m_text;
};

#endif //GREETER_INITIAL_FRAME_H
