 ======================================
|					|
|	1. Run with python script	|
|	2. Run with make		|
|	3. Basic commands		|
|	4. Doc				|
|					|
 ======================================


####### 1. Python script ###########################################

Dans le terminal :

	$ ./run.py

L'anneau est alors en cours d'exécution avec des paramètres par défaut.
Il est également possible de passer des arguments qui sont le port TCP 
et le port UDP pour l'application. Pour cela, dans le terminal :

	$ ./run.py PORT_TCP PORT_UDP

Ainsi, par exemple :

	$./run.py 4800 4600


--------------------------------------------------------------------


###### 2. make #####################################################

Dans le cas où le script python n'est pas supporté, il est possible
de compiler et d'exécuter avec make, dans le terminal :

	$ make

Puis pour lancer l'anneau avec des paramètres par défaut :

	$ make run

Il est également possible de donner les ports TCP et UDP :

	$ make run tcp=4242 udp=4545


--------------------------------------------------------------------


###### 3. commandes basiques #######################################

Une fois l'anneau en cours d'exécution, il est conseillé de taper :

	$ HELP

afin de faire apparaître la liste des commandes disponibles.
Il sera présenté ici les commandes principales.

	$ TCP ip port

	établit une connexion TCP avec l'ip et le port donnée
	afin d'effectuer une insertion dans l'anneau,
	le destinaire peut accepter la connexion. Dans ce cas,
	un message "WELC" est envoyé, sinon il peut la refuser
	auquel cas est envoyé "NOTC". Remarque, il est tout à
	fait possible d'attendre une connexion TCP.


	$ WHOS

	envoie sur l'anneau courant le message WHOS afin de
	connaître les entités présentes sur l'anneau. Si
	l'anneau est dupliquée, alors le message fait le tour
	sur les deux anneaux. En réponse est envoyé un
	message MEMB avec certaines informations.


	$ GBYE
	demande à quitter l'anneau en cours. L'entité courante
	assure son rôle jusqu'à recevoir le message EYBG.


	$ TEST

	envoie un messge TEST sur l'anneau courant. Si au bout
	d'un certain délai (10 secondes par défaut), ce message
	n'est pas reçu, alors DOWN est envoyé en multicast.
	Les entités le recevant se déconnectent alors de l'anneau.


	$ EXIT

	ferme le programme.


--------------------------------------------------------------------


###### DOCUMENTATION ###############################################

Dans le dossier doc, ouvrir depuis un navigateur le fichier index.html :

	$ firefox index.html
