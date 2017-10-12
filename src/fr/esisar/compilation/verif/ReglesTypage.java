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
        ResultatAffectCompatible affectOK = new ResultatAffectCompatible();
        affectOK.setConv2(false);
        if (t1.getNature().equals(t2.getNature())) {
            if (t1.getNature().equals(NatureType.Array)) {
                affectOK.setOk(affectCompatible(t1.getElement(), t2.getElement()).getOk()
                        && t1.getIndice().getNature().equals(NatureType.Interval)
                        && t2.getIndice().getNature().equals(NatureType.Interval)
                        && t1.getIndice().getBorneInf() == t2.getIndice().getBorneInf()
                        && t1.getIndice().getBorneSup() == t2.getIndice().getBorneSup());
            } else {
                affectOK.setOk(true);
            }
        } else {
            if (t1.getNature().equals(NatureType.Real) && t2.getNature().equals(NatureType.Interval)) {
                affectOK.setOk(true);
                affectOK.setConv2(true);
            } else {
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
        ResultatBinaireCompatible binaireOk =new ResultatBinaireCompatible();
        binaireOk.setOk(false);
        binaireOk.setConv1(false);
        binaireOk.setConv2(false);
        switch (noeud) {
            case Et:
            case Ou:
                if (t1.getNature().equals(NatureType.Boolean) && t1.getNature().equals(NatureType.Boolean))
                {
                    binaireOk.setOk(true);
                    binaireOk.setTypeRes(Type.Boolean);
                }
                break;
            case Egal:
            case Inf:
            case Sup:
            case NonEgal:
            case InfEgal:
            case SupEgal:
                if((t1.getNature().equals(NatureType.Interval) || t1.getNature().equals(NatureType.Real)) && (t2.getNature().equals(NatureType.Interval) || t2.getNature().equals(NatureType.Real)))
                {
                    binaireOk.setOk(true);
                    binaireOk.setTypeRes(Type.Boolean);
                    if(t1.getNature().equals(NatureType.Interval) && t2.getNature().equals(NatureType.Real)){
                        binaireOk.setConv1(true);
                    }
                    if(t1.getNature().equals(NatureType.Real) && t2.getNature().equals(NatureType.Interval)){
                        binaireOk.setConv2(true);
                    }
                }
                break;
            case Plus:
            case Moins:
            case Mult:
                if((t1.getNature().equals(NatureType.Interval) || t1.getNature().equals(NatureType.Real)) && (t2.getNature().equals(NatureType.Interval) || t2.getNature().equals(NatureType.Real)))
                {
                    binaireOk.setOk(true);
                    if (t1.getNature().equals(NatureType.Interval) && t2.getNature().equals(NatureType.Interval))
                    {
                        binaireOk.setTypeRes(Type.Integer);
                    }
                    else
                    {
                        if(t1.getNature().equals(NatureType.Interval)){
                            binaireOk.setConv1(true);
                        }
                        if(t2.getNature().equals(NatureType.Interval)){
                            binaireOk.setConv2(true);
                        }
                        binaireOk.setTypeRes(Type.Real);
                    }
                }
                break;
            case Quotient:
            case Reste:
                if (t1.getNature().equals(NatureType.Interval) && t2.getNature().equals(NatureType.Interval))
                {
                    binaireOk.setOk(true);
                    binaireOk.setTypeRes(Type.Integer);
                }
                break;
            case DivReel:
                if((t1.getNature().equals(NatureType.Interval) || t1.getNature().equals(NatureType.Real)) && (t2.getNature().equals(NatureType.Interval) || t2.getNature().equals(NatureType.Real)))
                {
                    binaireOk.setOk(true);
                    binaireOk.setTypeRes(Type.Real);
                    if(t1.getNature().equals(NatureType.Interval) && t2.getNature().equals(NatureType.Real)){
                        binaireOk.setConv1(true);
                    }
                    if(t1.getNature().equals(NatureType.Real) && t2.getNature().equals(NatureType.Interval)){
                        binaireOk.setConv2(true);
                    }
                }
                break;
        }
        return binaireOk;
    }

    /**
     * Teste si le type t est compatible pour l'opération binaire représentée
     * dans noeud.
     */
    static ResultatUnaireCompatible unaireCompatible(Noeud noeud, Type t) {
        ResultatUnaireCompatible unaireOK = new ResultatUnaireCompatible();
        unaireOK.setOk(false);
        switch (noeud){
            case Non:
                if (t.getNature().equals(NatureType.Boolean))
                {
                    unaireOK.setOk(true);
                    unaireOK.setTypeRes(Type.Boolean);
                }
                break;
            case MoinsUnaire:
            case PlusUnaire:
                if (t.getNature().equals(NatureType.Interval))
                {
                    unaireOK.setOk(true);
                    unaireOK.setTypeRes(Type.Integer);
                }
                if (t.getNature().equals(NatureType.Real))
                {
                    unaireOK.setOk(true);
                    unaireOK.setTypeRes(Type.Real);
                }
                break;
        }
        return unaireOK;
    }

}
