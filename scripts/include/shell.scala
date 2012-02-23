
import sys.process.Process
import sys.process.ProcessBuilder._
 
case class CurDir(cwd:java.io.File)
implicit def stringToCurDir(d:String) = CurDir(new java.io.File(d))
implicit def stringToProcess(cmd: String)(implicit curDir:CurDir) = Process(cmd, curDir.cwd)
implicit def stringSeqToProcess(cmd:Seq[String])(implicit curDir:CurDir) = Process(cmd, curDir.cwd)

implicit var cwd:CurDir=scala.util.Properties.userDir
def cd(dir:String=util.Properties.userDir) = cwd=dir

