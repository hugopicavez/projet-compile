package fr.esisar.compilation.verif;

import fr.esisar.compilation.global.src.*;

/**
 * La classe ReglesTypage permet de définir les différentes règles
 * de typage du langage JCas.
 */

public class ReglesTypage {
   /**
    * Teste si le type t1 et le type t2 sont compatibles pour l'affectation,
    * c'est à dire si on peut affecter un objet de t2 à un objet de type t1.
    */

   static ResultatAffectCompatible affectCompatible(Type t1, Type t2) {
     ResultatAffectCompatible affectOK;
     affectOK.setConv2(false);
     if (t1.getNature().equals(t2.getNature())
     {
       if (t1.getNature().equals(NatureType.Array))
       {
         affectOK.setOk(ResultatAffectCompatible((Array)t1.getElement(),(Array)t2.getElement()).getOk()
         && (Array)t1.getIndice().getNature().equals(NatureType.Interval)
         && (Array)t2.getIndice().getNature().equals(NatureType.Interval)
         && (Array)t1.getIndice().getBorneInf().equals(t2.getIndice().getBorneInf())
         && (Array)t1.getIndice().getBorneSup().equals(t2.getIndice().getBorneSup()));
       }
       else
       {
         affectOK.setOk(true);
       }
     }
     else
     {
       if(t1.getNature().equals(NatureType.Real) && t2.getNature().equals(NatureType.Interval))
       {
         affectOK.setOk(true);
         affectOK.setConv2(true);
       }
       else
       {
         affectOK.setOk(false);
       }
     }
     return affectOK;
   }

   /**
    * Teste si le type t1 et le type t2 sont compatible pour l'opération
    * binaire représentée dans noeud.
    */

   static ResultatBinaireCompatible binaireCompatible(Noeud noeud, Type t1, Type t2) {
     ResultatBinaireCompatible binaireOk;
     ResultatAffectCompatible affectOK;
     affectOK=ResultatAffectCompatible(t1,t2);
     if(affectOK.getOk().equals(false))
     {
       affectOK=ResultatAffectCompatible(t2,t1);
       binaireOk.setOk(affectOK.getOk());
       binaireOk.setConv1(affectOK.getConv2());
     }
     else
     {
       binaireOk.setOk(affectOK.getOk());
       binaireOk.setConv2(affectOK.getConv2());
     }
     switch(noeud){
       case Affect:
        binaireOk.setTypeRes(t1)
        break;
      case Egal:
      case Et:
      case Inf:
      case InfEgal:
      case NonEgal:
      case Ou:
      case Sup:
      case SupEgal:
        binaireOk.setTypeRes(Type.Boolean)
        break;
      case DivReel:
        binaireOk.setTypeRes(Type.Real);
        break;
      case

     }
     return null;
   }

   /**
    * Teste si le type t est compatible pour l'opération binaire représentée
    * dans noeud.
    */
   static ResultatUnaireCompatible unaireCompatible(Noeud noeud, Type t) {
     ResultatUnaireCompatible unaireOK;

     return unaireOK;
   }

}
