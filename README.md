

## Linux-dependent code for LUWRAIN

See~also:

* [LUWRAIN main repo](https://github.com/luwrain/luwrain.git)
* [LUWRAIN website](http://luwrain.org/?lang=en)

### Files copying  rules

To make the behaviour  of files copying procedure
more predictable, impose three simple rules, which the corresponding  algorithm must
always follow:

* Only regular files may be overwritten if they are exist
* Symlinks are always copied as symlinks (their copying never results in creating of regular files or directories)
* Files of other types than regular files, directories or symlinks are </li>
* The source files may not be given by relative pathes
* If the destination is given by a relative pathe, the parent of the first source must be used to resolve it
