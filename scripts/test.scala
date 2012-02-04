#!/bin/sh
exec java -jar bootstrap.jar "$0" "$@"
!#

cd("/etc/")

"ls" #| "grep net" !

