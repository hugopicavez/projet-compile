package fr.esisar.compilation.verif;

import fr.esisar.compilation.global.src.*;

import java.util.ArrayList;

/**
 * Cette classe permet de réaliser la vérification et la décoration
 * de l'arbre abstrait d'un programme.
 */
public class Verif {

    private Environ env; // L'environnement des identificateurs
    private ReglesTypage reglesType;


    /**
     * Constructeur.
     */
    public Verif() {
        env = new Environ();
        reglesType = new ReglesTypage();
    }

    /**
     * Vérifie les contraintes contextuelles du programme correspondant à
     * l'arbre abstrait a, qui est décoré et enrichi.
     * Les contraintes contextuelles sont décrites
     * dans Context.txt.
     * En cas d'erreur contextuelle, un message d'erreur est affiché et
     * l'exception ErreurVerif est levée.
     */
    public void verifierDecorer(Arbre a) throws ErreurVerif {
        verifier_PROGRAMME(a);
        ajout_Decor(a);
    }

    private void ajout_Decor(Arbre a) {
        ArrayList<Arbre> arbres = new ArrayList<>();
        arbres.add(a);
        while (arbres.size() != 0) {
            a = arbres.remove(0);
            if (a.getDecor() == null)
                a.setDecor(new Decor());
            for (int i = 1; i <= a.getArite(); i++)
                arbres.add(a.getFils(i));
        }
    }

    /**
     * Initialisation de l'environnement avec les identificateurs prédéfinis.
     */
    private void initialiserEnv() {
        Defn def;

        //boolean
        def = Defn.creationType(Type.Boolean);
        def.setGenre(Genre.PredefBoolean);
        env.enrichir("boolean", def);

        //false
        def = Defn.creationConstBoolean(false);
        def.setGenre(Genre.PredefFalse);
        env.enrichir("false", def);

        //true
        def = Defn.creationConstBoolean(true);
        def.setGenre(Genre.PredefTrue);
        env.enrichir("true", def);

        // integer
        def = Defn.creationType(Type.Integer);
        def.setGenre(Genre.PredefInteger);
        env.enrichir("integer", def);

        //max_int
        def = Defn.creationConstInteger(Integer.MAX_VALUE);
        def.setGenre(Genre.PredefMaxInt);
        env.enrichir("max_int", def);

        //real
        def = Defn.creationType(Type.Real);
        def.setGenre(Genre.PredefReal);
        env.enrichir("real", def);

    }

    /**************************************************************************
     * PROGRAMME
     **************************************************************************/
    private void verifier_PROGRAMME(Arbre a) throws ErreurVerif {
        initialiserEnv();
        verifier_LISTE_DECL(a.getFils1());
        verifier_LISTE_INST(a.getFils2());
    }

    /**************************************************************************
     * LISTE_DECL
     **************************************************************************/
    private void verifier_LISTE_DECL(Arbre a) throws ErreurVerif {
        switch (a.getNoeud()) {
            case Vide:
                break;
            case ListeDecl:
                verifier_LISTE_DECL(a.getFils1());
                verifier_DECL(a.getFils2());
                break;
            default:
                throw new ErreurInterneVerif(
                        "Arbre incorrect dans verifier_LISTE_DECL");
        }
    }

    private void verifier_DECL(Arbre a) throws ErreurVerif {
        switch (a.getNoeud()) {
            case Decl:
                Type type = verifier_TYPE(a.getFils2());
                verifier_LISTE_IDF(a.getFils1(), type);
                break;
            default:
                throw new ErreurInterneVerif(
                        "Arbre incorrect dans verifier_DECL");
        }
    }

    private void verifier_LISTE_IDF(Arbre a, Type type) throws ErreurVerif {
        switch (a.getNoeud()) {
            case Vide:
                break;
            case ListeIdent:
                verifier_LISTE_IDF(a.getFils1(), type);
                Defn defn = Defn.creationVar(type);
                a.getFils2().setDecor(new Decor(defn));
                if (env.enrichir(a.getFils2().getChaine(), defn))
                    ErreurContext.ErreurVariableDejaDefinit.leverErreurContext(a.getFils2().getChaine(), a.getNumLigne());
                break;
            default:
                throw new ErreurInterneVerif(
                        "Arbre incorrect dans verifier_LISTE_IDF");
        }
    }

    private Type verifier_TYPE(Arbre a) throws ErreurVerif {
        switch (a.getNoeud()) {
            case Ident:
                Defn defn = env.chercher(a.getChaine());
                if (defn == null || defn.getNature() != NatureDefn.Type)
                    ErreurContext.ErreurTypeInvalid.leverErreurContext(a.getChaine(), a.getNumLigne());
                a.setDecor(new Decor(defn));
                return defn.getType();
            case Intervalle:
                return verifier_INTERVALLE(a);
            case Tableau:
                Type intervale = verifier_INTERVALLE(a.getFils1());
                return Type.creationArray(intervale, verifier_TYPE(a.getFils2()));
            default:
                throw new ErreurInterneVerif(
                        "Arbre incorrect dans verifier_TYPE");
        }

    }

    private Type verifier_INTERVALLE(Arbre a) throws ErreurVerif {
        if (a.getNoeud() != Noeud.Intervalle)
            throw new ErreurInterneVerif(
                    "Arbre incorrect dans verifier_INTERVALLE");
        int inf = verifier_CONSTANTE(a.getFils1());
        int sup = verifier_CONSTANTE(a.getFils2());
        return Type.creationInterval(inf, sup);
    }

    private int verifier_CONSTANTE(Arbre a) throws ErreurVerif {
        switch (a.getNoeud()) {
            case PlusUnaire:
                return verifier_CONSTANTE(a.getFils1());
            case MoinsUnaire:
                return -verifier_CONSTANTE(a.getFils1());
            case Entier:
                return a.getEntier();
            case Ident:
                Defn defn = env.chercher(a.getChaine());
                if (defn == null || defn.getNature() != NatureDefn.ConstInteger)
                      ErreurContext.ErreurConstanteInvalide.leverErreurContext(a.getChaine(), a.getNumLigne());
                a.setDecor(new Decor(defn));
                return defn.getValeurInteger();
            default:
                throw new ErreurInterneVerif(
                        "Arbre incorrect dans verifier_CONSTANTE");
        }
    }

    /**************************************************************************
     * LISTE_INST
     **************************************************************************/
    private void verifier_LISTE_INST(Arbre a) throws ErreurVerif {
        switch (a.getNoeud()) {
            case Vide:
                break;
            case ListeInst:
                verifier_LISTE_INST(a.getFils1());
                verifier_INST(a.getFils2());
                break;
            default:
                throw new ErreurInterneVerif("Arbre incorrect dans verifier_LISTE_INST");
        }
    }


    private void verifier_INST(Arbre a) throws ErreurVerif {
        switch (a.getNoeud()) {
            case Nop:
                break;
            case Affect:
                verifier_AFFECT(a);
                break;
            case Pour:
                verifier_POUR(a);
                break;
            case TantQue:
                verifier_TANTQUE(a);
                break;
            case Si:
                verifier_SI(a);
                break;
            case Ecriture:
                verifier_ECRITURE(a);
                break;
            case Lecture:
                verifier_LECTURE(a);
                break;
            case Ligne:
                break;
            default:
                throw new ErreurInterneVerif("Arvre incorrect dans verifier_INST");
        }
    }

    private void verifier_AFFECT(Arbre a) throws ErreurVerif {
        verifier_PLACE(a.getFils1());
        verifier_EXP(a.getFils2());

        ResultatAffectCompatible affectComp = reglesType.affectCompatible(a.getFils1().getDecor().getType(), a.getFils2().getDecor().getType());
        if (affectComp.getOk()) {
            if (affectComp.getConv2()) {
                a.setFils2(Arbre.creation1(Noeud.Conversion, a.getFils2(), a.getFils2().getNumLigne()));
                a.getFils2().setDecor(new Decor(Type.Real));
            }
            a.setDecor(new Decor(a.getFils1().getDecor().getType()));
        } else
            ErreurContext.ErreurTypeNonCompatible.leverErreurContext("=> Affect", a.getNumLigne());
    }

    private void verifier_POUR(Arbre a) throws ErreurVerif {
        verifier_PAS(a.getFils1());
        verifier_LISTE_INST(a.getFils2());
    }


    private void verifier_PAS(Arbre a) throws ErreurVerif{
        if (! (a.getNoeud() == Noeud.Increment || a.getNoeud().equals(Noeud.Decrement)))
          throw new ErreurInterneVerif("PAS :  "+a.getNumLigne());
        verifier_IDF(a.getFils1());
        Defn defn1 = env.chercher(a.getFils1().getChaine());
        if (defn1 == null || defn1.getNature() != NatureDefn.Var )
            throw new ErreurReglesTypage();
        verifier_EXP(a.getFils2());
        verifier_EXP(a.getFils3());
        NatureType natureTypeExp2 = a.getFils2().getDecor().getType().getNature() ;
        NatureType natureTypeExp3 = a.getFils3().getDecor().getType().getNature() ;
         if( !( natureTypeExp2 == NatureType.Interval && natureTypeExp3 == NatureType.Interval))
            ErreurContext.ErreurTypeNonCompatible.leverErreurContext( "pas -->",  a.getNumLigne());
  }

    private void verifier_TANTQUE(Arbre a) throws ErreurVerif {
        verifier_EXP(a.getFils1());
        if (Type.Boolean != a.getFils1().getDecor().getType())
            ErreurContext.ErreurTypeBooleanAttendu.leverErreurContext("While -->", a.getNumLigne());
        verifier_LISTE_INST(a.getFils2());

    }


    private void verifier_SI(Arbre a) throws ErreurVerif {
        verifier_EXP(a.getFils1());
        if (Type.Boolean != a.getFils1().getDecor().getType())
            ErreurContext.ErreurTypeBooleanAttendu.leverErreurContext("Si -->", a.getNumLigne());
        verifier_LISTE_INST(a.getFils2());
        verifier_LISTE_INST(a.getFils3());
    }


    private void verifier_ECRITURE(Arbre a) throws ErreurVerif {
        verifier_LISTE_EXP(a.getFils1());
        Arbre listeEXP = a.getFils1();
        while (Noeud.Vide != listeEXP.getNoeud()) {
            NatureType natureTypeExp = listeEXP.getFils2().getDecor().getType().getNature();
            if (!(natureTypeExp == NatureType.Real || natureTypeExp == NatureType.String || natureTypeExp == NatureType.Interval))
                ErreurContext.ErreurTypeNonCompatible.leverErreurContext("write -->", a.getNumLigne());
            listeEXP = listeEXP.getFils1();
        }
    }

    private void verifier_LISTE_EXP(Arbre a) throws ErreurVerif {
        switch (a.getNoeud()) {
            case ListeExp:
                verifier_LISTE_EXP(a.getFils1());
                verifier_EXP(a.getFils2());
                break;
            case Vide:
                break;
            default:
                throw new ErreurInterneVerif("Arbre incorrect dans verifier_LISTE_EXP");
        }
    }

    private void verifier_LECTURE(Arbre a) throws ErreurVerif {
        verifier_PLACE(a.getFils1());
        NatureType natureTypeExp = a.getFils1().getDecor().getType().getNature();
        if (!(natureTypeExp == NatureType.Real || natureTypeExp == NatureType.Interval))
            ErreurContext.ErreurTypeNonCompatible.leverErreurContext("read -->", a.getNumLigne());
    }


    private void verifier_PLACE(Arbre a) throws ErreurVerif, ErreurInterneVerif {
        switch (a.getNoeud()) {
            case Ident:
                verifier_IDF(a);
                break;
            case Index:
                verifier_INDEX(a);
                break;
            default:
                throw new ErreurInterneVerif("Place : " + a.getFils1().getNumLigne());
        }
    }

    private void verifier_INDEX(Arbre a) throws ErreurVerif {
        verifier_PLACE(a.getFils1());
        verifier_EXP(a.getFils2());
        if(a.getFils2().getDecor().getType().getNature() != NatureType.Interval)
            ErreurContext.ErreurTypeNonCompatible.leverErreurContext("", a.getNumLigne());
        if(a.getFils1().getDecor().getType().getNature() != NatureType.Array)
            ErreurContext.ErreurTypeNonCompatible.leverErreurContext("", a.getNumLigne());
        a.setDecor(new Decor(a.getFils1().getDecor().getType().getElement()));
    }


    private void verifier_EXP(Arbre a) throws  ErreurVerif{
        switch (a.getNoeud()) {
            case Entier:
            case Reel:
            case Index:
            case Ident:
            case Chaine:
                this.verifier_FACTEUR(a);
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
                this.verifier_DEUX_EXP(a);
                break;
            case Non:
            case MoinsUnaire:
            case PlusUnaire:
                this.verifier_UNAIRE(a);
                break;
            default:
              throw new ErreurInterneVerif("Arbre incorrect dans verifier_EXP");
        }
    }

    private void verifier_FACTEUR(Arbre a) throws ErreurVerif {
        switch (a.getNoeud()) {
            case Entier:
                a.setDecor(new Decor(Type.Integer));
                break;
            case Reel:
                a.setDecor(new Decor(Type.Real));
                break;
            case Chaine:
                a.setDecor(new Decor(Type.String));
                break;
            case Index:
                verifier_PLACE(a);
                break;
            case Ident:
                Defn defn = env.chercher(a.getChaine());
                if (defn == null)
                  ErreurContext.ErreurVariableInconnue.leverErreurContext("", a.getNumLigne());
                if(defn.getNature() == NatureDefn.Type )
                    ErreurContext.ErreurIdentificateurInvalide.leverErreurContext("", a.getNumLigne());
                a.setDecor(new Decor(defn, defn.getType()));
                break;
            default:
                throw new ErreurInterneVerif("Facteur : " + a.getNumLigne());
        }
    }

    private void verifier_DEUX_EXP(Arbre a) throws ErreurVerif {
        verifier_EXP(a.getFils1());
        verifier_EXP(a.getFils2());
        Type t1 = a.getFils1().getDecor().getType();
        Type t2 = a.getFils2().getDecor().getType();
        ResultatBinaireCompatible res = reglesType.binaireCompatible(a.getNoeud(), t1, t2);
        if (res.getOk() == true) {
            if (res.getConv1() == true) {
                a.setFils1(Arbre.creation1(Noeud.Conversion, a.getFils1(), a.getFils1().getNumLigne()));
                a.getFils1().setDecor(new Decor(Type.Real));
            }
            if (res.getConv2() == true) {
                a.setFils2(Arbre.creation1(Noeud.Conversion, a.getFils2(), a.getFils2().getNumLigne()));
                a.getFils2().setDecor(new Decor(Type.Real));
            }
            a.setDecor(new Decor(res.getTypeRes()));
        } else
            ErreurContext.ErreurTypeNonCompatible.leverErreurContext(a.getNoeud() + "=>" + "(" + t1.toString() + "," + t2.toString() + ")", a.getNumLigne());

    }

    private void verifier_UNAIRE(Arbre a) throws ErreurVerif { 
        verifier_FACTEUR(a.getFils1());
        Type t = a.getFils1().getDecor().getType();
        ResultatUnaireCompatible res2 = reglesType.unaireCompatible(a.getNoeud(), t);
        if (res2.getOk() == true) {
            a.setDecor(new Decor(res2.getTypeRes()));
        } else {
            ErreurContext err = ErreurContext.ErreurTypeNonCompatible;
            err.leverErreurContext(res2.getTypeRes().getNature() + "=>" + "(" + t.getNature() + ")", a.getNumLigne());
        }

    }

    private void verifier_IDF(Arbre a) throws ErreurVerif {
        if (!a.getNoeud().equals(Noeud.Ident))
            throw new ErreurInterneVerif("Idf : " + a.getNumLigne());
        Defn defn = env.chercher(a.getChaine());
        if(defn == null)
            ErreurContext.ErreurVariableInconnue.leverErreurContext(a.getChaine(), a.getNumLigne());
        if(defn.getNature() != NatureDefn.Var)
            ErreurContext.ErreurIdentificateurInvalide.leverErreurContext(a.getChaine(), a.getNumLigne());
        a.setDecor(new Decor(defn, defn.getType()));
    }
}
