object Main {
 	import java.io.InputStream

 	private[this] lazy val Extract = """(.+?)#(.+)""".r

  def main(args: Array[String]) {
  	if(args.length < 2) {
  		println("usage: foo.Bar#baz directory0 directory1 ... directoryN")
  		sys.exit(1)
  	}

  	args(0) match {
  		case Extract(owner, name) =>
  			val f = grep(owner.replace('.', '/'), name) _
  			(args drop 1).par filter { _ endsWith ".class" } foreach f
  		case other =>
  			Console.err.println("\""+other+"\" is not of format package.Class#method.")
  			sys.exit(2)
  	}
  }

  def grep(owner: String, name: String)(file: String) {
  	import java.io.FileInputStream

  	val inputStream =
	 		try {
		  	Right(new FileInputStream(file))
		  } catch {
		  	case unexpected => Left(unexpected)
		  }

		@inline
		def using[U](inputStream: InputStream)(f: InputStream => U) =
			try {
				f(inputStream)
			} finally {
				try {
					inputStream.close()
				} catch { case _ => }
			}

  	inputStream.fold(throw _, 
	  	x => 
		  	using(x) {
			  	input => visit(owner, name, input, file)
			  }
		)
  }

  def visit(ownerToLookFor: String, nameToLookFor: String, inputStream: InputStream, file: String) {
  	import org.objectweb.asm._
  	import org.objectweb.asm.commons._
  	import ClassReader.{SKIP_FRAMES => SkipFrames}

  	val classReader = new ClassReader(inputStream)
  	var numOccurrences = 0

  	classReader.accept(new EmptyVisitor {
  		override def visitMethod(access: Int, name: String, desc: String, signature: String, exceptions: Array[String]) = {
  			new EmptyVisitor {
  				override def visitMethodInsn(opcode: Int, owner: String, name: String, desc: String) {
  					if(ownerToLookFor.equals(owner) && nameToLookFor.equals(name)) {
  						numOccurrences += 1
  					}
  				}
  			}
  		}
	  }, SkipFrames)

	  if(numOccurrences > 0) {
	  	println("Found "+numOccurrences+" occurrence"+(if(numOccurrences == 1) "" else "s")+" in "+file)
	  }
  }
}
