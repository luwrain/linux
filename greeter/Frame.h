
#ifndef  GREETER_FRAME_H
#define GREETER_FRAME_H

class Frame
{
public:
  virtual ~Frame() {}

public:
  virtual void introduce() = 0;
  virtual void drawScreen() = 0;
};

typedef std::vector<Frame*> FrameVector;

#endif //GREETER_FRAME_H
