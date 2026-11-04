package util;

import java.math.BigDecimal;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.config.ObjectConstructor;

import modelo.Pet;
import modelo.Servico;
import modelo.Tutor;

public class Util {
	private static ObjectContainer manager;

	public static void conectar() {
		if (manager != null)
			return; // ja tem uma conexao

		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.common().messageLevel(0);

		for (Class<?> c : new Class<?>[] { Tutor.class, Pet.class, Servico.class }) {
			config.common().objectClass(c).cascadeOnDelete(false);
			config.common().objectClass(c).cascadeOnUpdate(true);
			config.common().objectClass(c).cascadeOnActivate(true);
		}
		config.common().activationDepth(Integer.MAX_VALUE);

		// Desde o Java 9 o db4o nao consegue ler os campos internos do BigDecimal por reflexao
		// (InaccessibleObjectException). O translator grava o valor como texto e o recria ao ler.
		config.common().objectClass(BigDecimal.class).translate(new ObjectConstructor() {
			public Object onStore(ObjectContainer db, Object valor) {
				return valor.toString();
			}

			public Object onInstantiate(ObjectContainer db, Object gravado) {
				return new BigDecimal((String) gravado);
			}

			public void onActivate(ObjectContainer db, Object valor, Object gravado) {
			}

			public Class<?> storedClass() {
				return String.class;
			}
		});

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
