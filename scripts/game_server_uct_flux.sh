#!/bin/bash
X=$(date +%s);
R=10;
for ((i=0;i<$R;i++)); do
	RES=$(java -jar ./lib/gamecontroller-cli-r466.jar M ./specs/connect4.kif 10 15 1  -remote 1 UCT localhost 4002 1 -remote 2 FLU localhost 8000 1 | grep 'Game over')
	set -- $RES;
	echo " UCT $5, FLU $6" >> "out$X.data"  #echo the result to the file
	echo " UCT $5, FLU $6"  #echo the result to console

	RES=$(java -jar ./lib/gamecontroller-cli-r466.jar M ./specs/connect4.kif 10 15 1  -remote 2 UCT localhost 4002 1 -remote 1 FLU localhost 8000 1 | grep 'Game over')
	set -- $RES;
	echo "-UCT $6, FLU $5" >> "out$X.data"  #echo the result to the file
	echo "-UCT $6, FLU $5"  #echo the result to console
done
