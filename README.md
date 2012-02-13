# bytecode-grep
Quick and dirty bytecode based search implementation.

## Setup
```sbt compile```

## Usage
```bytecode-grep scala.Option#getOrElse ~/Development/project/target/*.class```

## Why?
Imagine you switched from scala.actor.Future to akka.dispatch.Future and have a lot of blocking code like this:

```
val x = future { ... }
x()
```

This is all no problem with Scala's default Future but when using the Akka version you will runtime exceptions because
you should not use the ```apply``` method in this context. For whatever reason scalac decides to compile this without complaining. 

If you are in the same situation and need to figure out where ```apply``` is called but your IDE is not capable of finding
all the usages then this tool might help you. Simply invoke it with ```bytecode-grep akka.dispatch.Future#apply *.class```.

