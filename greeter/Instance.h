
#ifndef GREETER_INSTANCE_H
#define GREETER_INSTANCE_H

#include"Screen.h"
#include"Player.h"

class Instance
{
public:
  Instance()
    : frameNum(0) {}

public:
  Screen screen;
  Player player;
  size_t frameNum;
};

#endif //GREETER_INSTANCE_H
