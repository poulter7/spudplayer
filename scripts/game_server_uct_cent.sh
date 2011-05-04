#!/bin/bash
X=$(date +%s);
R=10;
for ((i=0;i<$R;i++)); do
	RES=$(java -jar ./lib/gamecontroller-cli-r466.jar M ./specs/connect4.kif 10 15 1  -remote 1 SPU localhost 4001 1 -remote 2 CEN localhost 5000 1 | grep 'Game over')
	set -- $RES;
	echo " SPU $5, CEN $6" >> "out$X.data"  #echo the result to the file
	echo " SPU $5, CEN $6"  #echo the result to console

	RES=$(java -jar ./lib/gamecontroller-cli-r466.jar M ./specs/connect4.kif 10 15 1  -remote 2 SPU localhost 4001 1 -remote 1 CEN localhost 5000 1 | grep 'Game over')
	set -- $RES;
	echo "-SPU $6, CEN $5" >> "out$X.data"  #echo the result to the file
	echo "-SPU $6, CEN $5"  #echo the result to console
done
