
# Linux-dependent components

## Files copying  rules

To make the behaviour  of files copying procedure
more predictable, we suggest imposing of three simple rules, which the corresponding  algorithm must
follow:

* Only regular files may be overwritten, if they are exist
* Symlinks are always copied as symlinks (their copying never results in creating of regular files or directories)
* Files of other types than regular files, directories or symlinks are  silently skipped
* The source files may not be given by relative pathes
* If the destination is given by a relative pathe, the parent of the first source must be used to resolve it


This repository is a part of the LUWRAIN project.
LUWRAIN is a platform for the creation of apps for the blind and partially-sighted.
Please visit [luwrain.org](http://luwrain.org/?lang=en) for further information.

