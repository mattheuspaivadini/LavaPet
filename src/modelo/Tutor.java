package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tutor {
	private int id; 
	private String nome;
	private List<Pet> listaPets = new ArrayList<>();

	public Tutor(String nome) {
		this.nome = nome;
	}

	public Tutor(int id, String nome) {
		this.id = id;
		this.nome = nome;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public List<Pet> getListaPets() {
		return Collections.unmodifiableList(listaPets);
	}

	public void adicionarPet(Pet pet) {
		if (pet == null || listaPets.contains(pet)) {
			return;
		}
		listaPets.add(pet);
		pet.setTutor(this);
	}

	public void removerPet(Pet pet) {
		if (pet == null) {
			return;
		}
		if (listaPets.remove(pet) && pet.getTutor() == this) {
			pet.setTutor(null);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Tutor [id=").append(id)
				.append(", nome=").append(nome)
				.append(", pets=[");
		for (int i = 0; i < listaPets.size(); i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(listaPets.get(i).getNome());
		}
		sb.append("]]");
		return sb.toString();
	}
}
