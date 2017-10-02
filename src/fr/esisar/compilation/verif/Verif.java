package fr.esisar.compilation.verif;

import fr.esisar.compilation.global.src.*;

/**
 * Cette classe permet de réaliser la vérification et la décoration
 * de l'arbre abstrait d'un programme.
 */
public class Verif {

    private Environ env; // L'environnement des identificateurs

    /**
     * Constructeur.
     */
    public Verif() {
        env = new Environ();
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
                        "Arbre incorrect dans verifier_LISTE_INST");
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
                        "Arbre incorrect dans verifier_LISTE_INST");
        }
    }

    private void verifier_LISTE_IDF(Arbre a, Type type) throws ErreurVerif {
        switch (a.getNoeud()) {
            case Vide:
                break;
            case ListeDecl:
                verifier_LISTE_IDF(a.getFils1(), type);
                Defn defn = Defn.creationVar(type);
                if (env.enrichir(a.getFils2().getChaine(), defn))
                    throw new ErreurReglesTypage();
                break;
            default:
                throw new ErreurInterneVerif(
                        "Arbre incorrect dans verifier_LISTE_INST");
        }
    }

    private Type verifier_TYPE(Arbre a) throws ErreurVerif {
        ErreurContext.ErreurNonRepertoriee.leverErreurContext("",1);
        switch (a.getNoeud()) {
            case Ident:
                Defn defn = env.chercher(a.getChaine());
                if (defn == null || defn.getNature() != NatureDefn.Type)
                    throw new ErreurReglesTypage();
                return defn.getType();
            case Intervalle:
                return verifier_INTERVALLE(a);
            case Tableau:
                Type intervale = verifier_INTERVALLE(a.getFils1());
                return Type.creationArray(intervale, verifier_TYPE(a.getFils2()));
            default:
                throw new ErreurInterneVerif(
                        "Arbre incorrect dans verifier_LISTE_INST");
        }

    }

    private Type verifier_INTERVALLE(Arbre a) throws ErreurVerif {
        if (a.getNoeud() != Noeud.Intervalle)
            throw new ErreurInterneVerif(
                    "Arbre incorrect dans verifier_LISTE_INST");
        int inf = verifier_CONSTANTE(a.getFils1());
        int sup = verifier_CONSTANTE(a.getFils2());
        return Type.creationInterval(inf, sup);
    }

    private int verifier_CONSTANTE(Arbre a) {
        switch (a.getNoeud()) {
            case PlusUnaire:
                return verifier_CONSTANTE(a.getFils1());
            case MoinsUnaire:
                return -verifier_CONSTANTE(a.getFils1());
            case Entier:
                return -a.getEntier();
            case Ident:
                Defn defn = env.chercher(a.getChaine());
                if (defn == null)
                    throw new ErreurReglesTypage();
                return defn.getValeurInteger();
            default:
                throw new ErreurInterneVerif(
                        "Arbre incorrect dans verifier_LISTE_INST");
        }
    }

    /**************************************************************************
     * LISTE_INST
     **************************************************************************/
    private void verifier_LISTE_INST(Arbre a) throws ErreurVerif {
        // A COMPLETER
    }

    // ------------------------------------------------------------------------
    // COMPLETER les operations de vérifications et de décoration pour toutes
    // les constructions d'arbres
    // ------------------------------------------------------------------------

}
