package fr.esisar.compilation.gencode;

import fr.esisar.compilation.global.src.*;
import fr.esisar.compilation.global.src3.*;
import fr.esisar.compilation.verif.ErreurInterneVerif;
import fr.esisar.compilation.verif.ErreurVerif;

import java.util.Arrays;

/**
 * Génération de code pour un programme JCas à partir d'un arbre décoré.
 */

class Generation {
    //TODO vérifier état pile a chaque fois.
    private static PullRegistre pullRegistre;
    private static int numberEti = 0;

    /**
     * Méthode principale de génération de code.
     * Génère du code pour l'arbre décoré a.
     */
    static Prog coder(Arbre a) {
        pullRegistre = new PullRegistre();
        numberEti = 0;
        Prog.ajouterGrosComment("Programme généré par JCasc");

        int size = gene_LISTE_DECL(a.getFils1(), 0);
        Prog.instance().getListeLignes().add(0, new Ligne(null, Inst.creation1(Operation.ADDSP,
                Operande.creationOpEntier(size)), null));

        gene_LISTE_INST(a.getFils2());

        Prog.ajouter(Inst.creation1(Operation.SUBSP,
                Operande.creationOpEntier(size)));

        // Fin du programme
        // L'instruction "HALT"
        Inst inst = Inst.creation0(Operation.HALT);
        // On ajoute l'instruction à la fin du programme
        Prog.ajouter(inst);

        // On retourne le programme assembleur généré
        return Prog.instance();
    }

    private static int gene_LISTE_DECL(Arbre a, int index) {
        switch (a.getNoeud()) {
            case Vide:
                break;
            case ListeDecl:
                gene_LISTE_DECL(a.getFils1(), index);
                index = gene_DECL(a.getFils2(), index);
                break;
        }
        return index;
    }

    private static int gene_DECL(Arbre a, int index) {
        switch (a.getNoeud()) {
            case Decl:
                index = gene_LISTE_IDF(a.getFils1(), index);
                break;
        }
        return index;
    }

    private static int gene_LISTE_IDF(Arbre a, int index) {
        switch (a.getNoeud()) {
            case Vide:
                break;
            case ListeIdent:
                index = gene_LISTE_IDF(a.getFils1(), index);
                a.getFils2().getDecor().setInfoCode(index);
                Type type = a.getFils2().getDecor().getType();
                if (type.getNature() != NatureType.Array) {
                    index++;
                    break;
                }
                index = gene_TABLEAU(type, index);
        }
        return index;
    }

    private static int gene_TABLEAU(Type type, int index) {
        int start = index;
        int size = type.getIndice().getBorneSup() - type.getIndice().getBorneInf();
        index += size;
        if (type.getElement().getNature() == NatureType.Array) {
            for (int i = start; i < start + size; i++) {
                Prog.ajouter(Inst.creation2(Operation.LOAD, Operande.creationOpEntier(index), Operande.R0));
                Prog.ajouter(Inst.creation2(Operation.STORE, Operande.R0, Operande.creationOpIndirect(i, Registre.GB)));
                index = gene_TABLEAU(type.getElement(), index);
            }
        }
        return index;
    }

    private static void gene_LISTE_INST(Arbre a) {
        switch (a.getNoeud()) {
            case Vide:
                break;
            case ListeInst:
                gene_LISTE_INST(a.getFils1());
                gene_INST(a.getFils2());
                break;
            default:
                throw new ErreurInterneVerif("Arbre incorrect dans verifier_LISTE_INST");
        }
    }


    private static void gene_INST(Arbre a) {
        switch (a.getNoeud()) {
            case Nop:
                break;
            case Affect:
                gene_AFFECT(a);
                break;
            case Pour:
                gene_POUR(a);
                break;
            case TantQue:
                gene_TANTQUE(a);
                break;
            case Si:
                gene_SI(a);
                break;
            case Ecriture:
                gene_ECRITURE(a);
                break;
            case Lecture:
                gene_LECTURE(a);
                break;
            case Ligne:
                Prog.ajouter(Inst.creation0(Operation.WNL));
            default:
                throw new ErreurInterneVerif("Arvre incorrect dans verifier_INST");
        }
    }

    private static void gene_AFFECT(Arbre a) {
        Type type = a.getFils1().getDecor().getType();
        if (type.getNature() == NatureType.Array)
            gene_Affect_Tableau(a);
        else {
            boolean alreadyUse = false;
            Registre registre = pullRegistre.checkregistre();
            if (registre == null) {
                registre = pullRegistre.getRegistreAlreadyUse();
                alreadyUse = true;
                Prog.ajouter(Inst.creation1(Operation.PUSH, Operande.opDirect(registre)));
            }
            gene_Exp(a.getFils2(), registre);
            Prog.ajouter(Inst.creation2(Operation.STORE, Operande.opDirect(registre),
                    Operande.creationOpIndirect(a.getFils1().getDecor().getInfoCode(), Registre.GB)));
            if (alreadyUse)
                Prog.ajouter(Inst.creation1(Operation.POP, Operande.opDirect(registre)));
            else
                pullRegistre.libere(registre);
        }
    }

    private static void gene_Affect_Tableau(Arbre a) {
        //TODO faire
    }

    private static void gene_POUR(Arbre a) {
        Registre variable = pullRegistre.checkregistre();
        Registre fin = pullRegistre.checkregistre();
        Etiq debutEti = Etiq.nouvelle("eti" + numberEti++);
        Etiq finEti = Etiq.nouvelle("eti" + numberEti++);
        boolean alreadyUseVariable = false;
        if(variable == null ){
            fin = pullRegistre.getRegistreAlreadyUse();
            alreadyUseVariable = true;
            Prog.ajouter(Inst.creation1(Operation.PUSH, Operande.opDirect(variable)));
        }
        boolean alreadyUseFin = false;
        if(fin == null ){
            fin = pullRegistre.getRegistreAlreadyUse(variable);
            alreadyUseFin = true;
            Prog.ajouter(Inst.creation1(Operation.PUSH, Operande.opDirect(fin)));
        }
        gene_Exp(a.getFils1().getFils2(), variable);
        gene_Exp(a.getFils1().getFils3(), fin);
        Prog.ajouter(debutEti);
        Prog.ajouter(Inst.creation2(Operation.CMP, Operande.opDirect(variable), Operande.opDirect(fin)));
        Prog.ajouter(Inst.creation1(Operation.BEQ, Operande.creationOpEtiq(finEti)));

    }

    private static void gene_TANTQUE(Arbre a) {
        Etiq debut = Etiq.nouvelle("eti" + numberEti++);
        Etiq fin = Etiq.nouvelle("eti" + numberEti++);
        boolean alreadyUse = false;
        Registre registre = pullRegistre.checkregistre();
        if (registre == null) {
            registre = pullRegistre.getRegistreAlreadyUse();
            alreadyUse = true;
            Prog.ajouter(Inst.creation1(Operation.PUSH, Operande.opDirect(registre)));
        }
        Prog.ajouter(debut);
        gene_Exp(a.getFils1(), registre);
        Prog.ajouter(Inst.creation2(Operation.CMP, Operande.creationOpEntier(0), Operande.opDirect(registre)));
        Prog.ajouter(Inst.creation1(Operation.BEQ, Operande.creationOpEtiq(fin)));
        gene_LISTE_INST(a.getFils2());
        Prog.ajouter(Inst.creation1(Operation.BRA, Operande.creationOpEtiq(debut)));
        Prog.ajouter(fin);
        if (alreadyUse)
            Prog.ajouter(Inst.creation1(Operation.POP, Operande.opDirect(registre)));
        else
            pullRegistre.libere(registre);
    }

    private static void gene_SI(Arbre a) {
        Etiq els = Etiq.nouvelle("eti" + numberEti++);
        Etiq fin = Etiq.nouvelle("eti" + numberEti++);
        boolean alreadyUse = false;
        Registre registre = pullRegistre.checkregistre();
        if (registre == null) {
            registre = pullRegistre.getRegistreAlreadyUse();
            alreadyUse = true;
            Prog.ajouter(Inst.creation1(Operation.PUSH, Operande.opDirect(registre)));
        }
        gene_Exp(a.getFils1(), registre);
        Prog.ajouter(Inst.creation2(Operation.CMP, Operande.creationOpEntier(0), Operande.opDirect(registre)));
        Prog.ajouter(Inst.creation1(Operation.BEQ, Operande.creationOpEtiq(els)));
        gene_LISTE_INST(a.getFils2());
        Prog.ajouter(Inst.creation1(Operation.BRA, Operande.creationOpEtiq(fin)));
        Prog.ajouter(els);
        gene_LISTE_INST(a.getFils3());
        Prog.ajouter(fin);
        if (alreadyUse)
            Prog.ajouter(Inst.creation1(Operation.POP, Operande.opDirect(registre)));
        else
            pullRegistre.libere(registre);
    }

    private static void gene_ECRITURE(Arbre a) {
        Arbre noeud_Exp = a.getFils1();
        boolean alreadyUse = false;
        while (noeud_Exp.getNoeud() != Noeud.Vide){
            Arbre exp = noeud_Exp.getFils2();
            noeud_Exp = noeud_Exp.getFils1();
            if(exp.getDecor().getType() == Type.String)
                Prog.ajouter(Inst.creation1(Operation.WSTR, Operande.creationOpChaine(exp.getChaine())));
            else {
                if (!alreadyUse && !pullRegistre.isFree(Registre.R1)) {
                    alreadyUse = true;
                    Prog.ajouter(Inst.creation1(Operation.PUSH, Operande.R1));
                }
                gene_Exp(exp, Registre.R1);
                if (a.getFils1().getDecor().getType().getNature() == NatureType.Real)
                    Prog.ajouter(Inst.creation0(Operation.WFLOAT));
                else
                    Prog.ajouter(Inst.creation0(Operation.WINT));
            }
        }
        if (alreadyUse)
            Prog.ajouter(Inst.creation1(Operation.POP, Operande.R1));
    }

    private static void gene_LECTURE(Arbre a) {
        boolean alreadyUse = false;
        if (!pullRegistre.isFree(Registre.R1)) {
            alreadyUse = true;
            Prog.ajouter(Inst.creation1(Operation.PUSH, Operande.R1));
        }
        if (a.getFils1().getDecor().getType().getNature() == NatureType.Real)
            Prog.ajouter(Inst.creation0(Operation.RFLOAT));
        else
            Prog.ajouter(Inst.creation0(Operation.RINT));
        if (alreadyUse)
            Prog.ajouter(Inst.creation1(Operation.POP, Operande.R1));
    }

    private static void gene_Exp(Arbre a, Registre registre) {
        switch (a.getNoeud()) {
            case Entier:
            case Reel:
            case Index:
            case Ident:
                gene_lecture_variable(a, registre);
                break;
            case Non:
            case MoinsUnaire:
            case PlusUnaire:
                gene_expression_Unitaire(a, registre);
                break;
            case Et:
            case Ou:
                gene_expression_Logique(a, registre);
                break;
            case Egal:
            case InfEgal:
            case SupEgal:
            case NonEgal:
            case Inf:
            case Sup:
                gene_comparaison(a, registre);
                break;
            case Plus:
            case Moins:
            case Mult:
            case DivReel:
            case Reste:
            case Quotient:
                //TODO faire
                break;

        }
    }

    private static void gene_lecture_variable(Arbre a, Registre registre) {
        switch (a.getNoeud()) {
            case Entier:
                Prog.ajouter(Inst.creation2(Operation.LOAD, Operande.creationOpEntier(a.getEntier()), Operande.opDirect(registre)));
                break;
            case Reel:
                Prog.ajouter(Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getReel()), Operande.opDirect(registre)));
                break;
            case Index:
                gene_Lecture_Tableau(a, registre);
                break;
            case Ident:
                Prog.ajouter(Inst.creation2(Operation.LOAD,
                        Operande.creationOpIndirect(a.getDecor().getInfoCode(), Registre.GB),
                        Operande.opDirect(registre)));
                break;
        }
    }

    private static void gene_Lecture_Tableau(Arbre a, Registre registre) {
        switch (a.getNoeud()) {
            case Ident:
                Prog.ajouter(Inst.creation2(Operation.LOAD,
                        Operande.creationOpIndirect(a.getDecor().getInfoCode(), Registre.GB),
                        Operande.opDirect(registre)));
                break;
            case Index:
                gene_Exp(a.getFils2(), registre);
                boolean alreadyUse = false;
                Registre temp = pullRegistre.checkregistre();
                if (temp == null) {
                    temp = pullRegistre.getRegistreAlreadyUse(registre);
                    alreadyUse = true;
                    Prog.ajouter(Inst.creation1(Operation.PUSH, Operande.opDirect(temp)));
                }
                Prog.ajouter(Inst.creation2(Operation.LOAD,
                        Operande.creationOpIndexe(0, registre, temp),
                        Operande.opDirect(registre)));
                if (alreadyUse)
                    Prog.ajouter(Inst.creation1(Operation.POP, Operande.opDirect(temp)));
                else
                    pullRegistre.libere(temp);
                break;
            default:
                throw new ErreurInterneVerif("Place : " + a.getFils1().getNumLigne());
        }
    }

    private static void gene_expression_Unitaire(Arbre a, Registre registre) {
        switch (a.getNoeud()) {
            case Non:
                Etiq vrai = Etiq.lEtiq("eti" + numberEti++);
                Etiq fin = Etiq.nouvelle("eti" + numberEti++);
                Prog.ajouter(Inst.creation2(Operation.CMP, Operande.creationOpEntier(0), Operande.opDirect(registre)));
                Prog.ajouter(Inst.creation1(Operation.BEQ, Operande.creationOpEtiq(vrai)));
                Prog.ajouter(Inst.creation2(Operation.LOAD, Operande.creationOpEntier(0), Operande.opDirect(registre)));
                Prog.ajouter(Inst.creation1(Operation.BRA, Operande.creationOpEtiq(fin)));
                Prog.ajouter(vrai);
                Prog.ajouter(Inst.creation2(Operation.LOAD, Operande.creationOpEntier(1), Operande.opDirect(registre)));
                Prog.ajouter(fin);
                break;
            case MoinsUnaire:
                gene_Exp(a.getFils1(), registre);
                Prog.ajouter(Inst.creation2(Operation.MUL, Operande.creationOpEntier(-1), Operande.opDirect(registre)));
                break;
            case PlusUnaire:
                gene_Exp(a.getFils1(), registre);
                break;
        }
    }

    private static void gene_expression_Logique(Arbre a, Registre registre) {
        Etiq autre = Etiq.nouvelle("eti" + numberEti++);
        Etiq fin = Etiq.nouvelle("eti" + numberEti++);
        gene_Exp(a.getFils1(), registre);
        Prog.ajouter(Inst.creation2(Operation.CMP, Operande.creationOpEntier(0), Operande.opDirect(registre)));
        if (a.getNoeud() == Noeud.Et)
            Prog.ajouter(Inst.creation1(Operation.BEQ, Operande.creationOpEtiq(autre)));
        else
            Prog.ajouter(Inst.creation1(Operation.BEQ, Operande.creationOpEtiq(autre)));
        gene_Exp(a.getFils2(), registre);
        Prog.ajouter(Inst.creation2(Operation.CMP, Operande.creationOpEntier(0), Operande.opDirect(registre)));
        Prog.ajouter(Inst.creation1(Operation.BRA, Operande.creationOpEtiq(fin)));
        Prog.ajouter(autre);
        if (a.getNoeud() == Noeud.Et)
            Prog.ajouter(Inst.creation2(Operation.LOAD, Operande.creationOpEntier(0), Operande.opDirect(registre)));
        else
            Prog.ajouter(Inst.creation2(Operation.LOAD, Operande.creationOpEntier(1), Operande.opDirect(registre)));
        Prog.ajouter(fin);
    }

    private static void gene_comparaison(Arbre a, Registre registre) {
        Etiq vrai = Etiq.lEtiq("eti" + numberEti++);
        Etiq fin = Etiq.nouvelle("eti" + numberEti++);
        boolean alreadyUse = false;
        Registre temp = pullRegistre.checkregistre();
        if (temp == null) {
            temp = pullRegistre.getRegistreAlreadyUse(registre);
            alreadyUse = true;
            Prog.ajouter(Inst.creation1(Operation.PUSH, Operande.opDirect(temp)));
        }
        gene_Exp(a.getFils1(), registre);
        gene_Exp(a.getFils2(), temp);
        Prog.ajouter(Inst.creation2(Operation.CMP, Operande.opDirect(registre), Operande.opDirect(temp)));
        if (a.getNoeud() == Noeud.Egal)
            Prog.ajouter(Inst.creation1(Operation.BEQ, Operande.creationOpEtiq(vrai)));
        else if (a.getNoeud() == Noeud.InfEgal)
            Prog.ajouter(Inst.creation1(Operation.BLE, Operande.creationOpEtiq(vrai)));
        else if (a.getNoeud() == Noeud.SupEgal)
            Prog.ajouter(Inst.creation1(Operation.BGE, Operande.creationOpEtiq(vrai)));
        else if (a.getNoeud() == Noeud.NonEgal)
            Prog.ajouter(Inst.creation1(Operation.BNE, Operande.creationOpEtiq(vrai)));
        else if (a.getNoeud() == Noeud.Inf)
            Prog.ajouter(Inst.creation1(Operation.BLT, Operande.creationOpEtiq(vrai)));
        else if (a.getNoeud() == Noeud.Sup)
            Prog.ajouter(Inst.creation1(Operation.BGT, Operande.creationOpEtiq(vrai)));
        Prog.ajouter(Inst.creation2(Operation.LOAD, Operande.creationOpEntier(0), Operande.opDirect(registre)));
        Prog.ajouter(Inst.creation1(Operation.BRA, Operande.creationOpEtiq(fin)));
        Prog.ajouter(vrai);
        Prog.ajouter(Inst.creation2(Operation.LOAD, Operande.creationOpEntier(1), Operande.opDirect(registre)));
        Prog.ajouter(fin);
        if (alreadyUse)
            Prog.ajouter(Inst.creation1(Operation.POP, Operande.opDirect(temp)));
        else
            pullRegistre.libere(temp);
    }

}



