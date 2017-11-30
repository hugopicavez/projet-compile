package fr.esisar.compilation.gencode;

import java.util.ArrayList;
import java.util.Arrays;

import fr.esisar.compilation.global.src3.*;

public class Memoire {
    private boolean registres[] = new boolean[16];
    private ArrayList<Boolean> pile = new ArrayList<>();
    private long pillUse = 0;

    public void setPileUse(long pileUse){
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
        return Operande.creationOpIndirect((int)++this.pillUse, Registre.GB);
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
            if (content(Registre.values()[i], needs)) {
                Registre registre = Registre.values()[i];
                Prog.ajouter(Inst.creation1(Operation.PUSH, Operande.opDirect(registre)));

                return registre;
            }
        return null;
    }

    public void reserveRegistre(Registre r){
        int indice = Arrays.binarySearch(Registre.values(), r);
        if (this.registres[indice]) {
            pile.add(false);
            testePile(1);
            Prog.ajouter(Inst.creation1(Operation.PUSH, Operande.opDirect(r)));
        }
        else
            pile.add(true);
        this.registres[indice] = true;
    }

    private boolean content(Registre registre, Registre[] needs){
        for(int i = 0; i < needs.length; i++)
            if(registre.equals(needs[i]))
                return false;
        return true;
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

    public void reservePile(long value){
        while(value > 0){
            int place = (int) Math.min(value, Integer.MAX_VALUE);
            testePile(place);
            Prog.ajouter(Inst.creation1(Operation.ADDSP,
                    Operande.creationOpEntier(place)));
            value = value - place;
        }
    }

    public void testePile(int size){
        Etiq error = Etiq.nouvelle("eti");
        Etiq fin = Etiq.nouvelle("eti");
        Prog.ajouter(Inst.creation1(Operation.TSTO, Operande.creationOpEntier(size)));
        Prog.ajouter(Inst.creation1(Operation.BOV, Operande.creationOpEtiq(error)));
        Prog.ajouter(Inst.creation1(Operation.BRA, Operande.creationOpEtiq(fin)));
        Prog.ajouter(error);
        Prog.ajouter(Inst.creation1(Operation.WSTR, Operande.creationOpChaine("E03 erreur debordement de la pile")));
        Prog.ajouter(Inst.creation0(Operation.WNL));
        Prog.ajouter(Inst.creation0(Operation.HALT));
        Prog.ajouter(fin);
    }


    public void freePile() {
        while(this.pillUse > 0){
            int place = (int) Math.min(this.pillUse, Integer.MAX_VALUE);
            Prog.ajouter(Inst.creation1(Operation.SUBSP,
                    Operande.creationOpEntier(place)));
            this.pillUse = this.pillUse - place;
        }
    }
}
