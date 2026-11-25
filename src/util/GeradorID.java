package util;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.db4o.ObjectContainer;
import com.db4o.events.EventRegistry;
import com.db4o.events.EventRegistryFactory;
import com.db4o.query.Query;



public class GeradorID {
	private static ObjectContainer sequencia; 

	@SuppressWarnings("unused")
	static void ativar(ObjectContainer manager) {
		if (manager == null)
			throw new RuntimeException("Não pode ativar GeradorID - manager desconectado");

		sequencia = manager;


		EventRegistry eventRegistry = EventRegistryFactory.forObjectContainer(manager);

	
		eventRegistry.creating().addListener((event, args) -> {
			try {
				Object objeto = args.object();
				
				Class<?> classe = objeto.getClass();
				Field campo = null;
				for (Field f : getAllFieldsList(classe)) {
					if (f.getName().equals("id")) {
						campo = f;
						break;
					}
				}

				if (campo != null) { // tem campo id?
					Class<?> cls = objeto.getClass();
					String nomedaclasse = "";
					if(cls.getSuperclass()==Object.class)
						nomedaclasse = objeto.getClass().getName();
					else
						nomedaclasse = cls.getSuperclass().getName();
					
					RegistroID registro = lerRegistroID(nomedaclasse); 
					registro.incrementarID(); 
					campo.setAccessible(true); 
					campo.setInt(objeto, registro.getid()); 
					sequencia.store(registro);
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
			}
		});


		eventRegistry.created().addListener((event, args) -> {
			manager.ext().purge(); // limpar cache do manager
		});


		eventRegistry.queryFinished().addListener((event, args) -> {

			manager.ext().purge();
		});
	}

	private static RegistroID lerRegistroID(String nomeclasse) {
		Query q = sequencia.query();
		q.constrain(RegistroID.class);
		q.descend("nomedaclasse").constrain(nomeclasse);
		List<RegistroID> resultados = q.execute();
		if (resultados.size() > 0)
			return resultados.getFirst();
		else
			return new RegistroID(nomeclasse); // novo registro de id
	}



	
	private static <X> List<Field> getAllFieldsList(final Class<X> cls) {
		
		final List<Field> allFields = new ArrayList<>();
		Class<?> currentClass = cls;
		while (currentClass != null) {
			final Field[] declaredFields = currentClass.getDeclaredFields();
			Collections.addAll(allFields, declaredFields);
			currentClass = currentClass.getSuperclass();
		}
		return allFields;
	}
} 

class RegistroID {
	private String nomedaclasse;
	private int ultimoid;

	public RegistroID(String nomedaclasse) {
		this.nomedaclasse = nomedaclasse;
		this.ultimoid = 0;
	}
	public RegistroID(String nomedaclasse, int valorInicial) {
		this.nomedaclasse = nomedaclasse;
		this.ultimoid = valorInicial;
	}

	public String getNomedaclasse() {
		return nomedaclasse;
	}

	public int getid() {
		return ultimoid;
	}

	public void incrementarID() {
		ultimoid++;
	}

	@Override
	public String toString() {
		return "RegistroID [nomedaclasse=" + nomedaclasse + ", ultimoid=" + ultimoid + "]";
	}

} 
