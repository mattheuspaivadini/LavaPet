package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pet {
	private int id;
	private String nome;
	private String raca;
	private Tutor tutor;
	private List<Servico> listaServicos = new ArrayList<>();

	public Pet(String nome, String raca, Tutor tutor) {
		if (tutor == null) {
			throw new IllegalArgumentException("Pet precisa de um tutor");
		}
		this.nome = nome;
		this.raca = raca;
		setTutor(tutor);
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

	public String getRaca() {
		return raca;
	}

	public void setRaca(String raca) {
		this.raca = raca;
	}

	public Tutor getTutor() {
		return tutor;
	}

	public final void setTutor(Tutor novo) {
		if (this.tutor == novo) {
			return;
		}
		Tutor antigo = this.tutor;
		this.tutor = novo;
		if (antigo != null) {
			antigo.removerPet(this);
		}
		if (novo != null) {
			novo.adicionarPet(this);
		}
	}

	public List<Servico> getListaServicos() {
		return Collections.unmodifiableList(listaServicos);
	}

	public void adicionarServico(Servico servico) {
		if (servico != null && !listaServicos.contains(servico)) {
			listaServicos.add(servico);
			servico.adicionarPet(this);
		}
	}

	public void removerServico(Servico servico) {
		if (listaServicos.remove(servico)) {
			servico.removerPet(this);
		}
	}

	@Override
	public String toString() {
		return "Pet [id=" + id + ", nome=" + nome + ", raca=" + raca
				+ ", tutor=" + (tutor == null ? "-" : tutor.getNome())
				+ ", servicos=" + listaServicos.stream().filter(s -> s != null).map(s -> "#" + s.getId() + " " + s.getData()).toList() + "]";
	}
}
