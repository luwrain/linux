
CXXFLAGS=-O2
SONAME=libluwrainlinux.so
INCLUDES=-I/usr/lib/jvm/java/include -I/usr/lib/jvm/java/include/linux -I/usr/lib/jvm/java-8-openjdk-amd64/include -I/usr/lib/jvm/java-8-openjdk-amd64/include/linux -I/usr/lib/jvm/java-8-openjdk/include -I/usr/lib/jvm/java-8-openjdk/include/linux
FLAGS=-Wall -pedantic -fno-rtti -fpic
GPP=g++ -c $(FLAGS) $(CXXFLAGS) $(INCLUDES)
LINK=g++ -shared $(FLAGS) $(CXXFLAGS) -o$(SONAME) $(INCLUDES)

all: $(SONAME)

clean:
	/bin/rm -f *.o *.so

pt.o: pt.cpp org_luwrain_linux_term_PT.h
	$(GPP) pt.cpp

prgroup.o: prgroup.cpp org_luwrain_linux_ProcessGroup.h
	$(GPP) prgroup.cpp

$(SONAME): prgroup.o pt.o
	$(LINK) prgroup.o pt.o
