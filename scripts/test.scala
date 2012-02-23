#!/bin/sh
DIRNAME=`dirname "$0"`
exec java -jar "$DIRNAME"/bootstrap.jar "$0" "$@"
!#

#include "shell.scala"

cd("/etc/")

"ls" #| "grep net" !

