package tictactoecodingame;
import java.util.Arrays;

/**
 *
 * @author franck
 */

    /*--------------------------------------------------------*/
    /*                 Version jeu en local                   */
    /*--------------------------------------------------------*/
public class Player {

    public static void main(String args[]) {
        
        //Test à la main : 
        
        JoueurHumain humain = new JoueurHumain("Humain");
        JoueurOrdi IA = new JoueurOrdi("IA");
        
        /* L'utilité et l'utilisation de ces paramètres est précisée dans le fichier AlgoRechercheMCTS.java. 
          Nous le re-précisons ici toutefois : l'algorithme MCTS fonctionne en soi sans avoir à préciser
          si la grille est en 3x3 ou en 9x9, cette précision est uniquement nécéssaire dans le cadre de
          l'utilisation de notre fonction de simulation améliorée simulationAmelioree. */
        
        boolean is9x9 = false;
        int methodeSimulation = 0;
        
        AlgoRechercheMCTS ia = new AlgoRechercheMCTS(humain, IA, is9x9, methodeSimulation);
        IA.setAlgoRecherche(ia);
        
        GrilleTicTacToe3x3 grille = new GrilleTicTacToe3x3();
        
        Arbitre a = new Arbitre(grille, IA, humain);
        
        a.startNewGame(true);
        
        // Test tournament IA vs Aléat :
        
        /*JoueurOrdi ordi = new JoueurOrdi("Ordi");
        JoueurOrdi IA = new JoueurOrdi("IA");
        boolean is9x9 = false;
        int methodeSimulation = 0;
        
        AlgoRechercheAleatoire aleat = new AlgoRechercheAleatoire();
        AlgoRechercheMCTS ia = new AlgoRechercheMCTS(ordi, IA, is9x9, methodeSimulation);
        
        ordi.setAlgoRecherche(aleat);
        IA.setAlgoRecherche(ia);
             
        GrilleTicTacToe3x3 grille = new GrilleTicTacToe3x3();
        
        Arbitre a = new Arbitre(grille, ordi, IA);  
        
       
        a.startTournament(1000, false); */
        

    }
}
    /*--------------------------------------------------------*/
    /*                 Version Codin game                     */
    /*--------------------------------------------------------*/


    /* Code à utliser en mode compétition sur le site CodinGame (une fois la ligue Wood passée, qui se joue en 3x3).
       Afin d'obtenir le fichier contenant toutes les classes, il faut utiliser le builder du projet, vous pouvez vous réferrer
       au ReadMe du projet. */
    
    /* import java.util.Scanner;



    class Player {

       public static void main(String args[]) {

             Scanner in = new Scanner(System.in);

            CoupTicTacToe coup;
            JoueurHumain adversaire = new JoueurHumain("Adversaire");
            JoueurOrdi IA = new JoueurOrdi("IA");

	    boolean is9x9 = true;  
            int methodeSimulation = 0;

            AlgoRechercheName ia  = new AlgoRechercheName(adversaire, IA, is9x9, methodeSimulation);   
            IA.setAlgoRecherche(ia);

            GrilleTicTacToe9x9 grille = new GrilleTicTacToe9x9();
            grille.init();


            while (true) {
                int opponentRow = in.nextInt();
                int opponentCol = in.nextInt();
                int validActionCount = in.nextInt();
                for (int i = 0; i < validActionCount; i++) {
                    int row = in.nextInt();
                    int col = in.nextInt();
                }
                if ( opponentCol != -1  ) {
                    coup = new CoupTicTacToe(opponentCol, opponentRow, new Jeton(adversaire));
                    grille.joueCoup(coup);
                }

                coup = (CoupTicTacToe) IA.joue(grille);
                grille.joueCoup(coup);
                System.out.println(coup.getLigne() + " " + coup.getColonne() ); 
                System.out.flush();
            }
       }
    
} */
