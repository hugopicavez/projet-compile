package fr.esisar.compilation.gencode;

import com.sun.org.apache.bcel.internal.generic.BREAKPOINT;
import com.sun.org.apache.regexp.internal.RE;
import com.sun.xml.internal.ws.wsdl.writer.document.OpenAtts;
import fr.esisar.compilation.global.src.*;
import fr.esisar.compilation.global.src3.*;
import fr.esisar.compilation.verif.ErreurContext;
import fr.esisar.compilation.verif.ErreurInterneVerif;
import fr.esisar.compilation.verif.ErreurVerif;

import javax.naming.BinaryRefAddr;

/**
 * Génération de code pour un programme JCas à partir d'un arbre décoré.
 */

class Generation {

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

        Prog.instance().getListeLignes().add(0, new Ligne(null, Inst.creation1(Operation.ADDSP,
                Operande.creationOpEntier(gene_LISTE_DECL(a, 0))), null));

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

    private static int gene_TABLEAU(Type type, int index){
        int start = index;
        int size =  type.getIndice().getBorneSup() - type.getIndice().getBorneInf();
        index += size;
        if(type.getElement().getNature() == NatureType.Array){
            for(int i = start; i < start+size; i++){
                Prog.ajouter(Inst.creation2(Operation.LOAD, Operande.creationOpEntier(index), Operande.R0));
                Prog.ajouter(Inst.creation2(Operation.STORE, Operande.R0, Operande.creationOpIndirect(i, Registre.GB)));
                index = gene_TABLEAU(type.getElement(), index);
            }
        }
        return index;
    }

    private void gene_Exp(Arbre a, Registre registre) {
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
                boolean alreadyUse = false;
                Registre temp = pullRegistre.checkregistre();
                if(temp == null) {
                    temp = pullRegistre.getRegistreAlreadyUse(registre);
                    alreadyUse = true;
                    Prog.ajouter(Inst.creation1(Operation.PUSH, Operande.opDirect(temp)));
                }
                gene_Exp(a.getFils1(), temp);
                Prog.ajouter(Inst.creation2(Operation.SUB, Operande.opDirect(temp), Operande.opDirect(registre)));
                if(alreadyUse)
                    Prog.ajouter(Inst.creation1(Operation.POP, Operande.opDirect(temp)));
                else
                    pullRegistre.libere(temp);
            case PlusUnaire:
                gene_Exp(a.getFils1(), registre);
                break;
            case Et:
            case Ou:
            case Egal:
            case InfEgal:
            case SupEgal:
            case NonEgal:
            case Inf:
            case Sup:
            case Plus:
            case Moins:
            case Mult:
            case DivReel:
            case Reste:
            case Quotient:
                break;
        }

    }

    private void gene_Lecture_Tableau(Arbre a, Registre registre){
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
                if(temp == null) {
                    temp = pullRegistre.getRegistreAlreadyUse(registre);
                    alreadyUse = true;
                    Prog.ajouter(Inst.creation1(Operation.PUSH, Operande.opDirect(temp)));
                }
                Prog.ajouter(Inst.creation2(Operation.LOAD,
                        Operande.creationOpIndexe(0, registre, temp),
                        Operande.opDirect(registre)));
                if(alreadyUse)
                    Prog.ajouter(Inst.creation1(Operation.POP, Operande.opDirect(temp)));
                else
                    pullRegistre.libere(temp);
                break;
            default:
                throw new ErreurInterneVerif("Place : " + a.getFils1().getNumLigne());
        }
    }

}



