package util;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;

import modelo.Pet;
import modelo.Servico;
import modelo.Tutor;

public class Util {
	private static ObjectContainer manager;

	public static void conectar() {
		if (manager != null)
			return; // ja tem uma conexao

		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.common().messageLevel(0); // mensagens na tela 0(desliga),1,2,3...

		// cascata na alteracao e leitura; remocao manual (ver Apagar)
		for (Class<?> c : new Class<?>[] { Tutor.class, Pet.class, Servico.class }) {
			config.common().objectClass(c).cascadeOnDelete(false);
			config.common().objectClass(c).cascadeOnUpdate(true);
			config.common().objectClass(c).cascadeOnActivate(true);
		}
		config.common().activationDepth(Integer.MAX_VALUE);

		manager = Db4oEmbedded.openFile(config, "lavapet.db4o");
		GeradorID.ativar(manager); // ids automaticos para as classes com atributo "int id"
	}

	public static void desconectar() {
		if (manager != null) {
			GeradorID.salvar(manager);
			manager.close();
			manager = null;
		}
	}

	public static ObjectContainer getManager() {
		return manager;
	}
}
