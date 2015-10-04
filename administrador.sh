if [ $# -eq 0 ];
then echo "
	# 1:tipo de producto
	# 2:stock a agregar"
	exit
fi

export CP=bin/:src/libs/commons-io-1.2.jar:src/libs/commons-cli-1.1.jar:src/libs/rabbitmq-client.jar

#Administrador
java -cp $CP simulacion.Administrador $1 $2
