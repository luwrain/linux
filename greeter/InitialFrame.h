
#ifndef GREETER_INITIAL_FRAME_H
#define GREETER_INITIAL_FRAME_H

#include"Frame.h"
#include"Instance.h"

class InitialFrame: public Frame
{
public:
 InitialFrame(Instance& instance, const std::string& introCmd)
   : m_instance(instance),
    m_introCmd(introCmd) {}

public:
  void introduce()
  {
    m_instance.player.run(m_introCmd);
  }

  void drawScreen()
  {
    Screen& screen = m_instance.screen;
  screen.begin();
  screen.put(0, 0, "Добро пожаловать в LUWRAIN");
  screen.end();


  }

 private: 
Instance& m_instance;
  const std::string& m_introCmd;
};

#endif //GREETER_INITIAL_FRAME_H
