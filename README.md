# README: notem

## Quick start:

Requires: Java 1.5+, Ant 1.6+

    $ ant run

You should see something like:

    cwinters@abita:~/github/notem$ ant run
    Buildfile: build.xml
    
    run:
    
    build:
        [mkdir] Created dir: /home/cwinters/Desktop/Projects/Notem/build/classes
        [javac] Compiling 22 source files to /home/cwinters/Desktop/Projects/Notem/build/classes
    ...
          [jar] Building jar: /home/cwinters/Desktop/Projects/Notem/build/notem-0.25.jar
    
    dist:
        [mkdir] Created dir: /home/cwinters/Desktop/Projects/Notem/build/notem-0.25
         [copy] Copying 4 files to /home/cwinters/Desktop/Projects/Notem/build/notem-0.25
        [mkdir] Created dir: /home/cwinters/Desktop/Projects/Notem/build/notem-0.25/lib
         [sync] Copying 22 files to /home/cwinters/Desktop/Projects/Notem/build/notem-0.25/lib
     ...
          [zip] Building zip: /home/cwinters/Desktop/Projects/Notem/build/notem-0.25.zip
    
    deploy:
        [mkdir] Created dir: /home/cwinters/Desktop/Projects/Notem/build/deploy
        [mkdir] Created dir: /home/cwinters/Desktop/Projects/Notem/build/deploy/notes
        [mkdir] Created dir: /home/cwinters/Desktop/Projects/Notem/build/deploy/lib
         [sync] Copying 22 files to /home/cwinters/Desktop/Projects/Notem/build/deploy/lib
    ...
    
    -runapp:
         [java] Running @ http://localhost:8765
         [java] Any key to stop server


# Intro

Notem is a lightweight shared note taking system. It collects text
messages throughout the day and makes them available for browsing and
search. It's not meant as an input to structured documentation, only
as a shared memory, good for things like remembering why certain
design decisions were made, or why alternatives were discarded, or
things to watch out for in future work, or links to useful resources,
or...

- every note is a resource
- resources are identified by date + order
   - when we get a resource, it's easy to fetch the 10 
     before and 10 after
- every note has associated with it:
   - datetime posted
   - poster
   - note
   - order


