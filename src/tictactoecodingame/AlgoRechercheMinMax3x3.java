package tictactoecodingame;

import java.util.ArrayList;

/* Algorithme MinMax pour grille 3x3

Version MinMax avec élagage Alpha-Beta, sans tri des coups au préalable, à profondeur fixée.

La fonction d'évaluation juge une position uniquement si celle-ci est gagnée.
C'est suffisant pour le 3x3.

Auteurs : Alexandre LACOUR, Gaultier LECADRE
*/


public class AlgoRechercheMinMax3x3 extends AlgoRecherche{
    Joueur joueur1;
    Joueur joueur2;
    
    Joueur joueurAMax;
    
    /* -----------------------
    CONSTANTES DE LA CLASSE
       ----------------------- */
    
    //Profondeur de départ
    private static final int PROFONDEUR_DE_RECEHRCHE = 5;
    
    //Points
    private static final int POINTS_PLATEAU_GAGNE = 10000;
    private static final int POINTS_PLATEAU_NUL = 0;
    private static final int POINTS_PLATEAU_INDERTERMINE = 0;
    
    /* -----------------------
    METHODES GENERALES POUR LE FONCTIONNEMENT DE LA CLASSE
       ----------------------- */
    
    /* CONSTRUCTEUR
    - Initialise joueur1 et joueur2 pour la méthode swapJoueur (l'ordre des paramètres n'est pas important)
    */
    public AlgoRechercheMinMax3x3(Joueur _joueur1, Joueur _joueur2){
        this.joueur1 = _joueur1;
        this.joueur2 = _joueur2;
    }
    
    /* Retoune Joueur différent de _joueur
    */
    Joueur swapJoueur(Joueur _joueur){
        return (_joueur == joueur1) ? joueur2 : joueur1;
    }
    
    /* Méthode meilleurCoup à profondeur fixée, sans contrôle de temps
    */
    @Override
    public Coup meilleurCoup(Plateau _plateau, Joueur _joueur, boolean _ponder) {
        
        ArrayList<Coup> coups = _plateau.getListeCoups(_joueur);
        
        joueurAMax = _joueur;
        
        int profondeur = PROFONDEUR_DE_RECEHRCHE;
        
        int value = Integer.MIN_VALUE;
        int meilleurCoup = 0;
        
        for(int i=0; i<coups.size(); i++){
            _plateau.joueCoup(coups.get(i));
            int evaluation = minmax(_plateau, swapJoueur(_joueur), profondeur, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
            if(evaluation > value){
                value = evaluation;
                meilleurCoup = i;
            }
            _plateau.annuleDernierCoup();
        }
        return coups.get(meilleurCoup);
    }
    
    /* -----------------------
    PARTIE MIN MAX ET EVALUATION DE POSITION 3x3
       ----------------------- */
    
    /* Min Max avec élagage Alpha Beta
    */
    int minmax(Plateau _plateau, Joueur _joueur, int profondeur, boolean maximizingPlayer, int alpha, int beta){
        int value;
        
        ArrayList<Coup> coups = _plateau.getListeCoups(_joueur);
        
        if(_plateau.partieTerminee() || profondeur == 0){
            value = evaluation(_plateau, _joueur) + profondeur; //Permet de prendre le gain le plus rapide
        }
        else{
            if(maximizingPlayer){
                value = Integer.MIN_VALUE;
                for(int i=0; i<coups.size(); i++){
                    _plateau.joueCoup(coups.get(i));
                    value = Math.max(value, minmax(_plateau, swapJoueur(_joueur), profondeur - 1, false, alpha, beta));
                    _plateau.annuleDernierCoup();
                    if(value >= beta)
                        return value;
                    alpha = Math.max(alpha, value);
                }
            }
            else{
                value = Integer.MAX_VALUE;
                for(int i=0; i<coups.size(); i++){
                    _plateau.joueCoup(coups.get(i));
                    value = Math.min(value, minmax(_plateau, swapJoueur(_joueur), profondeur - 1, true, alpha, beta));
                    _plateau.annuleDernierCoup();
                    if(alpha >= value)
                        return value;
                    beta = Math.min(beta, value);
                }
            }
        }
        return value;
    }
    
    /* Evaluation d'une position Plateau 3x3
    Si _plateau gagné, retourne +- POINTS_PLATEAU_GAGNE
    Si _plateau nul, retourne POINTS_PLATEAU_NUL
    Sinon retourne POINTS_PLATEAU_INDERTERMINE
    */
    int evaluation(Plateau _plateau, Joueur _joueur){
       if(_plateau.partieTerminee()){
            if(_plateau.partieNulle()){
                return POINTS_PLATEAU_NUL;
            }
            else if(_plateau.vainqueur() == joueurAMax){
                return POINTS_PLATEAU_GAGNE;
            }
            else{
                return -POINTS_PLATEAU_GAGNE;
            }
        }
        return POINTS_PLATEAU_INDERTERMINE;
   }
}

