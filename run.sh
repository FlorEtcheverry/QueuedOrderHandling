#!/bin/bash
# 1:loops from client
# 2:number of OrderManagers
# 3:number of StockControllers
# 4:number of StateControllers
# 5:number of OrderDeliverers
# 6:number of StockSuppliers
# 7:loops from empleado
# 8:sleep for empleado
# 9:cant de ids a generar

if [ $# -eq 0 ];
then echo "
	# 1:loops from client
	# 2:sleep for client (millis)
	# 3:number of OrderManagers 
	# 4:number of StockControllers 
	# 5:number of StateControllers 
	# 6:number of OrderDeliverers 
	# 7:number of StockSuppliers 
	# 8:loops from empleado 
	# 9:sleep for empleado (millis)
	#10:cant de ids a generar"
	exit
fi

export CP=bin/:src/libs/commons-io-1.2.jar:src/libs/commons-cli-1.1.jar:src/libs/rabbitmq-client.jar

#lanzo procesos de la aplicacion
#OrderManager
for (( c=1; c <= $3;c++ ))
do
	java -cp $CP ordersmanagement.OrderManager &
done
#Logger
java -cp $CP ordersmanagement.Logger &
#StockController
for (( c=1; c<=$4;c++ ))
do
	java -cp $CP stockmanagement.StockController &
done
#StateController
for (( c=1; c<=$5;c++ ))
do
	java -cp $CP ordersmanagement.OrderStateController &
done
#OrderDeliverer
for (( c=1; c<=$6;c++ ))
do
	java -cp $CP ordersmanagement.OrderDeliverer &
done
#StockSupplier
for (( c=1; c<=$7;c++ ))
do
	java -cp $CP stockmanagement.StockSupplier &
done

#procesos para simulacion
#generador
java -cp $CP simulacion.UUIDsGenerator ${10}
#cliente pedido
java -cp $CP simulacion.Cliente $1 $2 &
#update empleado
java -cp $CP simulacion.Empleado $8 $9 &

