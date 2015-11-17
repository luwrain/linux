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

#ifndef  GREETER_FRAME_H
#define GREETER_FRAME_H

class Frame
{
public:
  virtual ~Frame() {}

public:
  virtual void introduce() = 0;
  virtual void drawScreen() = 0;

protected:
  static size_t maxLength(const StringVector& v)
  {
    size_t res = 0;
    for(StringVector::size_type i = 0;i < v.size();++i)
      if (v[i].length() > res)
	res = v[i].length();
    return res;
  }
};

typedef std::vector<Frame*> FrameVector;

#endif //GREETER_FRAME_H
