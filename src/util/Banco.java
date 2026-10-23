package util;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import modelo.Pet;
import modelo.Servico;
import modelo.Tutor;

public class Banco {
    private static final String BANCO_ARQUIVO = "lavapet.db4o";
    private static ObjectContainer manager = null;

    public static EmbeddedConfiguration obterConfiguracao() {
        EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();

        config.common().objectClass(Tutor.class).cascadeOnUpdate(true);
        config.common().objectClass(Tutor.class).cascadeOnActivate(true);

        config.common().objectClass(Pet.class).cascadeOnUpdate(true);
        config.common().objectClass(Pet.class).cascadeOnActivate(true);

        config.common().objectClass(Servico.class).cascadeOnUpdate(true);
        config.common().objectClass(Servico.class).cascadeOnActivate(true);

        // Índices em campos chave para busca rápida
        config.common().objectClass(Tutor.class).objectField("id").indexed(true);
        config.common().objectClass(Tutor.class).objectField("nome").indexed(true);
        config.common().objectClass(Pet.class).objectField("id").indexed(true);
        config.common().objectClass(Pet.class).objectField("nome").indexed(true);
        config.common().objectClass(Servico.class).objectField("id").indexed(true);

        return config;
    }

    public static ObjectContainer conectar() {
        if (manager == null || manager.ext().isClosed()) {
            manager = Db4oEmbedded.openFile(obterConfiguracao(), BANCO_ARQUIVO);
        }
        return manager;
    }

    public static void desconectar() {
        if (manager != null && !manager.ext().isClosed()) {
            manager.close();
            manager = null;
        }
    }

    public static int gerarIdTutor(ObjectContainer db) {
        List<Tutor> lista = db.query(Tutor.class);
        int max = 0;
        for (Tutor t : lista) {
            if (t.getId() > max) {
                max = t.getId();
            }
        }
        return max + 1;
    }

    public static int gerarIdPet(ObjectContainer db) {
        List<Pet> lista = db.query(Pet.class);
        int max = 0;
        for (Pet p : lista) {
            if (p.getId() > max) {
                max = p.getId();
            }
        }
        return max + 1;
    }

    public static int gerarIdServico(ObjectContainer db) {
        List<Servico> lista = db.query(Servico.class);
        int max = 0;
        for (Servico s : lista) {
            if (s.getId() > max) {
                max = s.getId();
            }
        }
        return max + 1;
    }

    public static <T> List<T> query(ObjectContainer db, Class<T> tipo, Predicate<T> filtro) {
        List<T> base = db.query(tipo);
        List<T> resultado = new ArrayList<>();
        for (T item : base) {
            if (filtro.test(item)) {
                resultado.add(item);
            }
        }
        return resultado;
    }

}