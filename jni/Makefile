
CXXFLAGS=-O2
SONAME=libluwrainlinux.so
INCLUDES=-I/usr/lib/jvm/java/include -I/usr/lib/jvm/java/include/linux
FLAGS=-Wall -pedantic -fno-rtti -fpic
GPP=g++ -c $(FLAGS) $(CXXFLAGS) $(INCLUDES)
LINK=g++ -shared $(FLAGS) $(CXXFLAGS) -o$(SONAME) $(INCLUDES)

all: $(SONAME)

clean:
	/bin/rm -f *.o *.so

terminal.o: terminal.cpp org_luwrain_os_Terminal.h
	$(GPP) terminal.cpp

$(SONAME): terminal.o
	$(LINK) terminal.o
