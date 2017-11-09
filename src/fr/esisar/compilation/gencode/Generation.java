package fr.esisar.compilation.gencode;

import fr.esisar.compilation.global.src.*;
import fr.esisar.compilation.global.src3.*;

/**
 * Génération de code pour un programme JCas à partir d'un arbre décoré.
 */

class Generation {
   
   /**
    * Méthode principale de génération de code.
    * Génère du code pour l'arbre décoré a.
    */
   static Prog coder(Arbre a) {
      Prog.ajouterGrosComment("Programme généré par JCasc");



      // Fin du programme
      // L'instruction "HALT"
      Inst inst = Inst.creation0(Operation.HALT);
      // On ajoute l'instruction à la fin du programme
      Prog.ajouter(inst);

      // On retourne le programme assembleur généré
      return Prog.instance(); 
   }

   static Registre GenExpr(Arbre a) {
      Noeud n = a.getNoeud();
      Registre resultat=null;
      switch(n){
          case Et:
          case Ou:
          case Egal:
          case Inf:
          case Sup:
          case NonEgal:
          case InfEgal:
          case SupEgal:
          case Plus:
          case Moins:
          case Mult:
          case Quotient:
          case Reste:
          case DivReel:

              break;
          case Non:
          case MoinsUnaire:
          case PlusUnaire:
              break;
      }
      return resultat;
   }
}



