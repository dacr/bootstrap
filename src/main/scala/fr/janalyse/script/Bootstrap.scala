/*
 * Copyright 2011 David Crosson
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package fr.janalyse.script

import scala.tools.nsc.ScriptRunner
import scala.tools.nsc.GenericRunnerCommand
import scala.io.Source
import java.io.File

object Bootstrap {
  val defaultOptions = List("-nocompdaemon","-usejavacp","-savecompiled", "-deprecation")
  val defaultExpandedScriptExt = ".pscala"
  
  val includeRE = """\s*#include\s+"([^"]+)"\s*"""r
  
  def expand(file:File, availableIncludes:List[File]) : List[String] = {
    val content=Source.fromFile(file).getLines().toList
    // First we remove "shell" startup lines, everything between #! and !#
    val cleanedContent = content.indexWhere { _.trim.startsWith("!#") } match {
      case -1 => content
      case i  => content.drop(i+1)
    }
	// Then we expand #include directives
	cleanedContent flatMap {
	  case includeRE(filename) =>
	    val fileOpt = availableIncludes find {_.getName() == filename}
	    fileOpt orElse {
	       throw new RuntimeException("%s : Couln't find include file '%s' ".format(file.getName, filename))
	    }
	    fileOpt map { file => expand(file, availableIncludes)} getOrElse List.empty[String]  
	  case line => line::Nil
	}
  }
    
  def main(cmdargs:Array[String]) {
    val command = new GenericRunnerCommand(defaultOptions ++ cmdargs.toList)
    val scriptDir = new File(cmdargs(0)).getParentFile()
    val includePath = List(new File(scriptDir, "include"), scriptDir)
    val availableIncludes = includePath filter {_.exists()} flatMap {_.listFiles()}
    val scriptname = command.thingToRun 
    val script = new File(scriptname)
    val richerScript = new File(scriptname.replaceFirst(".scala", "")+defaultExpandedScriptExt))
    
    if (script.exists()) {
      val jars = util.Properties.javaClassPath.split(File.pathSeparator) map {new File(_)} collect {
        case f if (f.exists() && f.isFile()) => f 
      }
      val jarsLastModified = (jars map {_.lastModified()} max)
      
      if (!richerScript.exists ||  // -- nothing already available
          (jarsLastModified > richerScript.lastModified) ||   // -- Bootstrap jar is newer
          (script.lastModified > richerScript.lastModified)) { // -- Script has been modified
        val newcontent =  expand(script, availableIncludes).mkString("\n")
        new java.io.FileOutputStream(richerScript) {
          write(newcontent.getBytes())
        }.close()
      }
    }
    ScriptRunner.runScript(command.settings, richerScript.getPath, command.arguments)
  }
}
