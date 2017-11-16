package fr.esisar.compilation.gencode;

import java.util.ArrayList;
import java.util.Arrays;

import fr.esisar.compilation.global.src3.Inst;
import fr.esisar.compilation.global.src3.Operande;
import fr.esisar.compilation.global.src3.Operation;
import fr.esisar.compilation.global.src3.Prog;
import fr.esisar.compilation.global.src3.Registre;

public class Memoire {
	private boolean registres[]=new boolean[16];
	private ArrayList<Boolean> pile=new ArrayList<Boolean>();
	
	public Registre getregistres(Registre ... need) {
		Registre resultat=null;
		int indice=-1;
		
        for(int i=0;i<16 && registres[i] == true;i++){
            indice=i;
        }
        
        if (registres[indice]==false){
            registres[indice]=true;
            resultat=Registre.values()[indice];
            pile.add(true);
        }
        else {
            for(int i = 0; i < this.registres.length; i++)
                if(Arrays.binarySearch(need, Registre.values()[i]) == -1)
                    resultat = Registre.values()[i];
            pile.add(false);
        }
        
        
		return resultat;
	}
	
    public void libere(Registre r){
        int indice= Arrays.binarySearch(Registre.values(),r);
        
        if (pile.get(pile.size()-1))
        	registres[indice]=false;
        else
        	Prog.ajouter(Inst.creation1(Operation.POP, Operande.opDirect(r)));
            
        pile.remove(pile.size()-1);
    }
}
