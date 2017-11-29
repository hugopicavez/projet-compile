package fr.esisar.compilation.gencode;

import fr.esisar.compilation.global.src.Arbre;
import fr.esisar.compilation.global.src.NatureType;
import fr.esisar.compilation.global.src.Noeud;
import fr.esisar.compilation.global.src.Type;
import fr.esisar.compilation.global.src3.*;
import fr.esisar.compilation.verif.ErreurInterneVerif;

import java.util.ArrayList;
import java.util.List;

public class Generation {

    private static Memoire memoire;
    public static int numberEti = 0;

    static Prog coder(Arbre a) {
        memoire = new Memoire();
        numberEti = 0;
        Prog.ajouterGrosComment("Programme généré par JCasc");

        int size = gene_LISTE_DECL(a.getFils1(), 1);
        memoire.testePile(size);
        memoire.setPileUse(size);
        Prog.ajouter(Inst.creation1(Operation.ADDSP,
                Operande.creationOpEntier(size)));
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
                index = gene_LISTE_DECL(a.getFils1(), index);
                index = gene_DECL(a.getFils2(), index);
                break;
        }
        return index;
    }

    private static int gene_DECL(Arbre a, int index) {
        switch (a.getNoeud()) {
            case Decl:
                index = gene_LISTE_IDF(a.getFils1(), index);
        }
        return index;
    }

    private static int gene_LISTE_IDF(Arbre a, int index) {
        switch (a.getNoeud()) {
            case Vide:
                break;
            case ListeIdent:
                index = gene_LISTE_IDF(a.getFils1(), index);
                a.getFils2().getDecor().getDefn().setOperande(Operande.creationOpIndirect(index, Registre.GB));
                System.out.println(index);
                Type type = a.getFils2().getDecor().getDefn().getType();
                if (type.getNature() != NatureType.Array) {
                    index++;
                    return index;
                }
                index += sizeTableau(type);
        }
        return index;
    }

    private static int sizeTableau(Type type) {
        int size = 1;
        while (type.getNature() == NatureType.Array) {
            size = (type.getIndice().getBorneSup() - type.getIndice().getBorneInf() + 1) * size;
            type = type.getElement();
        }
        return size;
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
                break;
        }
    }

    private static void gene_AFFECT(Arbre a) {
        Type type = a.getFils1().getDecor().getType();
        if (type.getNature() == NatureType.Array)
            gene_Affect_Tableau(a);
        else {
            Registre registre = memoire.get();
            Operande operande = gene_Exp(a.getFils2(), registre);
            if (operande.getNature() != NatureOperande.OpDirect)
                Prog.ajouter(Inst.creation2(Operation.LOAD, operande, Operande.opDirect(registre)));
            if (a.getFils1().getNoeud() != Noeud.Index) {
                if (type.getNature() == NatureType.Interval)
                    gene_Test_Affect_Interval(type, registre);
                Prog.ajouter(Inst.creation2(Operation.STORE, Operande.opDirect(registre),
                        a.getFils1().getDecor().getDefn().getOperande()));
            } else {
                Registre emplacement = memoire.get(registre);
                emplacement_Variable(a.getFils1(), emplacement);
                Prog.ajouter(Inst.creation2(Operation.STORE, Operande.opDirect(registre),
                        Operande.creationOpIndirect(0, emplacement)));
                memoire.free(emplacement);
            }
            memoire.free(registre);
        }
    }

    private static void gene_Test_Affect_Interval(Type type, Registre registre) {
        Etiq erreur = Etiq.nouvelle("eti" + numberEti++);
        Etiq fin = Etiq.nouvelle("eti" + numberEti++);
        Prog.ajouter(Inst.creation2(Operation.CMP, Operande.creationOpEntier(type.getBorneInf()), Operande.opDirect(registre)));
        Prog.ajouter(Inst.creation1(Operation.BLT, Operande.creationOpEtiq(erreur)));
        Prog.ajouter(Inst.creation2(Operation.CMP, Operande.creationOpEntier(type.getBorneSup()), Operande.opDirect(registre)));
        Prog.ajouter(Inst.creation1(Operation.BLE, Operande.creationOpEtiq(fin)));
        Prog.ajouter(erreur);
        Prog.ajouter(Inst.creation1(Operation.WSTR, Operande.creationOpChaine("débordement de l'intervale")));
        Prog.ajouter(Inst.creation0(Operation.WNL));
        Prog.ajouter(Inst.creation0(Operation.HALT));
        Prog.ajouter(fin);
    }

    private static void gene_Affect_Tableau(Arbre a) {
        Type emplacement = a.getFils1().getDecor().getDefn().getType();
        while (emplacement.getNature() == NatureType.Array)
            emplacement = emplacement.getElement();
        Registre result = memoire.get();
        Registre value = memoire.get(result);
        Registre temp = memoire.get(result, value);
        int size = sizeTableau(emplacement_Variable(a.getFils1(), result));
        emplacement_Variable(a.getFils2(), value);
        for (int i = 0; i < size; i++) {
            Prog.ajouter(Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(i, value), Operande.opDirect(temp)));
            if (emplacement.getNature() == NatureType.Interval)
                gene_Test_Affect_Interval(emplacement, temp);
            Prog.ajouter(Inst.creation2(Operation.STORE, Operande.opDirect(temp), Operande.creationOpIndirect(i, result)));
        }
        memoire.free(temp);
        memoire.free(value);
        memoire.free(result);
    }

    private static Type emplacement_Variable(Arbre a, Registre registre) {
        switch (a.getNoeud()) {
            case Ident:
                Prog.ajouter(Inst.creation2(Operation.LEA,
                        a.getDecor().getDefn().getOperande(),
                        Operande.opDirect(registre)));
                return a.getDecor().getType();
            case Index:
                Type type = emplacement_Variable(a.getFils1(), registre);
                Etiq erreur = Etiq.nouvelle("eti" + numberEti++);
                Etiq fin = Etiq.nouvelle("eti" + numberEti++);
                int size = sizeTableau(type.getElement());
                Registre index = memoire.get(registre);
                Operande operande = gene_Exp(a.getFils2(), index);
                if (operande.getNature() != NatureOperande.OpDirect)
                    Prog.ajouter(Inst.creation2(Operation.LOAD, operande, Operande.opDirect(index)));
                Prog.ajouter(Inst.creation2(Operation.CMP, Operande.creationOpEntier(type.getIndice().getBorneInf()), Operande.opDirect(index)));
                Prog.ajouter(Inst.creation1(Operation.BLT, Operande.creationOpEtiq(erreur)));
                Prog.ajouter(Inst.creation2(Operation.CMP, Operande.creationOpEntier(type.getIndice().getBorneSup()), Operande.opDirect(index)));
                Prog.ajouter(Inst.creation1(Operation.BLE, Operande.creationOpEtiq(fin)));
                Prog.ajouter(erreur);
                Prog.ajouter(Inst.creation1(Operation.WSTR, Operande.creationOpChaine("erreur debordemet index tableau")));
                Prog.ajouter(Inst.creation0(Operation.WNL));
                Prog.ajouter(Inst.creation0(Operation.HALT));
                Prog.ajouter(fin);
                Prog.ajouter(Inst.creation2(Operation.SUB, Operande.creationOpEntier(type.getIndice().getBorneInf()), Operande.opDirect(index)));
                Prog.ajouter(Inst.creation2(Operation.MUL, Operande.creationOpEntier(size), Operande.opDirect(index)));
                Prog.ajouter(Inst.creation2(Operation.LEA, Operande.creationOpIndexe(0, registre, index), Operande.opDirect(registre)));
                memoire.free(index);
                return type.getElement();
            default:
                throw new ErreurInterneVerif("Place : " + a.getFils1().getNumLigne());
        }
    }

    private static void gene_POUR(Arbre a) {
        Registre variable = memoire.get();
        Registre fin = memoire.get(variable);
        Etiq debutEti = Etiq.nouvelle("eti" + numberEti++);
        Etiq finEti = Etiq.nouvelle("eti" + numberEti++);
        Operande operande = gene_Exp(a.getFils1().getFils2(), variable);
        if (operande.getNature() != NatureOperande.OpDirect)
            Prog.ajouter(Inst.creation2(Operation.LOAD, operande, Operande.opDirect(variable)));
        Operande operandeFin = gene_Exp(a.getFils1().getFils3(), fin);
        if (operandeFin.getNature() != NatureOperande.OpDirect)
            Prog.ajouter(Inst.creation2(Operation.LOAD, operandeFin, Operande.opDirect(fin)));
        Prog.ajouter(debutEti);
        Prog.ajouter(Inst.creation2(Operation.STORE, Operande.opDirect(variable),
                a.getFils1().getFils1().getDecor().getDefn().getOperande()));
        Prog.ajouter(Inst.creation2(Operation.CMP, Operande.opDirect(variable), Operande.opDirect(fin)));
        if (a.getFils1().getNoeud() == Noeud.Increment)
            Prog.ajouter(Inst.creation1(Operation.BLT, Operande.creationOpEtiq(finEti)));
        else
            Prog.ajouter(Inst.creation1(Operation.BGT, Operande.creationOpEtiq(finEti)));
        gene_LISTE_INST(a.getFils2());
        if (a.getFils1().getNoeud() == Noeud.Increment)
            Prog.ajouter(Inst.creation2(Operation.ADD, Operande.creationOpEntier(1), Operande.opDirect(variable)));
        else
            Prog.ajouter(Inst.creation2(Operation.ADD, Operande.creationOpEntier(-1), Operande.opDirect(variable)));
        Prog.ajouter(Inst.creation1(Operation.BRA, Operande.creationOpEtiq(debutEti)));
        Prog.ajouter(finEti);
        memoire.free(fin);
        memoire.free(variable);
    }

    private static void gene_TANTQUE(Arbre a) {
        Etiq debut = Etiq.nouvelle("eti" + numberEti++);
        Etiq fin = Etiq.nouvelle("eti" + numberEti++);
        Registre registre = memoire.get();
        Prog.ajouter(debut);
        Operande operande = gene_Exp(a.getFils1(), registre);
        if (operande.getNature() != NatureOperande.OpDirect)
            Prog.ajouter(Inst.creation2(Operation.LOAD, operande, Operande.opDirect(registre)));
        Prog.ajouter(Inst.creation2(Operation.CMP, Operande.creationOpEntier(0), Operande.opDirect(registre)));
        Prog.ajouter(Inst.creation1(Operation.BEQ, Operande.creationOpEtiq(fin)));
        gene_LISTE_INST(a.getFils2());
        Prog.ajouter(Inst.creation1(Operation.BRA, Operande.creationOpEtiq(debut)));
        Prog.ajouter(fin);
        memoire.free(registre);
    }

    private static void gene_SI(Arbre a) {
        Etiq els = Etiq.nouvelle("eti" + numberEti++);
        Etiq fin = Etiq.nouvelle("eti" + numberEti++);
        Registre registre = memoire.get();
        Operande operande = gene_Exp(a.getFils1(), registre);
        if (operande.getNature() != NatureOperande.OpDirect)
            Prog.ajouter(Inst.creation2(Operation.LOAD, operande, Operande.opDirect(registre)));
        Prog.ajouter(Inst.creation2(Operation.CMP, Operande.creationOpEntier(0), Operande.opDirect(registre)));
        Prog.ajouter(Inst.creation1(Operation.BEQ, Operande.creationOpEtiq(els)));
        gene_LISTE_INST(a.getFils2());
        Prog.ajouter(Inst.creation1(Operation.BRA, Operande.creationOpEtiq(fin)));
        Prog.ajouter(els);
        gene_LISTE_INST(a.getFils3());
        Prog.ajouter(fin);
        memoire.free(registre);
    }

    private static void gene_ECRITURE(Arbre a) {
        if (gene_ECRICRE_GENE(a.getFils1()))
            memoire.pop(Registre.R1);
    }

    private static boolean gene_ECRICRE_GENE(Arbre a) {
        if (a.getNoeud() == Noeud.Vide)
            return false;
        boolean r = gene_ECRICRE_GENE(a.getFils1());
        Arbre exp = a.getFils2();
        if (exp.getDecor().getType() == Type.String)
            Prog.ajouter(Inst.creation1(Operation.WSTR, Operande.creationOpChaine(exp.getChaine())));
        else {
            if (!r && !memoire.isFree(Registre.R1)) {
                r = true;
                memoire.push(Registre.R1);
            }
            Operande operande = gene_Exp(exp, Registre.R1);
            if (operande.getNature() != NatureOperande.OpDirect)
                Prog.ajouter(Inst.creation2(Operation.LOAD, operande, Operande.R1));
            if (exp.getDecor().getType().getNature() == NatureType.Real)
                Prog.ajouter(Inst.creation0(Operation.WFLOAT));
            else
                Prog.ajouter(Inst.creation0(Operation.WINT));
        }
        return r;
    }

    private static void gene_LECTURE(Arbre a) {
        boolean alreadyUse = false;
        if (!memoire.isFree(Registre.R1)) {
            alreadyUse = true;
            memoire.push(Registre.R1);
        }
        if (a.getFils1().getDecor().getType().getNature() == NatureType.Real)
            Prog.ajouter(Inst.creation0(Operation.RFLOAT));
        else
            Prog.ajouter(Inst.creation0(Operation.RINT));
        Registre registre = memoire.get(Registre.R1);
        emplacement_Variable(a.getFils1(), registre);
        Prog.ajouter(Inst.creation2(Operation.STORE, Operande.R1, Operande.creationOpIndirect(0, registre)));
        memoire.free(registre);
        if (alreadyUse)
            memoire.pop(Registre.R1);
    }

    private static Operande gene_Exp(Arbre a, Registre registre) {
        switch (a.getNoeud()) {
            case Entier:
            case Reel:
            case Index:
            case Ident:
                return gene_Lecture_variable(a, registre);
            case Non:
            case MoinsUnaire:
            case PlusUnaire:
            case Conversion:
                return gene_expression_Unitaire(a, registre);
            case Et:
            case Ou:
                return gene_expression_Logique(a, registre);
            case Egal:
            case InfEgal:
            case SupEgal:
            case NonEgal:
            case Inf:
            case Sup:
                return gene_comparaison(a, registre);
            case Plus:
            case Moins:
            case Mult:
            case DivReel:
            case Reste:
            case Quotient:
                return gene_arith(a, registre);
        }
        throw new Error();
    }

    private static Operande gene_Lecture_variable(Arbre a, Registre registre) {
        switch (a.getNoeud()) {
            case Entier:
                return Operande.creationOpEntier(a.getEntier());
            case Reel:
                return Operande.creationOpReel(a.getReel());
            case Index:
                gene_Lecture_Tableau(a, registre);
                return Operande.opDirect(registre);
            case Ident:
                if (a.getChaine().equals("true"))
                    return Operande.creationOpEntier(1);
                if (a.getChaine().equals("false"))
                    return Operande.creationOpEntier(0);
                if (a.getChaine().equals("max_int"))
                    return Operande.creationOpEntier(Integer.MAX_VALUE);
                return a.getDecor().getDefn().getOperande();
        }
        throw new Error();
    }

    private static void gene_Lecture_Tableau(Arbre a, Registre registre) {
        Type type = emplacement_Variable(a, registre);
        if (type.getNature() != NatureType.Array)
            Prog.ajouter(Inst.creation2(Operation.LOAD, Operande.opDirect(registre), Operande.opDirect(registre)));
    }

    private static Operande gene_expression_Unitaire(Arbre a, Registre registre) {
        Operande operande;
        switch (a.getNoeud()) {
            case Non:
                operande = gene_Exp(a.getFils1(), registre);
                if (operande.getNature() == NatureOperande.OpEntier)
                    return Operande.creationOpEntier(1 - operande.getEntier());
                if (operande.getNature() != NatureOperande.OpDirect)
                    Prog.ajouter(Inst.creation2(Operation.LOAD, operande, Operande.opDirect(registre)));
                Prog.ajouter(Inst.creation2(Operation.CMP, Operande.creationOpEntier(0), Operande.opDirect(registre)));
                Prog.ajouter(Inst.creation1(Operation.SEQ, Operande.opDirect(registre)));
                return Operande.opDirect(registre);
            case MoinsUnaire:
                operande = gene_Exp(a.getFils1(), registre);
                if (operande.getNature() == NatureOperande.OpEntier)
                    return Operande.creationOpEntier(-operande.getEntier());
                if (operande.getNature() == NatureOperande.OpReel)
                    return Operande.creationOpReel(-operande.getReel());
                Prog.ajouter(Inst.creation2(Operation.OPP, operande, Operande.opDirect(registre)));
                return Operande.opDirect(registre);
            case PlusUnaire:
                return gene_Exp(a.getFils1(), registre);
            case Conversion:
                operande = gene_Exp(a.getFils1(), registre);
                if (operande.getNature() == NatureOperande.OpEntier)
                    return Operande.creationOpReel(operande.getEntier());
                Prog.ajouter(Inst.creation2(Operation.FLOAT, operande, Operande.opDirect(registre)));
                return Operande.opDirect(registre);
        }
        throw new Error();
    }

    private static Operande gene_expression_Logique(Arbre a, Registre registre) {
        List<Ligne> stateBefore = (List<Ligne>) ((ArrayList) Prog.instance().getListeLignes()).clone();
        Operande operande1 = gene_Exp(a.getFils1(), registre);
        if (operande1.getNature() == NatureOperande.OpEntier)
            gene_Simplification_expression_Logique(a, registre, a.getFils2(), operande1);
        Etiq autre = Etiq.nouvelle("eti" + numberEti++);
        Etiq fin = Etiq.nouvelle("eti" + numberEti++);
        if (operande1.getNature() != NatureOperande.OpDirect)
            Prog.ajouter(Inst.creation2(Operation.LOAD, operande1, Operande.opDirect(registre)));
        Prog.ajouter(Inst.creation2(Operation.CMP, Operande.creationOpEntier(0), Operande.opDirect(registre)));
        if (a.getNoeud() == Noeud.Et)
            Prog.ajouter(Inst.creation1(Operation.BEQ, Operande.creationOpEtiq(autre)));
        else
            Prog.ajouter(Inst.creation1(Operation.BEQ, Operande.creationOpEtiq(autre)));
        Operande operande2 = gene_Exp(a.getFils2(), registre);
        if (operande2.getNature() == NatureOperande.OpEntier) {
            Prog.instance().getListeLignes().clear();
            Prog.instance().getListeLignes().addAll(stateBefore);
            gene_Simplification_expression_Logique(a, registre, a.getFils1(), operande2);
        }
        if (operande2.getNature() != NatureOperande.OpDirect)
            Prog.ajouter(Inst.creation2(Operation.LOAD, operande2, Operande.opDirect(registre)));
        Prog.ajouter(Inst.creation1(Operation.BRA, Operande.creationOpEtiq(fin)));
        Prog.ajouter(autre);
        if (a.getNoeud() == Noeud.Et)
            Prog.ajouter(Inst.creation2(Operation.LOAD, Operande.creationOpEntier(0), Operande.opDirect(registre)));
        else
            Prog.ajouter(Inst.creation2(Operation.LOAD, Operande.creationOpEntier(1), Operande.opDirect(registre)));
        Prog.ajouter(fin);
        return Operande.opDirect(registre);
    }

    private static Operande gene_Simplification_expression_Logique(Arbre a, Registre registre, Arbre fils, Operande operande) {
        if (a.getNoeud() == Noeud.Et && operande.getEntier() == 0)
            return Operande.creationOpEntier(0);
        else if (a.getNoeud() == Noeud.Ou && operande.getEntier() == 1)
            return Operande.creationOpEntier(1);
        return gene_Exp(fils, registre);
    }

    private static Operande gene_comparaison(Arbre a, Registre registre) {
        List<Ligne> stateBefore = (List<Ligne>) ((ArrayList) Prog.instance().getListeLignes()).clone();
        Registre temp = memoire.get(registre);
        Operande operande1 = gene_Exp(a.getFils1(), registre);
        Operande operande2 = gene_Exp(a.getFils2(), temp);
        if (operande1.getNature() == NatureOperande.OpEntier && operande2.getNature() == NatureOperande.OpEntier) {
            memoire.free(temp);
            return gene_Simplification_Comparaison(a, operande1.getEntier(), operande2.getEntier(), stateBefore);
        }
        if (operande1.getNature() == NatureOperande.OpReel && operande2.getNature() == NatureOperande.OpReel) {
            memoire.free(temp);
            return gene_Simplification_Comparaison(a, operande1.getReel(), operande2.getReel(), stateBefore);
        }
        if (operande1.getNature() == NatureOperande.OpDirect)
            Prog.ajouter(Inst.creation2(Operation.CMP, operande2, Operande.opDirect(registre)));
        else if (operande2.getNature() == NatureOperande.OpDirect)
            Prog.ajouter(Inst.creation2(Operation.CMP, operande1, Operande.opDirect(temp)));
        else {
            Prog.ajouter(Inst.creation2(Operation.LOAD, operande1, Operande.opDirect(registre)));
            Prog.ajouter(Inst.creation2(Operation.CMP, operande2, Operande.opDirect(registre)));
        }
        if (a.getNoeud() == Noeud.Egal)
            Prog.ajouter(Inst.creation1(Operation.SEQ, Operande.opDirect(registre)));
        else if (a.getNoeud() == Noeud.InfEgal)
            Prog.ajouter(Inst.creation1(Operation.SLE, Operande.opDirect(registre)));
        else if (a.getNoeud() == Noeud.SupEgal)
            Prog.ajouter(Inst.creation1(Operation.SGE, Operande.opDirect(registre)));
        else if (a.getNoeud() == Noeud.NonEgal)
            Prog.ajouter(Inst.creation1(Operation.SNE, Operande.opDirect(registre)));
        else if (a.getNoeud() == Noeud.Inf)
            Prog.ajouter(Inst.creation1(Operation.SLT, Operande.opDirect(registre)));
        else if (a.getNoeud() == Noeud.Sup)
            Prog.ajouter(Inst.creation1(Operation.SGT, Operande.opDirect(registre)));
        memoire.free(temp);
        return Operande.opDirect(registre);
    }

    private static Operande gene_Simplification_Comparaison(Arbre a, float operande1, float operande2, List<Ligne> stateBefore) {
        Prog.instance().getListeLignes().clear();
        Prog.instance().getListeLignes().addAll(stateBefore);
        if (a.getNoeud() == Noeud.Egal)
            return operande1 == operande2 ? Operande.creationOpEntier(1) : Operande.creationOpEntier(0);
        if (a.getNoeud() == Noeud.InfEgal)
            return operande1 <= operande2 ? Operande.creationOpEntier(1) : Operande.creationOpEntier(0);
        if (a.getNoeud() == Noeud.SupEgal)
            return operande1 >= operande2 ? Operande.creationOpEntier(1) : Operande.creationOpEntier(0);
        if (a.getNoeud() == Noeud.NonEgal)
            return operande1 != operande2 ? Operande.creationOpEntier(1) : Operande.creationOpEntier(0);
        if (a.getNoeud() == Noeud.Inf)
            return operande1 < operande2 ? Operande.creationOpEntier(1) : Operande.creationOpEntier(0);
        if (a.getNoeud() == Noeud.Sup)
            return operande1 > operande2 ? Operande.creationOpEntier(1) : Operande.creationOpEntier(0);
        throw new Error();
    }


    private static Operande gene_arith(Arbre a, Registre registre) {
        if (memoire.getNumberFree() >= 2) {
            Operande operande1 = gene_Exp(a.getFils1(), registre);
            if (operande1.getNature() != NatureOperande.OpDirect) {
                Prog.ajouter(Inst.creation2(Operation.LOAD, operande1, Operande.opDirect(registre)));
                operande1 = Operande.opDirect(registre);
            }
            Registre registre1 = memoire.get(registre);
            Operande operande2 = gene_Exp(a.getFils2(), registre1);
            gene_arith(a, operande2, operande1);
            memoire.free(registre1);
            return Operande.opDirect(registre);
        } else {
            Operande operande = gene_Exp(a.getFils2(), registre);
            if (operande.getNature() != NatureOperande.OpDirect) {
                Operande operande2 = gene_Exp(a.getFils1(), registre);
                if (operande2.getNature() != NatureOperande.OpDirect) {
                    Prog.ajouter(Inst.creation2(Operation.LOAD, operande2, Operande.opDirect(registre)));
                    operande2 = Operande.opDirect(registre);
                }
                gene_arith(a, operande, operande2);
                return Operande.opDirect(registre);
            }
            Operande temp = memoire.createVariableTemp();
            Prog.ajouter(Inst.creation2(Operation.STORE, Operande.opDirect(registre), temp));
            operande = gene_Exp(a.getFils1(), registre);
            if (operande.getNature() != NatureOperande.OpDirect) {
                Prog.ajouter(Inst.creation2(Operation.LOAD, operande, Operande.opDirect(registre)));
            }
            gene_arith(a, temp, Operande.opDirect(registre));
            return Operande.opDirect(registre);
        }
    }

    private static void gene_arith(Arbre a, Operande operande1, Operande operande2) {
        if (a.getNoeud() == Noeud.Plus)
            Prog.ajouter(Inst.creation2(Operation.ADD, operande1, operande2));
        else if (a.getNoeud() == Noeud.Moins)
            Prog.ajouter(Inst.creation2(Operation.SUB, operande1, operande2));
        else if (a.getNoeud() == Noeud.Mult)
            Prog.ajouter(Inst.creation2(Operation.MUL, operande1, operande2));
        else {
            gene_Test_Division_0(operande1);
            if (a.getNoeud() == Noeud.Reste)
                Prog.ajouter(Inst.creation2(Operation.MOD, operande1, operande2));
            if (a.getNoeud() == Noeud.Quotient || a.getNoeud() == Noeud.DivReel) {
                Prog.ajouter(Inst.creation2(Operation.DIV, operande1, operande2));
            }
        }
    }

    private static void gene_Test_Division_0(Operande operande1) {
        Etiq fin = Etiq.nouvelle("eti" + numberEti++);
        Registre registre = null;
        if (operande1.getNature() != NatureOperande.OpDirect) {
            registre = memoire.get();
            Prog.ajouter(Inst.creation2(Operation.LOAD, operande1, Operande.opDirect(registre)));
            Prog.ajouter(Inst.creation2(Operation.CMP, Operande.creationOpEntier(0), Operande.opDirect(registre)));

        } else Prog.ajouter(Inst.creation2(Operation.CMP, Operande.creationOpEntier(0), operande1));
        Prog.ajouter(Inst.creation1(Operation.BEQ, Operande.creationOpEtiq(fin)));
        Prog.ajouter(Inst.creation1(Operation.WSTR, Operande.creationOpChaine("division par zero")));
        Prog.ajouter(Inst.creation0(Operation.WNL));
        Prog.ajouter(Inst.creation0(Operation.HALT));
        Prog.ajouter(fin);
        if (registre != null)
            memoire.free(registre);

    }
}
