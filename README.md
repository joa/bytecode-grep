Quick and dirty bytecode based search implementation.

Setup:
  sbt compile

Usage:
  scala -cp ~/.ivy2/cache/asm/asm-all/jars/asm-all-3.3.1.jar:target/scala-2.9.1/classes/ Main scala.Option#getOrElse ~/Development/project/target/*.class
  
