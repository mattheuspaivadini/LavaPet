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
			RegistroID registro = registros.computeIfAbsent(objeto.getClass().getName(),
					nome -> lerRegistroID(manager, nome));
			registro.incrementarID();
			try {
				campo.setAccessible(true);
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

	private static RegistroID lerRegistroID(ObjectContainer manager, String nomeclasse) {
		Query q = manager.query();
		q.constrain(RegistroID.class);
		q.descend("nomedaclasse").constrain(nomeclasse);
		List<RegistroID> resultados = q.execute();
		return resultados.isEmpty() ? new RegistroID(nomeclasse) : resultados.get(0);
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

	@Override
	public String toString() {
		return "RegistroID [nomedaclasse=" + nomedaclasse + ", ultimoid=" + ultimoid + "]";
	}
}
