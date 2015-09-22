#!/bin/bash
# 1:loops from client
# 2:number of OrderManagers
# 3:number of StockControllers
# 4:number of StateControllers
# 5:number of OrderDeliverers
# 6:number of StockSuppliers
# 7:loops from query
# 8:sleep for query
# 9:loops from empleado
# 10:sleep for empleado

declare -a array
let i=0
while read line
do
	array[$i]=$line
	((i++))
done <args.txt

export CP=.:commons-io-1.2.jar:commons-cli-1.1.jar:rabbitmq-client.jar

#Compilar:
javac -cp rabbitmq-client.jar *.java

#lanzo procesos de la aplicacion
#OrderManager
for (( c=1; c<=${array[1]};c++ ))
do
	java -cp $CP OrderManager &
done
#Logger
java -cp $CP Logger &
#StockController
for (( c=1; c<=${array[2]};c++ ))
do
	java -cp $CP StockController &
done
#StateController
for (( c=1; c<=${array[3]};c++ ))
do
	java -cp $CP OrderStateController &
done
#OrderDeliverer
for (( c=1; c<=${array[4]};c++ ))
do
	java -cp $CP OrderDeliverer &
done
#StockSupplier
for (( c=1; c<=${array[5]};c++ ))
do
	java -cp $CP StockSupplier &
done

#procesos para simulacion
#cliente pedido
java -cp $CP Cliente ${array[0]} &
#queries
java -cp $CP ClienteConsulta ${array[6]} ${array[7]} &
#update empleado
java -cp $CP Empleado ${array[8]} ${array[9]} &

