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
rm args.txt
for i in $@ ;
do echo $i >> args.txt;
done

if [ $# -eq 0 ];
then echo "
	# 1:loops from client: n -Va a hacer este n^2 (crea n pedidos, consulta uno, vuelve a crear n, eso n veces)
	# 2:number of OrderManagers 
	# 3:number of StockControllers 
	# 4:number of StateControllers 
	# 5:number of OrderDeliverers 
	# 6:number of StockSuppliers 
	# 7:loops from empleado 
	# 8:sleep for empleado 
	# 9:cant de ids a generar"
	exit
fi
declare -a array
let i=0
while read line
do
	array[$i]=$line
	((i++))
done <  args.txt
export CP=.:commons-io-1.2.jar:commons-cli-1.1.jar:rabbitmq-client.jar

#Compilar:
javac -cp rabbitmq-client.jar *.java

#lanzo procesos de la aplicacion
#OrderManager
for (( c=1; c <= ${array[1]};c++ ))
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
#generador
java -cp $CP UUIDsGenerator ${array[8]} 
#cliente pedido
java -cp $CP Cliente ${array[0]} &
#update empleado
java -cp $CP Empleado ${array[6]} ${array[7]} &

