package tictactoecodingame;

/* Test méthodes MinMax

Tests 3x3 et 9x9 : (mettre/enlever commentaires)
- 1 partie Humain vs IA
- 1 partie Aléatoire vs IA
- 50 parties Aléatoire vs IA

Auteurs : Alexandre LACOUR, Gaultier LECADRE
*/

public class Player {

    public static void main(String args[]) {
       
        /* -----------------------
            TEST TICTACTOE 3x3
            ----------------------- */
        //GrilleTicTacToe3x3 grille = new GrilleTicTacToe3x3();

        //Partie 3x3 Humain vs IA Min Max
        /*JoueurHumain humain = new JoueurHumain("Humain"); 
        JoueurOrdi joueurOrdi = new JoueurOrdi("Ordi");
        AlgoRechercheMinMax3x3 minMax  = new AlgoRechercheMinMax3x3(humain, joueurOrdi);
        joueurOrdi.setAlgoRecherche(minMax);
        Arbitre a = new Arbitre(grille, humain, joueurOrdi);
        a.startNewGame(true);*/

        //Partie 3x3 Aléatoire vs IA Min Max
        /*JoueurOrdi joueurAlea = new JoueurOrdi("Alea");
        JoueurOrdi joueurOrdi = new JoueurOrdi("Ordi");
        AlgoRechercheAleatoire algoAlea  = new AlgoRechercheAleatoire();
        AlgoRechercheMinMax3x3 minMax  = new AlgoRechercheMinMax3x3(joueurAlea, joueurOrdi);
        joueurAlea.setAlgoRecherche(algoAlea);
        joueurOrdi.setAlgoRecherche(minMax);
        Arbitre a = new Arbitre(grille, joueurAlea, joueurOrdi);
        a.startNewGame(false);*/

        //Tournoi 50 parties 3x3 Aléatoire vs IA Min Max
        /*JoueurOrdi joueurAlea = new JoueurOrdi("Alea");
        JoueurOrdi joueurOrdi = new JoueurOrdi("Ordi");
        AlgoRechercheAleatoire algoAlea  = new AlgoRechercheAleatoire();
        AlgoRechercheMinMax3x3 minMax  = new AlgoRechercheMinMax3x3(joueurAlea, joueurOrdi);
        joueurAlea.setAlgoRecherche(algoAlea);
        joueurOrdi.setAlgoRecherche(minMax);
        Arbitre a = new Arbitre(grille, joueurAlea, joueurOrdi);
        a.startTournament(50 , false);*/

        /* -----------------------
            TEST TICTACTOE 9x9
            ----------------------- */
        GrilleTicTacToe9x9 grille = new GrilleTicTacToe9x9();

        //Partie Humain vs IA Min Max
        /*JoueurHumain humain = new JoueurHumain("Humain"); 
        JoueurOrdi joueurOrdi = new JoueurOrdi("Ordi");
        AlgoRechercheMinMax9x9 minMax  = new AlgoRechercheMinMax9x9(humain, joueurOrdi);
        joueurOrdi.setAlgoRecherche(minMax);
        Arbitre a = new Arbitre(grille, humain, joueurOrdi);
        a.startNewGame(true);*/

        //Partie Aléatoire vs IA Min Max
        JoueurOrdi joueurAlea = new JoueurOrdi("Alea");
        JoueurOrdi joueurOrdi = new JoueurOrdi("Ordi");
        AlgoRechercheAleatoire algoAlea  = new AlgoRechercheAleatoire();
        AlgoRechercheMinMax9x9 minMax  = new AlgoRechercheMinMax9x9(joueurAlea, joueurOrdi);
        joueurAlea.setAlgoRecherche(algoAlea);
        joueurOrdi.setAlgoRecherche(minMax);
        Arbitre a = new Arbitre(grille, joueurAlea, joueurOrdi);
        a.startNewGame(false);

        //Tournoi 50 parties Aléatoire vs IA Min Max
        /*JoueurOrdi joueurAlea = new JoueurOrdi("Alea");
        JoueurOrdi joueurOrdi = new JoueurOrdi("Ordi");
        AlgoRechercheAleatoire algoAlea  = new AlgoRechercheAleatoire();
        AlgoRechercheMinMax9x9 minMax  = new AlgoRechercheMinMax9x9(joueurAlea, joueurOrdi);
        joueurAlea.setAlgoRecherche(algoAlea);
        joueurOrdi.setAlgoRecherche(minMax);
        Arbitre a = new Arbitre(grille, joueurAlea, joueurOrdi);
        a.startTournament(50 , false);*/
    }
}