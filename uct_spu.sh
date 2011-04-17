#!/bin/bash
X=$(date +%s);
R=200;
for ((i=0;i<$R;i++)); do
	RES=$(java -jar ./lib/gamecontroller-cli-r466.jar M ./specs/connect4.kif 30 20 1  -remote 1 UCT localhost 4002 1 -remote 2 SPU localhost 4001 1 | grep 'Game over')
	set -- $RES;
	echo " UCT $5, SPU $6" >> "out$X.data"  #echo the result to the file
	echo " UCT $5, SPU $6"  #echo the result to console

	RES=$(java -jar ./lib/gamecontroller-cli-r466.jar M ./specs/connect4.kif 30 20 1  -remote 2 UCT localhost 4002 1 -remote 1 SPU localhost 4001 1 | grep 'Game over')
	set -- $RES;
	echo "-UCT $6, SPU $5" >> "out$X.data"  #echo the result to the file
	echo "-UCT $6, SPU $5"  #echo the result to console
done
