#!/bin/bash
function getport()
{
    	case $1 in
		SPU )
			local port=4001 ;;
		UCT )
			local port=4002 ;;
		MC )
			local port=4003 ;;
		AB )
			local port=4004 ;;
	esac
	echo "$port"
}

X=$(date +%s);
FILE="out$X.data";
R=100;
NAME1=$1;
NAME2=$2;

PORT1=$(getport $NAME1)
PORT2=$(getport $NAME2);
START=$3;
PLAY=$4;

echo "$NAME1 $NAME2" >> $FILE
for ((i=0;i<$R;i++)); do
	RES=$(java -jar ./lib/gamecontroller-cli-r466.jar M ./specs/connect4.kif $START $PLAY 1  -remote 1 $NAME1 localhost $PORT1 1 -remote 2 $NAME2 localhost $PORT2 1 | grep 'Game over')
	set -- $RES;
	echo " $NAME1 $5, $NAME2 $6" | tee $FILE  #echo the result to the file and console

	RES=$(java -jar ./lib/gamecontroller-cli-r466.jar M ./specs/connect4.kif $START $PLAY 1  -remote 2 $NAME1 localhost $PORT1 1 -remote 1 $NAME2 localhost $PORT2 1 | grep 'Game over')
	set -- $RES;
	echo "-$NAME1 $5, $NAME2 $6" | tee $FILE  #echo the result to the file and console

done
