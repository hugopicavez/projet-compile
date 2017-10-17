/**
 * Type énuméré pour les erreurs contextuelles.
 * Ce type énuméré définit toutes les erreurs contextuelles possibles et
 * permet l'affichage des messages d'erreurs pour la passe 2.
 */

// -------------------------------------------------------------------------
// A COMPLETER, avec les différents types d'erreur et les messages d'erreurs
// correspondants
// -------------------------------------------------------------------------

package fr.esisar.compilation.verif;

public enum ErreurContext {
   ErreurIdentificateurInvalide,
   ErreurVariableInconnue,
   ErreurConstanteInvalide ,
   ErreurTypeNonCompatible,
   ErreurTypeBolleanAttendu,
   ErreurTypeInvalid,
   ErreurVariableDejaDeffinit,
   ErreurNonRepertoriee;

   void leverErreurContext(String s, int numLigne) throws ErreurVerif {
      System.err.println("Erreur contextuelle : "+s);
      switch (this) {
         case ErreurIdentificateurInvalide:
            System.err.print("identificateur invalide : "+s);
            break ;
      case ErreurConstanteInvalide:
           System.err.print("constante invalide : "+s);
           break;
        case ErreurVariableInconnue:
             System.err.print("variable inconnu : "+s);
             break;
        case ErreurTypeNonCompatible:
            System.err.print("type non compatible : "+s);
            break;
        case ErreurTypeBolleanAttendu:
          System.err.print("boolean attendu : "+s);
          break;
        case ErreurTypeInvalid:
          System.err.print("type invalide : "+s);
          break;
        case ErreurVariableDejaDeffinit:
          System.err.print("vvariable déja définie : "+s);
         default:
            System.err.print("non repertoriee : "+s);
      }
      System.err.println(" ... ligne " + numLigne);
      throw new ErreurVerif();
   }

}
