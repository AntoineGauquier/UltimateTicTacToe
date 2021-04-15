/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoecodingame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author franck.tempet
 */
public class AlgoRechercheMCTS extends AlgoRecherche{
    Random rnd; // Sert à la simulation aléatoire des parties.
    // Permet d'utiliser directement les joueurs de la partie en cours afin de ne pas créer de problèmes au niveau des ID Joueurs.
    Joueur j_humain; 
    Joueur j_ordi;
    boolean is9x9; /* false si la grille est en 3x3 et true si elle est en 9x9 : ce paramètre ne sert que dans le cadre
                      de l'amélioration sur la fonction de simulation des parties, l'algorithme MCTS en lui-même gère
                      parfaitement les deux types de grille sans spécification particulière. */
    int methodeSimulation; // 0 si c'est la simulation aléatoire, 1 pour l'améliorée
    
    // Constructeur permettant l'instanciation d'une IA.
    
    public AlgoRechercheMCTS(Joueur _j1, Joueur _j2, boolean _is9x9, int _methodeSimulation) {
        rnd = new Random(); 
        this.j_humain = _j1;
        this.j_ordi = _j2;
        this.is9x9 = _is9x9;
        this.methodeSimulation = _methodeSimulation;
        
    }
    
    // Fonction permettant de simuler une partie aléatoirement, et ce, récursivement.
    
    public double partieRecursive(Joueur joueur1, Joueur joueur2, Plateau plateau, Joueur initial){
        if(!plateau.partieTerminee()){ // Si la partie n'est pas encore terminée, on doit encore y jouer des coups.
            
            ArrayList<Coup> coups = plateau.getListeCoups(joueur1); // On séléctionne les coups disponible du joueur dont c'est le tour.
            plateau.joueCoup(coups.get(rnd.nextInt(coups.size()))); // On joue un coup parmi les coups disponibles, que l'on choisit aléatoirement (pseudo-aléatoirement).
            
            return partieRecursive(joueur2, joueur1, plateau, initial); // On rappelle la fonction afin de donner la main au joueur suivant.  
        } else {

            if(plateau.partieGagnee()){ // Si la partie est terminée et qu'elle est gagnée (non nulle).
                if(plateau.vainqueur() == initial){ // Si le vainqueur est le joueur initial, alors elle est considérée comme étant gagnée.
                    return 1.0;
                } else { // Si ce n'est pas le joueur initial qui gagne, elle est considérée comme étant perdue.
                    return 0.0;
                } 
            } else { // Si la partie est terminée mais pas gagnée, alors elle se termine en match nul, et on retourne 0.5 points peu importe le joueur initial.
                return 0.5;
            }
        }
    }
    
    public double simulationAmelioree(Joueur j1, Joueur j2, Plateau plateau, Joueur initial, boolean is9x9){
        
        /* La structure de la simulation est la même que la simulation aléatoire : elle fonctionne de manière récursive,
        s'apellant elle-même tant que la partie n'est pas terminée, et évaluant ensuite le score à retourner en fonction 
        de l'issue de la partie et du joueur initial. La principale modification réside dans le choix des coupds joués :
        il ne se fera pas de manière aléatoire contrairement à avant, mais grâce à la maximisartion d'une heuristique, qui
        calcule le nombre de jetons alignés. On choisira donc le coup qui maximise cette heuristique, qui a donc plus de 
        chances de proposer un coup qui terminera la partie plus rapidement.
        
        Il s'avère au final, qu'après analyse (cf présentation orale), que l'efficacité de cette amélioration est discutable.
        En effet, elle permet de résoudre les parties plus rapidement, et donc de boucler plus de fois dans l'arbre, proposant
        ainsi une plus grande précision théorique. Toutefois, un aspect non négligeable ici est la complexité temporelle ajoutée 
        par le choix du coup, qui est plus longue que le choix d'un coup aléatoire. Via l'implémentation faîte ici, pour le 3x3, 
        la version améliorée est légèrement plus performante (en tout cas, sur 2000 simulations effectuées par méthode) que la 
        version aléatoire.
        
        La version 9x9 est en revanche moins performante, voire bien moins performante, mais nous n'avons pas eu le temps de 
        la faire en détail et donc de la peaufiner, ce qui fait que nous l'avons simplement adaptée et étendue au 9x9, sans prendre 
        en compte certaines spécificités. Je pense notamment au fait que systématiquement, on identifie la grille sur laquelle doit
        jouer le joueur et explorons les 9 cases de cette grille afin d'idnetifier les coups possibles et leurs jetons alignés associés.
        Toutefois, il existe des situations, par exemple le tout premier coup sur le plateau, où il y a bien plus de possibilités (81 
        dans ce cas), et ces situations ne sont pas prises en compte, réduisant l'efficacité de l'algorithme. */
        
        if(!plateau.partieTerminee()){ // Si la partie n'est pas terminée
            
            // On stocke tous les coups que le joueur peut jouer.
            
            ArrayList<Coup> coups = plateau.getListeCoups(j1);
            
            /* On créer un tableau qui va contenir le nombre de jetons alignés pour chacun des coups possibles, un indice qui comptera 
            le nombre de coups jouables parmi les 9 cases, et l'indice du coup qui maximise le nombre de jetons alignés. */
            
            int[] nbJetonAligneParCoup = new int[coups.size()];
            int positionsCoupsIndex = 0;
            int maxNbJetonsAlignesIndex = 0;
            
            // Ces deux variables permettent de prendre en compte la position de la sous-grille sur laquelle l'on doit jouer.
            
            int positionColonneGrille;
            int positionLigneGrille;
            
            if(is9x9){ // Si nous sommes dans une configuration 9x9, alors on prend le premier coup de la liste des coups, afin d'identifier la sous-grille associée.
                CoupTicTacToe premierCoup = new CoupTicTacToe(0, 0, new Jeton(j1));
                premierCoup = (CoupTicTacToe)coups.get(0);

                // La position de la sous-grille calculée sera comprise à chaque fois entre 0 et 2 inclus, 0 pour la colonne 1, 1 pour la colonne 2, ... Et identiquement pour la ligne 
                
                positionColonneGrille = premierCoup.getColonne() / 3;
                positionLigneGrille = premierCoup.getLigne() / 3;
            } else { // Si on est dans le cas 3x3, on est dans la seule et unique grille 3x3.
                positionColonneGrille = 0;
                positionLigneGrille = 0;
            }

            // Pour chacune des 9 cases de la sous-grille concernée
            
            for(int i = 0; i < 3; i++){
                for(int j = 0; j < 3; j++){

                    Case caseActuelle = new Case(i + 3 * positionColonneGrille, j + 3 * positionLigneGrille);

                    if(plateau.getPiece(caseActuelle) == null){ // Si la pièce sur la plateau est nulle pour la case considérée, il s'agit d'un coup disponible.
                        
                        /* On va alors compter le nombre de jetons alignés pour ce coup, en regardant dans les deux directions que peut prendre le jeton.
                           La méthode d'identification des jetons alignés est celle qui est utilisée dans la classe GrilleTicTacToe3x3, que nous avons adaptée 
                           pour qu'elle fonctionne également en 9x9. */

                        int[][] dir = {{1, 0}, {1, 1}, {0, 1}, {1, -1}};
                        int[][] dirOps = {{-1, 0}, {-1, -1}, {0, -1}, {-1, 1}};
                        int x = i, y = j, nbJetonAligne = 0;

                        for (int d = 0; d < 4; d++) {
                            nbJetonAligne = 0;
                            x = i + 3 * positionColonneGrille;
                            y = j + 3 * positionLigneGrille;
                                
                                
                            while (x < 3 * (1 + positionColonneGrille) && x >= 3 * positionColonneGrille && y < (1 + positionLigneGrille) && y >= positionLigneGrille) {
                                caseActuelle = new Case(x, y);
                                    
                                if(plateau.getPiece(caseActuelle) != null){
                                    if(plateau.getPiece(caseActuelle).getJoueur() == j1){
                                        nbJetonAligne++;
                                    }
                                }
                                x += dir[d][0];
                                y += dir[d][1];
                            }

                            x = i + 3 * positionColonneGrille;
                            y = j + 3 * positionLigneGrille;
                            nbJetonAligne--;

                            while (x < 3 * (1 + positionColonneGrille) && x >= 3 * positionColonneGrille && y < (1 + positionLigneGrille) && y >= positionLigneGrille) {
                                caseActuelle = new Case(x, y);
                                    
                                if(plateau.getPiece(caseActuelle) != null){
                                    if(plateau.getPiece(caseActuelle).getJoueur() == j1){
                                        nbJetonAligne++;
                                    }
                                }
                                x += dirOps[d][0];
                                y += dirOps[d][1];
                            }
                        }
                        
                        // Une fois le nombre de jetons alignés calculé, on le stocke dans le tableau des nombres de jetons alignés par coup, et on incrément l'indice de position des coups disponibles.

                        nbJetonAligneParCoup[positionsCoupsIndex] = nbJetonAligne;
                        positionsCoupsIndex++;
                    } 
                }
            }
            
            // Une fois tous les coups disponibles analysés, on séléctionne celui qui maximise le nombre de jetons alignés par une recherche linéaire de maximum.
            
            int maxNbJetonsAlignes = 0;
            maxNbJetonsAlignesIndex = 0;

            for(int k = 0; k < coups.size(); k++){
                if(nbJetonAligneParCoup[k] > maxNbJetonsAlignes){
                    maxNbJetonsAlignes = nbJetonAligneParCoup[k];
                    maxNbJetonsAlignesIndex = k;
                }
            }
            
            // On joue ensuite le coup obtenu ...
            
            plateau.joueCoup(coups.get(maxNbJetonsAlignesIndex));
            
            // ... et on passe la main au joueur suivant.
            
            return simulationAmelioree(j2, j1, plateau, initial, is9x9); 
            
        } else { // Si la partie est terminée

            if(plateau.partieGagnee()){ // et si la partie est gagnée
                if(plateau.vainqueur() == initial){ // et si le joueur gagnant est le joueur inital
                    return 1.0; // alors on retourne une victoire
                } else {
                    return 0.0; // sinon, si c'est le joueur opposé, alors c'est une défaite
                } 
            } else {
                return 0.5; // si la partie est terminée mais non-gagnante, il s'agit d'un nul.
            }
        }
    }
    
    // Algorithme du Monte Carlo Tree Search (récursif)
    
    public double[] MCTS(Noeud pere, Plateau plateau, Joueur j1, Joueur j2, double c, boolean is9x9, int methodeSimulation){
        
        if(pere.fils != null){ // Si le père possède des fils, alors on doit encore faire une séléction
            
            // -- PHASE DE SÉLECTION DU CAS GÉNÉRAL -- 
            
            /* On récupère la liste des coups du joueur qui doit jouer. Le noeud père racine correpond 
            et est initialisée dans meilleurCoup comme étant le noeud associé à la position obtenue via le 
            coup de l'humain (ou la position où aucun coup n'est joué, dans le cas du tout premier coup de 
            la partie). De ce fait, c'est au joueur2 de jouer dans tous les cas, puisque le noeud père est 
            associé au joueur dont le coup a amené à la position du noeud en question. */
            
            ArrayList<Coup> coups = plateau.getListeCoups(j2); 
            
            // On va maintenant déterminer le noeud que l'on doit sélectionner, en fonction de l'exploitation et de l'exploration, dont la balance est faîte via un coefficent c.
           
           double maxValue = 0;
           int indexMaxValue = 0;
           double selectionValue = 0;
           
           for(int i = 0; i < coups.size(); i++){ // On parcours chaque noeud fils associé au père
               if(pere.fils[i].nbPartiesJouees == 0){ // Si aucune partie n'a été jouée sur le noeud considéré, alors on lui attribue un score de séléction très élevé (pour éviter une division par 0, tout en le priorisant).
                   selectionValue = 10000;
               } else { // Sinon, on calcule la valeur de séléction, selon la formule donnée dans le cours. Le coefficient c détermine l'importance de l'exploration dans l'arbitrage.
                   selectionValue = ((double)pere.fils[i].nbPartiesGagnees / (double)pere.fils[i].nbPartiesJouees) + (double)c * Math.sqrt(Math.log(pere.nbPartiesJouees) / pere.fils[i].nbPartiesJouees);
               }
               
               if(selectionValue > maxValue){ // On fait une recherche linéaire de maximum en parallèle, afin d'obtenir, à la fin de la boucle, l'indice du noeud à sélectionner.
                   maxValue = selectionValue;
                   indexMaxValue = i;
               }
           }
           
           // Le noeud étant maintenant sélectionné, on joue sur la plateau le coup qui y est associé.
           
           plateau.joueCoup(coups.get(indexMaxValue));
           
           // On appelle ensuite la fonction afin de continuer la phase de séléction, ou de passer aux phases suivantes si le cas d'arrêt est rencontré.
           
           double[] resultatNoeudSuivant = MCTS(pere.fils[indexMaxValue], plateau, j2, j1, c, is9x9, methodeSimulation);
           
           // -- PHASE DE BACK-PROPAGATION DU CAS GÉNÉRAL -- 
           
           // Lorsque le cas d'arrêt est rencontré, le résultat de la phase de simulation sera back-propagé.
           
           
            /* Si le joueur du noeud considéré n'est pas le même joueur que celui qui a fait la simulation de 
               la partie, alors il faut inverser son score, puisqu'il s'agit du joueur opposé (si par exemple
               la simulation est faîte sur un noeud qui correspond à l'IA et que le score correspondant est une victoire, 
               il faut alors la comptabiliser comme étant une défaite pour tous les noeuds ancêtres dont le joueur est le 
               joueur humain). */
      
           if(resultatNoeudSuivant[1] != pere.joueurAssocie){
               
            /* Pour rappel, les scores des parties sont les suivants : 
            
                * 1 si la partie est gagnée
                * 0.5 si elle est nulle
                * 0 si elle est perdue.

            Ainsi, en faisant 1 - score, on obtient la situation opposée (victoire (1) devient défaite (0), et vice-versa, et la nulle reste une nulle). */

               pere.nbPartiesGagnees += 1 - resultatNoeudSuivant[0];
           } else { // Si le joueur de la simualtion est le même que celui du noeud actuel, alors on enregistre le même score.
               pere.nbPartiesGagnees += resultatNoeudSuivant[0];
           }
           
           pere.nbPartiesJouees += 1; // Dans tous les cas et peu importe le score, on incrémente le nombre de partie jouée pour chaque ancêtre.
           
           return resultatNoeudSuivant; // On renvoit le résultat obtenu (le détail de sa composition sera détaillée plus tard) 
                                        // tel qu'on l'a obtenu, permettant ainsi l'alternance entre les joueurs différents dans la 
                                        // considération des scores.
            
        } else { // Si le père ne possède pas de fils, alors la phase de sélection est terminée, et on passe à l'expansion ... 
            
            /* Concernant le résultat, il s'agit d'un tableau de deux valeurs, qui contient : 
                * le résultat de la simulation effectuée sur le noeud étendu sélectionné (1 si victoire, 0.5 si nulle et 0 si dégaite).
                * le joueur associé à la victoire : cela permet lors de la back-propagation de savoir si l'on doit attribuer au noeud le score de la simulation ou son opposé, en comparant le joueur associé à la simulation à celui du noeud (voir définition de la classe Noeud).
            */
            
            double[] resultat = new double[2]; 
            
            /* ... sauf dans un cas : si, à l'issue de la sélection, donc lorsque l'on arrive sur le dernier noeud
            ne possédant pas de fils, la partie se  termine, alors c'est que le dernier coup a été joué sur le plateau 
            lors de la dernière sélection (nous avons atteint une feuille définitive de l'arbre, qui correspond à une partie
            complète). Dans ce cas, il n'y a pas besoin de simuler une partie, puisque nous avons déjà le résultat de cette 
            dernière. */
            
            if(plateau.partieTerminee()){ // Dans le cas où la partie est terminée ...
                if(plateau.partieGagnee()){ // ... si il y a un gagnant ...
                    if(plateau.vainqueur() == j1){ // ... et que le gagnant est le joueur j1, donc le joueur pour lequel la dernière action a conduit à l'état actuel du plateau ...
                        pere.nbPartiesGagnees += 1; // ... alors on augmente son nombre de parties gagnées de 1 ...
                        resultat[0] = 1; // ... et on définit l'issue de la partie à 1.
                    } else { // Sinon, donc si le vainqueur n'est pas le joueur à l'origine du dernier coup, et que la partie est gagnée, alors il a perdu.
                        resultat[0] = 0; // On définit donc uniquement l'issue de la simulation (inutile d'ajouter 0 au nombre de parties gagnées).
                    }
                } else { // Sinon, donc si la partie est finie mais pas gagnée, alors c'est nul et ce peu importe le joueur.
                    pere.nbPartiesGagnees += 0.5;
                    resultat[0] = 0.5;
                }
                
                pere.nbPartiesJouees += 1; // Dans tous les cas, on augmente le nombre de parties jouées de 1.
                resultat[1] = pere.joueurAssocie; // On associe au résultat le joueur associé au noeud (et donc celui à l'issue de la "simulation").
                
                return resultat; // On envoie le résultat aux ancêtres du chemin suivi par la sélection.
                
            } else { // Si on ne se trouve pas dans le cas spécifique du dernier coup d'une partie :
                
               // -- PHASE D'EXTENSION DU CAS D'ARRÊT --
               
               // On séléctionne les coups des futurs fils (coups disponibles pour le joueur à qui c'est le tour de jouer). 
                
               ArrayList<Coup> coups = plateau.getListeCoups(j2);
               
               // On créé autant de nouveaux fils que de coups existants, et on associe à chaque fils un coup, ainsi qu'une identification de joueur opposée (alternance des joueurs).
               
               pere.fils = new Noeud[coups.size()];
               
               for(int i = 0; i < coups.size(); i++){
                   pere.fils[i] = new Noeud();
                   pere.fils[i].coupAssocie = coups.get(i);
                   pere.fils[i].joueurAssocie = 1 - pere.joueurAssocie;
               }
               
               // On choisit, parmi ces fils, un noeud au hasard pour faire la simulation. On joue le coup associé au noeud.
               
               int rndFils = this.rnd.nextInt(coups.size());
               
               plateau.joueCoup(pere.fils[rndFils].coupAssocie);
               
               // -- PHASE DE SIMULATION DU CAS D'ARRÊT --
               
               double resultatSimulation;
               
               // En fonction de la méthode de simulation choisie (1 pour la simulation "améliorée", 0 pour la simulation aléatoire), on appelle la fonction de simulation correspondante.
               
               if(methodeSimulation == 1){
                    resultatSimulation = simulationAmelioree(j1, j2, plateau, j1, is9x9);
               
               } else {
                   resultatSimulation = partieRecursive(j1, j2, plateau, j1);
               }
               
               // -- PHASE de BACKPROPAGATION DU CAS D'ARRÊT --
               
               // On actualise les scores du noeud fils sélectionné aléatoirement et du noeud père, en fonction de l'issue de la simulation. 
               
               /* Ici, comme on a joué un coup du fils, c'est au père de jouer, et donc la simulation sera faite 
                avec pour joueur référent le joueur associé au père. Il faut donc inverser le score obtenu pour le 
                fils sélectionné aléatoirement. */
               
               
               pere.fils[rndFils].nbPartiesGagnees += 1 - resultatSimulation;
               pere.fils[rndFils].nbPartiesJouees += 1;
               
               // On actualise le score du père avec les résultats directs de la simulation.
               
               pere.nbPartiesGagnees += resultatSimulation;
               pere.nbPartiesJouees += 1;
               
               // Le joueur à l'origine de la simulation est le père : il est donc joueur de référence de la simulation.
               
               resultat[0] = resultatSimulation;
               resultat[1] = pere.joueurAssocie;
               
               // On renvoie le résultat aux ancêtres pour la backpropagation.
               
               return resultat;
               
            }
            
        }
        
    }
       
    @Override
    public Coup meilleurCoup(Plateau _plateau, Joueur _joueur, boolean _ponder) {
        
        // On sauvegarde la position actuelle.
        
        _plateau.sauvegardePosition(0);
       
        /* On créé un noeud père, qui est le noeud racine de l'arbre. On définit arbitrairement le joueur associé (
        (0 ou 1, il importe peu de savoir quel joueur correspond à ce nombre, cette information étant déjà portée par 
        j1 et j2 dans l'algorithme. Le tout est de faire alterner cette valeur à chaque niveau de l'arbre. */
        
        Noeud pere = new Noeud();
        pere.joueurAssocie = 0;
        
        // On définit ici c, le coefficient d'arbitrage entre exploration et exploitation. Ce dernier est théroquement optimal pour una valeur égale à sqrt(2).
    
        double c = 10 * Math.sqrt(2);
        double[] resultat = new double[2];
        
        // Conditions de fonctionnement par itération
        
        //int i = 1;
        //int nbTours = 10000;
        
        //while(i <= nbTours){
        
        // Conditions de fonctionnement en mode "compétition" (100 ms pour se décider au maximum).
        
        long startTime = System.currentTimeMillis();
        
        while((System.currentTimeMillis()-startTime) < 100){

            // La valeur résultat ne sert à rien fondamentalement ici, elle sert uniquement pour le bon fonctionnement de l'algorithme en lui même.
            // On restaure le plateau à chaque tour dans le MCTS (Sélection - Expension - Simulation - Backpropagation). 
            
            resultat = MCTS(pere, _plateau, this.j_humain, this.j_ordi, c, this.is9x9, this.methodeSimulation);
            _plateau.restaurePosition(0);
            
            //i++;
        }

        // On doit maintenant choisir le meilleur coup parmi les fils du noeud racine, qui correspondent au coup disponibles pour l'IA.
        
        double maxValue = 0;
        int maxValueIndex = 0;
        
        for(int j = 0; j < pere.fils.length; j++){
            
            double tauxGain = ((double)pere.fils[j].nbPartiesGagnees / (double)pere.fils[j].nbPartiesJouees);

            // On choisirat le coup qui maximise le taux de gain des parties simulées.
            
            if(tauxGain >= maxValue){
                maxValueIndex = j;
                maxValue = tauxGain;
                
            }
        }
        
        // On retourne le coup associé au taux de gain le plus élevé.

        return pere.fils[maxValueIndex].coupAssocie;
    }   
}

// Définition de la classe Noeud, qui va permettre la représentation de la partie sous forme de graphe.

class Noeud {
    // On associe à chaque noeud un nombre de parties jouées et un nombre de parties gagnées, permettant de faire des décisions quant au facteur de séléction, et quant au coup à choisir une fois que l'on souhaite jouer le coup de l'IA.
    double nbPartiesJouees;
    double nbPartiesGagnees;
    // Ce facteur permet d'identifier le joueur associé au noeud. Plus précisément, il permet de savoir si le résultat back-propagé par l'algorithme correspond au score effectué par le même joueur associé, ou bien au joueur opposé.
    int joueurAssocie; 
    // Chaque noeud possède également un coup, qui correspond au coup joué (sur la branche de l'arbre théoriquement), qui mène donc à la position associée au noeud un fois le coup joué.
    Coup coupAssocie;
    // Chaque noeud contient un tableau de noeuds, qui seront les fils de ce noeud père, permettant ainsi de lier les noeuds entre eux et de former un arbre.
    Noeud[] fils;
    
    // Le constructeur permet l'instanciation d'un nouveau noeud, pour lequel on initie les score (parties gagnées et jouées) à 0, ainsi que le coup à null.
    
    public Noeud(){
        this.nbPartiesGagnees = 0;
        this.nbPartiesJouees = 0;
        this.coupAssocie = null;
    }
}