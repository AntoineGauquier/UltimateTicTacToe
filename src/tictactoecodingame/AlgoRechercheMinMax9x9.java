package tictactoecodingame;

import java.util.ArrayList;

/* Algorithme MinMax pour grille 9x9

Version MinMax avec élagage Alpha-Beta et tri des coups au préalable, à temps fixé.

Auteurs : Alexandre LACOUR, Gaultier LECADRE
*/

public class AlgoRechercheMinMax9x9 extends AlgoRecherche{
    Joueur joueur1;
    Joueur joueur2;
    
    Joueur joueurAMax;
    
    boolean premierCoup;
    
    /* -----------------------
    CONSTANTES DE LA CLASSE
       ----------------------- */
    
    //Profondeur de départ
    private static final int PROFONDEUR_DE_DEPART = 1;
    
    //Durées accordées
    private static final int DUREE_PREMIER_COUP = 500;
    private static final int DUREE_AUTRES_COUPS = 60;
    
    //Points
    //Evaluation Plateau
    private static final int POINTS_PLATEAU_GAGNE = 10000;
    private static final int POINTS_PLATEAU_NUL = 0;
    //Evaluation sous-grille
    private static final int POINTS_SOUS_GRILLE_GAGNEE = 50;
    private static final int POINTS_SOUS_GRILLE_NULLE = 0;
    private static final int POINTS_LIGNE_PRESQUE_GAGNEE = 4;
    
    //Seuils
    //Evaluation Plateau
    private static final int SEUIL_LIGNE_GAGNANTE = 30;
    //Evaluation rapide
    private static final int SEUIL_NB_VOISIN_POUR_GAIN = 1;
    
    /*Coordonnées des 8 positions gagnantes d'une grille TicTacToe
    3 en ligne
    3 en colonne
    2 en diagonnale
    */
    private static final int[][][] POSITIONS_GAGNANTES = {{{0, 0}, {0, 1}, {0, 2}},
                                                          {{1, 0}, {1, 1}, {1, 2}},
                                                          {{2, 0}, {2, 1}, {2, 2}},
                                                          {{0, 0}, {1, 0}, {2, 0}},
                                                          {{0, 1}, {1, 1}, {2, 1}},
                                                          {{0, 2}, {1, 2}, {2, 2}},
                                                          {{0, 0}, {1, 1}, {2, 2}},
                                                          {{2, 0}, {1, 1}, {0, 2}}};
    
    /* -----------------------
    METHODES GENERALES POUR LE FONCTIONNEMENT DE LA CLASSE
       ----------------------- */
    
    /* CONSTRUCTEUR
    - Initialise joueur1 et joueur2 pour la méthode swapJoueur (l'ordre des paramètres n'est pas important)
    - Initialise premierCoup
    */
    public AlgoRechercheMinMax9x9(Joueur _joueur1, Joueur _joueur2){
        this.joueur1 = _joueur1;
        this.joueur2 = _joueur2;
        premierCoup = true;
    }
    
    
    /* Retoune Joueur différent de _joueur
    */
    Joueur swapJoueur(Joueur _joueur){
        return (_joueur == joueur1) ? joueur2 : joueur1;
    }
    
    /* Méthode meilleurCoup à durée fixée, adaptée pour les règles de CodinGames
    */
    @Override
    public Coup meilleurCoup(Plateau _plateau, Joueur _joueur, boolean _ponder){
        
        long debut = System.currentTimeMillis();
        long dureeCoup = 0;
        
        joueurAMax = _joueur;
        
        int profondeur = PROFONDEUR_DE_DEPART;
        
        int value = Integer.MIN_VALUE;
        int meilleurCoup = 0;
        int evaluation = 0;
        
        ArrayList<Coup> coups = getListeCoupTriee(_plateau, _joueur);
        
        int dureeAccordee = premierCoup ? DUREE_PREMIER_COUP : DUREE_AUTRES_COUPS;
        
        while(System.currentTimeMillis() - debut + dureeCoup*coups.size() < dureeAccordee){
            dureeCoup = System.currentTimeMillis();
            for(int i=0; i<coups.size(); i++){
                _plateau.joueCoup(coups.get(i));
                evaluation = minmax(_plateau, swapJoueur(_joueur), profondeur, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
                if(evaluation > value){
                    value = evaluation;
                    meilleurCoup = i;
                }
                _plateau.annuleDernierCoup();
            }
            dureeCoup = System.currentTimeMillis() - dureeCoup;
            swap(coups, meilleurCoup, 0);
            profondeur++;
        }
        premierCoup = false;
        return coups.get(meilleurCoup);
    }

    /* -----------------------
    PARTIE MIN MAX ET EVALUATION DE POSITION 9x9
       ----------------------- */
    
    /* Min Max avec élagage Alpha Beta et tri des coups les plus probables d'être bons    
    */
    int minmax(Plateau _plateau, Joueur _joueur, int profondeur, boolean maximizingPlayer, int alpha, int beta){
        int value = 0;
        if(_plateau.partieTerminee() || profondeur == 0){
            value = evaluation(_plateau, _joueur) + profondeur; //Permet de prendre le gain le plus rapide
        }
        else{
            ArrayList<Coup> coups = getListeCoupTriee(_plateau, _joueur); //Tri des coups les plus probables d'être bons
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
            else {
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
    
    /* Evaluation d'une position Plateau 9x9
    Si _plateau gagné, retourne +- POINTS_PLATEAU_GAGNE
    Si _plateau nul, retourne POINTS_PLATEAU_NUL
    Sinon retourne évaluation de _plateau avec evaluation définie comme:
    - somme des evaluationSousGrille de chaque sous grille
    - si l'évaluation des scores des sous-grilles en lignes/colonnes/diagonales >= SEUIL_LIGNE_GAGNANTE,
    ajoute la moyenne de ces lignes/colonnes/diagonales
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
        else{
            int evaluation = 0;
            Jeton[][] grille9x9 = new Jeton[9][9];
            for(int i=0; i<9; i++){
                for(int j=0; j<9; j++){
                    grille9x9[i][j] = (Jeton)_plateau.getPiece(new Case(i, j));
                }
            }
            //Préparation des 9 sous-grilles 3x3
            Jeton[][][] grilles3x3 = new Jeton[9][3][3];
            int compteurGrille = 0;
            for(int i=0; i<9; i+=3){
                for(int j=0; j<9; j+=3){
                    for(int a=0; a<3; a++){
                        for(int b=0; b<3; b++){
                            grilles3x3[compteurGrille][a][b] = grille9x9[i+a][j+b];
                        }
                    }
                    compteurGrille++;
                }
            }
            //Evalution de chaque sous-grille
            compteurGrille = 0;
            int[][] scoresGrilles = new int[3][3];
            for(int i=0; i<3; i++){
                for(int j=0; j<3; j++){
                    scoresGrilles[i][j] = evaluationSousGrille(grilles3x3[compteurGrille]);
                    compteurGrille++;
                }
            }
            //Sommes des scores de chaque sous-grille
            for(int i=0; i<3; i++){
                for(int j=0; j<3; j++){
                    evaluation += scoresGrilles[i][j];
                }
            }
            
            /*Si chaque évaluation d'une ligne, d'une colonne ou d'une diagonale gagnante est supérieure à SEUIL_LIGNE_GAGNANTE,
            on ajoute à evaluation la moyenne de ces évaluations
            */
            for(int i=0; i<8; i++){
                if(scoresGrilles[POSITIONS_GAGNANTES[i][0][0]][POSITIONS_GAGNANTES[i][0][1]] >= SEUIL_LIGNE_GAGNANTE &&
                   scoresGrilles[POSITIONS_GAGNANTES[i][1][0]][POSITIONS_GAGNANTES[i][1][1]] >= SEUIL_LIGNE_GAGNANTE &&
                   scoresGrilles[POSITIONS_GAGNANTES[i][2][0]][POSITIONS_GAGNANTES[i][2][1]] >= SEUIL_LIGNE_GAGNANTE){
                   evaluation += (scoresGrilles[POSITIONS_GAGNANTES[i][0][0]][POSITIONS_GAGNANTES[i][0][1]] +
                                  scoresGrilles[POSITIONS_GAGNANTES[i][1][0]][POSITIONS_GAGNANTES[i][1][1]] +
                                  scoresGrilles[POSITIONS_GAGNANTES[i][2][0]][POSITIONS_GAGNANTES[i][2][1]])/3;
                }
                else if(scoresGrilles[POSITIONS_GAGNANTES[i][0][0]][POSITIONS_GAGNANTES[i][0][1]] <= -SEUIL_LIGNE_GAGNANTE &&
                        scoresGrilles[POSITIONS_GAGNANTES[i][1][0]][POSITIONS_GAGNANTES[i][1][1]] <= -SEUIL_LIGNE_GAGNANTE &&
                        scoresGrilles[POSITIONS_GAGNANTES[i][2][0]][POSITIONS_GAGNANTES[i][2][1]] <= -SEUIL_LIGNE_GAGNANTE){
                   evaluation += (scoresGrilles[POSITIONS_GAGNANTES[i][0][0]][POSITIONS_GAGNANTES[i][0][1]] +
                                  scoresGrilles[POSITIONS_GAGNANTES[i][1][0]][POSITIONS_GAGNANTES[i][1][1]] +
                                  scoresGrilles[POSITIONS_GAGNANTES[i][2][0]][POSITIONS_GAGNANTES[i][2][1]])/3;
                }
            }
            return evaluation;
        }
    }
    
    /* Retourne l'évaluation de sousGrille
    Si sousGrille gagnée, retourne +- POINTS_SOUS_GRILLE_GAGNEE
    Si sousGrille nulle, retourne POINTS_SOUS_GRILLE_NULLE
    Sinon évaluation de cette sousGrille en sommant +- POINTS_LIGNE_PRESQUE_GAGNEE fois le nombre de lignes, colonnes
    ou diagonnales presque gagnées (2 jetons sur 3)
    */
    private int evaluationSousGrille(Jeton[][] sousGrille){
        int evaluation = 0;
        // Gain ?
        evaluation = gagnantSousGrille(sousGrille, POINTS_SOUS_GRILLE_GAGNEE);
        if(evaluation != 0){
            return evaluation;
        }
        // Nulle ?
        boolean draw = true;
        for(int i=0; i<3; i++){
            for(int j=0; j<3; j++){
                if(sousGrille[i][j] == null){
                    draw = false;
                }
            }
        }
        if(draw){
            return POINTS_SOUS_GRILLE_NULLE;
        }
        /*Evaluation en comptant les lignes/colonnes/diagonnales presque gagnées
        Pour se faire, on joue dans les cases vides un coup de chaque joueur et on regarde si la sousGrille est gagnée
        */
        for(int i=0; i<3; i++){
            for(int j=0; j<3; j++){
                if(sousGrille[i][j] == null){
                    //joueurAMax joue le coup
                    sousGrille[i][j] = new CoupTicTacToe(i, j, new Jeton(joueurAMax)).getJeton();
                    evaluation += gagnantSousGrille(sousGrille, POINTS_LIGNE_PRESQUE_GAGNEE);
                    //L'autre joueur joue le coup
                    sousGrille[i][j] = new CoupTicTacToe(i, j, new Jeton(swapJoueur(joueurAMax))).getJeton();
                    evaluation += gagnantSousGrille(sousGrille, POINTS_LIGNE_PRESQUE_GAGNEE);
                    sousGrille[i][j] = null;
                }
            }
        }
        return evaluation;
    }
    
    /*Retourne +-score si sousGrille est gagnée par joueurAMax ou pas, 0 sinon
    */
    private int gagnantSousGrille(Jeton[][] sousGrille, int score){
        for(int i=0; i<8; i++){
            if(sousGrille[POSITIONS_GAGNANTES[i][0][0]][POSITIONS_GAGNANTES[i][0][1]] != null &&
               sousGrille[POSITIONS_GAGNANTES[i][1][0]][POSITIONS_GAGNANTES[i][1][1]] != null &&
               sousGrille[POSITIONS_GAGNANTES[i][2][0]][POSITIONS_GAGNANTES[i][2][1]] != null){
                if(sousGrille[POSITIONS_GAGNANTES[i][0][0]][POSITIONS_GAGNANTES[i][0][1]].toString() == sousGrille[POSITIONS_GAGNANTES[i][1][0]][POSITIONS_GAGNANTES[i][1][1]].toString() &&
                   sousGrille[POSITIONS_GAGNANTES[i][0][0]][POSITIONS_GAGNANTES[i][0][1]].toString() == sousGrille[POSITIONS_GAGNANTES[i][2][0]][POSITIONS_GAGNANTES[i][2][1]].toString()){
                    if(sousGrille[POSITIONS_GAGNANTES[i][0][0]][POSITIONS_GAGNANTES[i][0][1]].getJoueur() == joueurAMax){
                        return score;
                    }
                    else{
                        return -score;
                    }
                }
            }
        }
        return 0;
    }
    
    /* -----------------------
    PARTIE TRI RAPIDE DES COUPS POTENTIELLEMENT BONS
       ----------------------- */
    
    /*Retourne la liste des coups possibles en mettant en premier les coups les plus probables d'être efficaces, c'est à dire ceux pour
      qui evalRapide retourne vrai.
    */
    private ArrayList<Coup> getListeCoupTriee(Plateau _plateau, Joueur _joueur){
        int meilleurCoup = 0;
        ArrayList<Coup> coups = _plateau.getListeCoups(_joueur);
        for(int i=0; i<coups.size(); i++){
            if(evalRapide(_plateau, _joueur, (CoupTicTacToe)coups.get(i))){
                swap(coups, i, meilleurCoup);
                meilleurCoup++;
            }
        }
        return coups;
    }
    
    /*Retourne la ArrayList<Coup> coups avec les cases i et j échangées.
    */
    private ArrayList<Coup> swap(ArrayList<Coup> coups, int i, int j){
        Coup temp = coups.get(i);
        coups.set(i, coups.get(j));
        coups.set(j, temp);
        return coups;
    }
    
    /* Calcul du nombre de jetons voisins d'un coup d'un même joueur
    Si nbVoisin >= SEUIL_NB_VOISIN_POUR_GAIN, le coup a des chances d'être bon, evalRapide retourne true, sinon retourne false
    */
    private boolean evalRapide(Plateau _plateau, Joueur _joueur, CoupTicTacToe coup){
        _plateau.joueCoup(coup);
        int colonneDebut = 3*(coup.getColonne()/3);
        int ligneDebut = 3*(coup.getLigne()/3);
        int colonne = coup.getColonne() - colonneDebut;
        int ligne = coup.getLigne() - ligneDebut;
        //Récupération de la sous grille du coup dans grille3x3
        Jeton[][] grille3x3 = new Jeton[3][3];
        for(int i=0; i<3; i++){
            for(int j=0; j<3; j++){
                grille3x3[i][j] = (Jeton)_plateau.getPiece(new Case(i + colonneDebut, j + ligneDebut));
            }
        }
        //Calcul des voisins
        int nbVoisin = 0;
        for(int i=-1; i<=1; i++){
            for(int j=-1; j<=1; j++){
                if(i != 0 && j != 0){
                    nbVoisin += getVoisin(grille3x3, _joueur, colonne + i, ligne + i);
                }
            }
        }
        _plateau.annuleDernierCoup();
        return (nbVoisin >= SEUIL_NB_VOISIN_POUR_GAIN);
    }
    
    /*Retourne 1 si le jeton en position (colonne, ligne) de grille3x3 est un jeton de _joueur, 0 sinon.
    */
    int getVoisin(Jeton[][] grille3x3, Joueur _joueur, int colonne, int ligne){
        if(colonne < 0 || colonne > 2 || ligne < 0 || ligne > 2){
            return 0;
        }
        else{
            if(grille3x3[colonne][ligne] != null && grille3x3[colonne][ligne].getJoueur() == _joueur){
                return 1;
            }
            else{
                return 0;
            }
        }
    }
    
}

