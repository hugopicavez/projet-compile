package fr.esisar.compilation.gencode;

import fr.esisar.compilation.global.src3.Registre;

import java.sql.Array;
import java.util.Arrays;

public class PullRegistre {
    private boolean pullregistre[]=new boolean[16];
    public PullRegistre(){

    }
    public Registre checkregistre(){
        Registre resultat=null;
        int indice=-1;
        for(int i=0;i<16 && pullregistre[i] == true;i++){
            indice=i;
        }
        if (pullregistre[indice]==false){
            pullregistre[indice]=true;
            resultat=Registre.values()[indice];
        }
        else{
            resultat=null;
        }
        return resultat;
    }

    public void libere(Registre r){
        int indice=Arrays.binarySearch(Registre.values(),r);
        if(indice != -1){
            pullregistre[indice]=false;
        }
    }
}
