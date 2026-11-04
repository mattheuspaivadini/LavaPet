package util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.db4o.ObjectContainer;
import com.db4o.events.EventRegistry;
import com.db4o.events.EventRegistryFactory;
import com.db4o.query.Query;

public class GeradorID {
	private static final Map<String, RegistroID> registros = new HashMap<>();

	static void ativar(ObjectContainer manager) {
		registros.clear();
		EventRegistry eventRegistry = EventRegistryFactory.forObjectContainer(manager);

		// "BEFORE PERSIST": disparado antes de gravar um objeto novo
		eventRegistry.creating().addListener((event, args) -> {
			Object objeto = args.object();
			Field campo;
			try {
				campo = objeto.getClass().getDeclaredField("id");
			} catch (NoSuchFieldException e) {
				return; // classe sem id (ex.: o proprio RegistroID)
			}
			campo.setAccessible(true);
			RegistroID registro = registros.computeIfAbsent(objeto.getClass().getName(),
					nome -> lerRegistroID(manager, objeto.getClass(), campo));
			registro.incrementarID();
			try {
				campo.setInt(objeto, registro.getid());
			} catch (IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
		});
	}

	static void salvar(ObjectContainer manager) {
		for (RegistroID registro : registros.values())
			manager.store(registro);
		manager.commit();
		registros.clear();
	}

	private static RegistroID lerRegistroID(ObjectContainer manager, Class<?> classe, Field campo) {
		Query q = manager.query();
		q.constrain(RegistroID.class);
		q.descend("nomedaclasse").constrain(classe.getName());
		List<RegistroID> resultados = q.execute();
		RegistroID registro = resultados.isEmpty() ? new RegistroID(classe.getName()) : resultados.get(0);

		// O contador so e gravado em salvar() (no desconectar). Se o programa for encerrado a forca
		// (Ctrl+C, fechar o terminal) depois de um commit, o contador fica atrasado e os proximos
		// objetos receberiam ids ja usados. Por isso ele nunca comeca abaixo do maior id ja gravado.
		Query maior = manager.query();
		maior.constrain(classe);
		maior.descend("id").orderDescending();
		List<Object> objetos = maior.execute();
		if (!objetos.isEmpty()) {
			try {
				registro.garantirMinimo(campo.getInt(objetos.get(0)));
			} catch (IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
		}
		return registro;
	}
}

// Ultimo ID gerado de uma classe (inicia com 0)
class RegistroID {
	private String nomedaclasse;
	private int ultimoid;

	RegistroID(String nomedaclasse) {
		this.nomedaclasse = nomedaclasse;
	}

	int getid() {
		return ultimoid;
	}

	void incrementarID() {
		ultimoid++;
	}

	void garantirMinimo(int id) {
		ultimoid = Math.max(ultimoid, id);
	}

	@Override
	public String toString() {
		return "RegistroID [nomedaclasse=" + nomedaclasse + ", ultimoid=" + ultimoid + "]";
	}
}
