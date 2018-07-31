#!/bin/bash

load=`./access-control loadschema --on | grep -oP '(?<=\[SCHEMA\] )(.*)' `
echo "$load"

cmd1=`./access-control --vertex=organization add --property=name Kofre --property=code 1`
v1=`echo "$cmd1" | grep -oP "(?<=\[CLI\-ADD\] )(.*)"`
echo "$cmd1" | grep -oP "(?<=\[CLI\] )(.*)"
echo "$cmd1" | grep -oP "(?<=\[TRAVERSAL\] )(.*)"
cmd2=`./access-control --vertex=unitOrganization add --property=name Bahia --property=code 1`
v2=`echo "$cmd2" | grep -oP '(?<=\[CLI\-ADD\] )(.*)'`
echo "$cmd2" | grep -oP '(?<=\[CLI\] )(.*)'
echo "$cmd2" | grep -oP '(?<=\[TRAVERSAL\] )(.*)'
e1=`./access-control --edge=has add --source=$(echo $v1) --target=$(echo $v2)` #(v1 -> v2)
echo "$e1" | grep -oP "(?<=\[CLI\] )(.*)"
echo "$e1" | grep -oP "(?<=\[TRAVERSAL\] )(.*)"
cmd3=`./access-control --vertex=group add --property=name Marketing --property=code 1`
v3=`echo "$cmd3" | grep -oP '(?<=\[CLI\-ADD\] )(.*)'`
echo "$cmd3" | grep -oP '(?<=\[CLI\] )(.*)'
echo "$cmd3" | grep -oP '(?<=\[TRAVERSAL\] )(.*)'
e2=`./access-control --edge=has add --source=$(echo $v2) --target=$(echo $v3)` #(v2 -> v3)
echo "$e1" | grep -oP "(?<=\[CLI\] )(.*)"
echo "$e1" | grep -oP "(?<=\[TRAVERSAL\] )(.*)"
cmd4=`./access-control --vertex=accessRule add`
v4=`echo "$cmd4" | grep -oP '(?<=\[CLI\-ADD\] )(.*)'`
echo "$cmd4" | grep -oP '(?<=\[CLI\] )(.*)'
echo "$cmd4" | grep -oP '(?<=\[TRAVERSAL\] )(.*)'
e3=`./access-control --edge=provide add --source=$(echo $v4) --target=$(echo $v3)` #(v4 -> v3)
echo "$e3" | grep -oP "(?<=\[CLI\] )(.*)"
echo "$e3" | grep -oP "(?<=\[TRAVERSAL\] )(.*)"
cmd5=`./access-control --vertex=user add --property=name User1 --property=code 1`
v5=`echo "$cmd5" | grep -oP "(?<=\[CLI\-ADD\] )(.*)"`
echo "$cmd5" | grep -oP "(?<=\[CLI\] )(.*)"
echo "$cmd5" | grep -oP "(?<=\[TRAVERSAL\] )(.*)"
e4=`./access-control --edge=associated add --source=$(echo $v5) --target=$(echo $v4)` #(v5 -> v4)
echo "$e4" | grep -oP "(?<=\[CLI\] )(.*)"
echo "$e4" | grep -oP "(?<=\[TRAVERSAL\] )(.*)"
cmd6=`./access-control --vertex=accessGroup add --property=name Administration --property=code 1`
v6=`echo "$cmd6" | grep -oP "(?<=\[CLI\-ADD\] )(.*)"`
echo "$cmd6" | grep -oP "(?<=\[CLI\] )(.*)"
echo "$cmd6" | grep -oP "(?<=\[TRAVERSAL\] )(.*)"
e5=`./access-control --edge=own add --source=$(echo $v4) --target=$(echo $v6)`  #(v4 -> v6)
echo "$e5" | grep -oP "(?<=\[CLI\] )(.*)"
echo "$e5" | grep -oP "(?<=\[TRAVERSAL\] )(.*)"
cmd7=`./access-control --vertex=rule add --property=name remove_user --property=code 1`
v7=`echo "$cmd7" | grep -oP "(?<=\[CLI\-ADD\] )(.*)"`
echo "$cmd7" | grep -oP "(?<=\[CLI\] )(.*)"
echo "$cmd7" | grep -oP "(?<=\[TRAVERSAL\] )(.*)"
cmd8=`./access-control --vertex=rule add --property=name add_user --property=code 2`
v8=`echo "$cmd8" | grep -oP "(?<=\[CLI\-ADD\] )(.*)"`
echo "$cmd8" | grep -oP "(?<=\[CLI\] )(.*)"
echo "$cmd8" | grep -oP "(?<=\[TRAVERSAL\] )(.*)"
cmd9=`./access-control --vertex=rule add --property=name edit_user --property=code 3`
v9=`echo "$cmd9" | grep -oP "(?<=\[CLI\-ADD\] )(.*)"`
echo "$cmd9" | grep -oP "(?<=\[CLI\] )(.*)"
echo "$cmd9" | grep -oP "(?<=\[TRAVERSAL\] )(.*)"
e6=`./access-control --edge=add add --source=$(echo $v6) --target=$(echo $v7)` #(v6 -> v7)
echo "$e6" | grep -oP "(?<=\[CLI\] )(.*)"
echo "$e6" | grep -oP "(?<=\[TRAVERSAL\] )(.*)"
e7=`./access-control --edge=add add --source=$(echo $v6) --target=$(echo $v8)` #(v6 -> v8)
echo "$e7" | grep -oP "(?<=\[CLI\] )(.*)"
echo "$e7" | grep -oP "(?<=\[TRAVERSAL\] )(.*)"
e8=`./access-control --edge=add add --source=$(echo $v6) --target=$(echo $v9)` #(v6 -> v9)
echo "$e8" | grep -oP "(?<=\[CLI\] )(.*)"
echo "$e8" | grep -oP "(?<=\[TRAVERSAL\] )(.*)"


cmd10=`./access-control --vertex=accessGroup add --property=name Operator --property=code 2`
v10=`echo "$cmd10" | grep -oP "(?<=\[CLI\-ADD\] )(.*)"`
echo "$cmd10" | grep -oP "(?<=\[CLI\] )(.*)"
echo "$cmd10" | grep -oP "(?<=\[TRAVERSAL\] )(.*)"
e9=`./access-control --edge=inherit add --source=$(echo $v6) --target=$(echo $v10)`  #(v6 -> v10)
echo "$e9" | grep -oP "(?<=\[CLI\] )(.*)"
echo "$e9" | grep -oP "(?<=\[TRAVERSAL\] )(.*)"
cmd11=`./access-control --vertex=rule add --property=name view_user --property=code 4`
v11=`echo "$cmd11" | grep -oP "(?<=\[CLI\-ADD\] )(.*)"`
echo "$cmd11" | grep -oP "(?<=\[CLI\] )(.*)"
echo "$cmd11" | grep -oP "(?<=\[TRAVERSAL\] )(.*)"
e10=`./access-control --edge=add add --source=$(echo $v10) --target=$(echo $v11)`  #(v10 -> v11)
echo "$e9" | grep -oP "(?<=\[CLI\] )(.*)"
echo "$e9" | grep -oP "(?<=\[TRAVERSAL\] )(.*)"
cmd12=`./access-control --vertex=user add --property=name User2 --property=code 2`
v12=`echo "$cmd12" | grep -oP "(?<=\[CLI\-ADD\] )(.*)"`
echo "$cmd12" | grep -oP "(?<=\[CLI\] )(.*)"
echo "$cmd12" | grep -oP "(?<=\[TRAVERSAL\] )(.*)"
cmd13=`./access-control --vertex=accessRule add`
v13=`echo "$cmd13" | grep -oP "(?<=\[CLI\-ADD\] )(.*)"`
echo "$cmd13" | grep -oP "(?<=\[CLI\] )(.*)"
echo "$cmd13" | grep -oP "(?<=\[TRAVERSAL\] )(.*)"
e11=`./access-control --edge=associated add --source=$(echo $v12) --target=$(echo $v13)`  #(v12 -> v13)
echo "$e11" | grep -oP "(?<=\[CLI\] )(.*)"
echo "$e11" | grep -oP "(?<=\[TRAVERSAL\] )(.*)"
e12=`./access-control --edge=provide add --source=$(echo $v13) --target=$(echo $v1)`  #(v13 -> v1)
echo "$e12" | grep -oP "(?<=\[CLI\] )(.*)"
echo "$e12" | grep -oP "(?<=\[TRAVERSAL\] )(.*)"
e13=`./access-control --edge=own add --source=$(echo $v13) --target=$(echo $v6)`  #(v13 -> v6)
echo "$e13" | grep -oP "(?<=\[CLI\] )(.*)"
echo "$e13" | grep -oP "(?<=\[TRAVERSAL\] )(.*)"

