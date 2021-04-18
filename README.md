# MEED Projet Informatique : Ultimate Tic-Tac-Toe (algorithmes MCTS et MinMax)

## Informations générales

Ce projet consiste en la réalisation d'une intelligence artificielle qui a pour but de jouer efficacement au Tic-Tac-Toe, et à l'Ultimate Tic Tac Toe (les règles sont présentées [ici](https://en.wikipedia.org/wiki/Ultimate_tic-tac-toe)).

Cette intelligence artificielle a été développée en language Java, à travers deux méthode : le Monte Carlo Tree Search, et l'algorithme MinMax.

## Démarrage et installation

Afin de pouvoir prendre en main le projet, il vous faut déjà télécharger le code source de ce dernier sur [le Github](https://github.com/AntoineGauquier/UltimateTicTacToe).

Une fois téléchargé, il vous faut ouvrir le projet avec un IDE, NetBeans de préférence, étant donné que le projet a été développé sur cet IDE. Vous pouvez le téléchargie [via ce lien](https://github.com/AntoineGauquier/UltimateTicTacToe).

Une fois le projet ouvert, l'ensemble des fichiers source sont accessibles. 

## Principales fonctionnalités

Les fonctionnalités du projet sont divisés en deux parties. Dans tous les cas, afin de choisir l'intelligence artificielle qui convient, il faut renseigner dans [Player.java](https://github.com/AntoineGauquier/UltimateTicTacToe/blob/main/src/tictactoecodingame/Player.java), l'algorithme qu'il faut associer au joueurOrdi.

### Monte Carlo Tree Search

Si vous souhaitez utiliser l'intelligence artificielle développée via la méthode du Monte Carlo Tree Search, il vous faut pour cela définir un joueurOrdi qui a pour algorithme de recherche la méthode MCTS. La classe associée est AlgoRechercheMCTS : 

```
JoueurHumain humain = new JoueurHumain("Humain");
JoueurOrdi IA = new JoueurOrdi("IA");
[...]
AlgoRechercheMCTS ia = new AlgoRechercheMCTS(humain, IA, is9x9, methodeSimulation);
```

Plusieurs paramètres sont à renseigner : humain et IA les deux joueurs, is9x9 est une variable booléenne qui renseigne si le jeu se joue en 3x3 (Tic-Tac-Toe classique), ou en 9x9 (Ultimate Tic-Tac-Toe). Enfin, methodeSimulation indique la méthode de simulation sélectionnée. Il en existe deux versions :


#### Version aléatoire ([partieRecursive](https://github.com/AntoineGauquier/UltimateTicTacToe/blob/671dc78473211e8d72b568ee56843e64af0aa339/src/tictactoecodingame/AlgoRechercheMCTS.java#L39))


En renseignant 0, la méthode utilisée pour la phase de simulation sera partieRecursive, qui va simuler récursivement une partie, en faisant jouer alternativement les deux joueurs passés en paramètres. Des coups sont joués tant que la partie n'est pas finie, et le coup à jouer est pris aléatoirement parmi les coups disponibles pour le joueur dont c'est le tour.

Il est bon de noter que dans cette version "non-améliorée" du MCTS, l'ordinateur s'adapte automatiquement au type de la partie. De ce fait, la variable is9x9 ne jouera aucun rôle dans ce mode, elle sera uniquement utile pour la deuxième méthode de simulation.


#### Version améliorée ([simulationAmelioree](https://github.com/AntoineGauquier/UltimateTicTacToe/blob/671dc78473211e8d72b568ee56843e64af0aa339/src/tictactoecodingame/AlgoRechercheMCTS.java#L60))


En renseignant 1, la méthode utilisée est une version améliorée de la simulation. Cette méthode va, au lieu d'effectuer une simulation des parties aléatoire, choisir les coups les plus prometteurs pour chacun des joueurs. Concrètement, l'algorithme va regarder l'ensemble des coups possibles pour le joueur dont c'est le tour, et identifier les positions associées. Pour chacune des positions, il va regarder le nombre de jetons qui sont alignés avec cette position (horitonzalement, diagonalement ou verticalement) et va stocker cette information.
Cette heuristique va permettre de sélectionner à la fin de l'étude de chacun des coups, celui qui comprendra le plus de jetons alignés, et donc qui est le plus suceptible d'être gagnant.

Cette méthode permet de réduire le nombre de coups joués par partie simulée, et donc, théoriquement, réduire le temps que l'IA va mettre pour effectuer un nombre de tours donné dans l'arbre, ou bien d'augmenter le nombre de tours dans l'arbre dans le cas d'un temps limité.


#### L'algorithme du MCTS en lui-même ([MCTS](https://github.com/AntoineGauquier/UltimateTicTacToe/blob/671dc78473211e8d72b568ee56843e64af0aa339/src/tictactoecodingame/AlgoRechercheMCTS.java#L210))


L'algorithme du MCTS est appelé par la fonction meilleurCoup, elle-même appelé lors d'une nouvelle partie ou d'un tournoi (voir section sur les tests).

Cet algorithme est récursif, et permet de faire les quatre étapes du Monte Carlo Tree Search : Séléction, Expansion, Simulation et Back-propagation.


Son fonctionnement global est le suivant : l'algorithme va sélectionner à chaque niveau de l'arbre, le noeud qu'il faut sélectionner, via un calcul faisant un compromis entre exploitation (efficacité du coup associé au noeud considéré) et exploration (degré d'exploration du noeud par rapport aux autres noeud du même niveau).

Ce processus est répété jusqu'à ce qu'un noeud n'ait plus de fils.

Ici deux possibilités interviennent : soit il s'agit du dernier coup qui pouvait être joué sur le plateau, et dans ce cas on ne peut faire d'expansion. La simulation correspondra alors simplement à regarder le résultat de la partie, et de sauvegarder ce résultat en l'associant au joueur du noeud considéré, avant de remonter ce résultat pour la back-propagation.
Soit la partie n'est pas finie après ce noeud, et il faut expandre ce dernier. On prend alors la liste de tous les coups possibles pour ce joueur, et on créé autant de noeuds que de coups en les associant. On passe ensuite à la simulation, en choisissant au hasard un de ces nouveaux noeuds fils, et en simulant une partie en fonction de la méthode choisie.
En fonction du résultat, on actualise le nombre de victoires et de parties jouées du noeud fils sélectionné aléatoirement et du noeud père, avant de remonter l'information pour back-propagation.

Pour la phase de back-propagation, on regarde à chaque noeud du chemin parcouru par la séléction si le joueur associé au score est le même que le joueur pour qui la partie a été simulée. Si ce n'est pas le cas, on ajoutera le score opposé.

La fonction meilleurCoup va appeler cet algorithme un certain nombre de fois, en fonction de deux critères possibles : un nombre de tours, ou bien un temps donné. Une fois l'une des deux condition choisie et remplie, meilleurCoup va choisir le coup à privilégier compte tenu des itérations faîtes dans l'arbre, en choisissant le ratio entre parties gagnées et parties jouées le plus haut parmi l'ensemble des coups possibles que l'IA peut jouer.


### Algorithme du MinMax

Les algorithmes MinMax implémentés sont disponibles dans les classes AlgoRechercheMinMax3x3 et AlgoRechercheMinMax9x9. Ce sont des classes qui héritent de la classe AlgoRecherche. Pour les utiliser, il suffit de créer une nouvelle instance de classe et d’utiliser le constructeur associé qui nécessite uniquement en paramètre les deux joueurs.

La version MinMax 3x3 utilise une profondeur fixée au départ et qui par conséquent n’a pas de limite de temps. Elle est développée avec une fonction d’évaluation très simplifiée qui retourne une évaluation non nulle uniquement quand la partie est terminée et gagnée. La profondeur à laquelle peut aller l’ordinateur et la facilité du jeu en 3x3 impliquent que cette fonction n’a pas besoin d’être développée davantage pour ne jamais perdre. L’algorithme MinMax en lui-même est amélioré avec l’implémentation de l’élagage Alpha-Beta.

La version MinMax 9x9 quant à elle est adaptée aux règles de temps de CodinGame, la profondeur est incrémentée s’il reste du temps. La méthode MinMax utilise aussi l’élagage Alpha-Beta mais dispose en plus d’une fonction de tri des coups possibles pour mettre en premier les coups les plus susceptibles d’être les meilleurs. La fonction d’évaluation évalue la position en associant un score à chaque sous grille. L’évaluation retournée est calculée par la somme de ces sous-évaluations ainsi que par la moyenne des lignes/colonnes/diagonnales gagnantes supérieur à un certain seuil.

## Tests

Le test des parties peut se faire via deux méthodes principales : soit en local, soit sur une plateforme permettant de confronter son IA aux autres IA, et plus particulièrement dans le cadre de l'Ultimate Tic-Tac-Toe, sur la plateforome [CodinGame](https://www.codingame.com/multiplayer/bot-programming/tic-tac-toe).


### Tests en local 


Les tests en local peuvent se faire de deux façons différentes, soit en jouant en tant qu'humain directement contre l'IA, soit en faisant jouer l'IA contre un ordinateur jouant ses coups aléatoirement, sur un très grand nombre de parties (tournoi).

Dans tous les cas, ces tests sont à effectuer dans le main du projet, présent dans le fichier [Player.java](https://github.com/AntoineGauquier/UltimateTicTacToe/blob/main/src/tictactoecodingame/Player.java).


#### Partie IA vs Humain 


Dans le cas d'une partie jouée contre l'ordinateur, la structure du test est de la forme suivante : 

```
        JoueurHumain humain = new JoueurHumain("Humain");
        JoueurOrdi IA = new JoueurOrdi("IA");
 
        AlgoRecherche[TypeAlgoChoisi] ia = new AlgoRecherche[TypeAlgoChoisi](humain, IA, [Autres paramètres en fonction du type d'algorithme choisi]);
        IA.setAlgoRecherche(ia);
        
        GrilleTicTacToe3x3 grille = new GrilleTicTacToe3x3(); // Remplacer par 9x9 pour jouer en 9x9
        
        Arbitre a = new Arbitre(grille, IA, humain);
        
        a.startNewGame(true);
```

Comme indiqué entre crochets, il faut adapter le test en fonction du type d'algorithme choisi (MCTS ou MinMax).

Une partie sera ensuite lancée, et vous pourrez jouer contre l'IA dans la fenêtre de console, où le plateau sera affiché à chaque coup.


#### Tournoi IA vs Ordinateur aléatoire


Afin de lancer un tournoi entre l'IA et l'ordinateur, il faut comme précédemment, en remplaçant a.startNeGame(true) par :


```       
        a.startTournament(1000, false); 
```

Et en choisissant un ordinateur jouant aléatoirement (via la classe AlgoRechercheAleat).


### Tests sur CodinGame

## Auteurs

Ce projet a été réalisé par Alexandre Lacour, Gaultier Lecadre, Antoine Gauquier et Hugo Dominois, dans le cadre du module d'exploration de domaine "Projet informatique" de l'IMT Lille Douai.

Alexandre et Gaultier sont à l'origine de la partie algorithme du MinMax, tandis qu'Antoine et Hugo sont à l'origine de l'algorithme du MCTS.