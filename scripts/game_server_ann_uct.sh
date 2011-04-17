#!/bin/bash
X=$(date +%s);
R=10;
for ((i=0;i<$R;i++)); do
	RES=$(java -jar ./lib/gamecontroller-cli-r466.jar M ./specs/tictactoe.kif 10 5 1  -remote 1 ANN localhost 4002 1 -remote 2 UCT localhost 4001 1 | grep 'Game over')
	echo $RES;		   #echo the result to the console
	set -- $RES;
	echo "ANN $5, UCT $6" >> "out$X.data"  #echo the result to the file
done

for ((i=0;i<$R;i++)); do
	RES=$(java -jar ./lib/gamecontroller-cli-r466.jar M ./specs/tictactoe.kif 10 5 1  -remote 2 ANN localhost 4002 1 -remote 1 UCT localhost 4001 1 | grep 'Game over')
	echo $RES;		   #echo the result to the console
	set -- $RES;
	echo "ANN $6, UCT $5" >> "out$X.data"  #echo the result to the file
done
