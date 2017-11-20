package fr.esisar.compilation.gencode;

import java.util.ArrayList;
import java.util.Arrays;

import fr.esisar.compilation.global.src3.*;

public class Memoire {
    private boolean registres[] = new boolean[16];
    private ArrayList<Boolean> pile = new ArrayList<>();
    private int pillUse = 0;

    public void setPileUse(int pileUse){
        this.pillUse = pileUse;
    }

    public void pop(Registre registre){
        this.pillUse++;
        Prog.ajouter(Inst.creation1(Operation.POP, Operande.opDirect(registre)));
    }

    public void push(Registre registre){
        this.pillUse--;
        testePile(1);
        Prog.ajouter(Inst.creation1(Operation.PUSH, Operande.opDirect(registre)));
    }

    public Operande createVariableTemp() {
        Prog.ajouter(Inst.creation1(Operation.ADDSP,
                Operande.creationOpEntier(1)));
        return Operande.creationOpIndirect(this.pillUse++, Registre.GB);
    }

    public void freeVariableTemp(){
        Prog.ajouter(Inst.creation1(Operation.SUBSP,
                Operande.creationOpEntier(1)));
        this.pillUse--;
    }

    public Registre get(Registre... needs) {
        for (int i = 0; i < 16; i++) {
            if (registres[i] == false) {
                registres[i] = true;
                pile.add(true);
                return Registre.values()[i];
            }
        }
        pile.add(false);
        testePile(1);
        for (int i = 0; i < this.registres.length; i++)
            if (Arrays.binarySearch(needs, Registre.values()[i]) == -1) {
                Registre registre = Registre.values()[i];
                Prog.ajouter(Inst.creation1(Operation.PUSH, Operande.opDirect(registre)));
                return registre;
            }
        return null;
    }

    public void free(Registre r) {
        int indice = Arrays.binarySearch(Registre.values(), r);
        if (pile.remove(pile.size() - 1))
            registres[indice] = false;
        else
            Prog.ajouter(Inst.creation1(Operation.POP, Operande.opDirect(r)));
    }


    public boolean isFree(Registre r) {
        return !this.registres[Arrays.binarySearch(Registre.values(), r)];
    }

    public int getNumberFree(){
        int number = 0;
        for (int i = 0; i < 16; i++)
            if(!registres[i])
                number++;
        return number;
    }

    public void testePile(int size){
        Etiq error = Etiq.nouvelle("eti" + Generation.numberEti++);
        Etiq fin = Etiq.nouvelle("eti" + Generation.numberEti++);
        Prog.ajouter(Inst.creation1(Operation.TSTO, Operande.creationOpEntier(size)));
        Prog.ajouter(Inst.creation1(Operation.BOV, Operande.creationOpEtiq(error)));
        Prog.ajouter(Inst.creation1(Operation.BRA, Operande.creationOpEtiq(fin)));
        Prog.ajouter(error);
        Prog.ajouter(Inst.creation1(Operation.WSTR, Operande.creationOpChaine("erreur debordement")));
        Prog.ajouter(Inst.creation0(Operation.WNL));
        Prog.ajouter(Inst.creation0(Operation.HALT));
        Prog.ajouter(fin);
    }

}
