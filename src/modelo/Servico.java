package modelo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Servico {
	private int id; // gerado por util.GeradorID
	private String data;
	private String valor;
	private List<Pet> listaPets = new ArrayList<>();

	public Servico(String data, BigDecimal valor) {
		if (data == null || valor == null) {
			throw new IllegalArgumentException("Servico precisa de data e valor");
		}
		this.data = data;
		this.valor = valor.toPlainString();
	}

	public int getId() {
		return id;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		if (data == null) {
			throw new IllegalArgumentException("data nao pode ser nula");
		}
		this.data = data;
	}

	public BigDecimal getValor() {
		return new BigDecimal(valor);
	}

	public void setValor(BigDecimal valor) {
		if (valor == null) {
			throw new IllegalArgumentException("valor nao pode ser nulo");
		}
		this.valor = valor.toPlainString();
	}

	public List<Pet> getListaPets() {
		return Collections.unmodifiableList(listaPets);
	}

	public void adicionarPet(Pet pet) {
		if (pet != null && !listaPets.contains(pet)) {
			listaPets.add(pet);
			pet.adicionarServico(this);
		}
	}

	public void removerPet(Pet pet) {
		if (listaPets.remove(pet)) {
			pet.removerServico(this);
		}
	}

	@Override
	public String toString() {
		return "Servico [id=" + id + ", data=" + data + ", valor=" + valor
				+ ", pets=" + listaPets.stream().map(Pet::getNome).toList() + "]";
	}
}
