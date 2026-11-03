package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tutor {
	private int id; // gerado por util.GeradorID
	private String nome;
	private List<Pet> listaPets = new ArrayList<>();

	public Tutor(String nome) {
		this.nome = nome;
	}

	public int getId() {
		return id;
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
		if (pet != null && !listaPets.contains(pet)) {
			listaPets.add(pet);
			pet.setTutor(this);
		}
	}

	public void removerPet(Pet pet) {
		if (listaPets.remove(pet) && pet.getTutor() == this) {
			pet.setTutor(null);
		}
	}

	@Override
	public String toString() {
		return "Tutor [id=" + id + ", nome=" + nome + ", pets=" + listaPets.stream().map(Pet::getNome).toList() + "]";
	}
}
