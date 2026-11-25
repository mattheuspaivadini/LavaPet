package util;

import javax.swing.JOptionPane;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;

import modelo.Pet;
import modelo.Servico;
import modelo.ServicoExpresso;
import modelo.Tutor;

public class Util {
	private static ObjectContainer manager;
	private static String ip="localhost";

	public static void conectar() {
		if (manager != null)
			return;
		
		try {
			conectarBancoLocal();

			GeradorID.ativar(manager);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Erro ao conectar ao banco " + ip + "\n" + e.getMessage());
			System.exit(0);
		}
	}

	private static void conectarBancoLocal() {
		if (manager != null)
			return;


		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.common().messageLevel(0);

		// habilitar cascata na alteração, remoção e leitura
		config.common().objectClass(Tutor.class).cascadeOnDelete(false);
		config.common().objectClass(Tutor.class).cascadeOnUpdate(true);
		config.common().objectClass(Tutor.class).cascadeOnActivate(true);
		config.common().objectClass(Pet.class).cascadeOnDelete(false);
		config.common().objectClass(Pet.class).cascadeOnUpdate(true);
		config.common().objectClass(Pet.class).cascadeOnActivate(true);
		config.common().objectClass(Servico.class).cascadeOnDelete(false);
		config.common().objectClass(Servico.class).cascadeOnUpdate(true);
		config.common().objectClass(Servico.class).cascadeOnActivate(true);
		config.common().objectClass(ServicoExpresso.class).cascadeOnDelete(false);
		config.common().objectClass(ServicoExpresso.class).cascadeOnUpdate(true);
		config.common().objectClass(ServicoExpresso.class).cascadeOnActivate(true);
		config.common().activationDepth(Integer.MAX_VALUE);


		try {
			manager = Db4oEmbedded.openFile(config, "lavapet.db4o");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Erro ao conectar ao banco local \n" + e.getMessage());
			System.exit(0);
		}
	}

	public static void desconectar() {
		if (manager != null) {
			manager.close();
			manager = null;
		}
	}

	public static ObjectContainer getManager() {
		return manager;
	}

	public static String getIP() {
		return ip;
	}

}
