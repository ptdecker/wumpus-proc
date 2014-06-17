wumpus-proc
===========

A Java version of the classic 1970's console computer game, "Wumpus". Written in a straight procedural style.

## Hunt the Wumpus

Originally written by Gregory Yob in the early 1970s while at Dartmouth, Hunt the Wumpus is an iconic early text-base adventure game.

This version is adapted from the original BASIC program published in "More BASIC Computer Games" from 1979 (although the program itself is much older).  This version adapts the original to Java syntax.  This is a purely procedural implementation not leveraging any object-oriented design principles or language feature.  It is designed to run from the console just as the original would have worked.  As such, it is an illustration of Java syntax but not a particularly good example of a modern object-oriented approach to the problem.  My intent is to simply resurrect the original spirit, then build from that while developing my Java skills.

See:

* [Original Article] (http://www.atariarchives.org/bcc1/showpage.php?page=247)
* [Wikipedia] (http://en.wikipedia.org/wiki/Hunt_the_Wumpus)
* [BSD wump man page] (http://web.archive.org/web/20090214233010/http://linux.die.net/man/6/wump)

## Installing

No build system (make, ant, maven, gradle, etc.) has been utilized yet. I am still working out the rust in my old skills and have not yet taken this on.

For now, to install and get it running you should be able to:

    $ mkdir -p ~/code/wumpus
    $ cd ~/code/wumpus
    $ git init
    $ git remote add origin https://github.com/ptdecker/wumpus-proc.git
    $ git pull origin master
    $ mvn package -Dmaven.compiler.target=1.7 -Dmaven.compiler.source=1.7
    $ ./run.sh

## Support

For support requests, logging found issues, etc., please use the projects [Issue Tracking] (https://github.com/ptdecker/wumpus-proc/issues) system.

## [Contributing] (http://www.hanselman.com/blog/getinvolvedinopensourcetodayhowtocontributeapatchtoagithubhostedopensourceprojectlikecode52.aspx)

At a minumum, please feel free to [create an issue or feature request](https://github.com/ptdecker/wumpus-proc/issues). If you would like to go further, then:

1. Fork it
1. Create your feature branch (`git checkout -b my-new-feature`)
1. Commit your changes (`git commit -am 'Add some feature'`)
1. Push to the branch (`git push origin my-new-feature`)
1. Create a new Pull Request