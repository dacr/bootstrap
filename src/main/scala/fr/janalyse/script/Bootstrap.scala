package fr.janalyse.script

import scala.tools.nsc.ScriptRunner
import scala.tools.nsc.GenericRunnerCommand
import scala.io.Source

object Bootstrap {

  val header = 
"""// WARNING
// Automatically generated file - do not edit !
import sys.process.Process
import sys.process.ProcessBuilder._
 
case class CurDir(cwd:java.io.File)
implicit def stringToCurDir(d:String) = CurDir(new java.io.File(d))
implicit def stringToProcess(cmd: String)(implicit curDir:CurDir) = Process(cmd, curDir.cwd)
implicit def stringSeqToProcess(cmd:Seq[String])(implicit curDir:CurDir) = Process(cmd, curDir.cwd)

implicit var cwd:CurDir=scala.util.Properties.userDir
def cd(dir:String=util.Properties.userDir) = cwd=dir

"""

  val footer = 
"""
"""

  def main(cmdargs:Array[String]) {

    def f(name:String) = new java.io.File(name)
    
    val na = List("-nocompdaemon","-usejavacp","-savecompiled", "-deprecation") ++ cmdargs.toList
    
    val command = new GenericRunnerCommand(na)
    
    import command.settings
    
    val scriptname = command.thingToRun 
    val script = f(scriptname)
    val richerScript = f(scriptname.replaceFirst(".scala", ".scala-plus"))
    
    if (script.exists()) {
      if (!richerScript.exists || (script.lastModified > richerScript.lastModified)) {
        val content=Source.fromFile(script).getLines().toList
        val cleanedContent = content.dropWhile(x => !x.startsWith("!#")).tail.mkString("\n")
        val newcontent =  List(header, cleanedContent, footer).mkString("\n")
        new java.io.FileOutputStream(richerScript) {
          write(newcontent.getBytes())
        }.close()
      }
    }
    
    val args = command.arguments
    
    ScriptRunner.runScript(settings, richerScript.getName, args)
  }
  
}
