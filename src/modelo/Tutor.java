package modelo;

import java.util.ArrayList;
import java.util.List;

public class Tutor {
    private int id;
    private String nome;
    private List<Pet> listaPets;

    public Tutor() {
        this.listaPets = new ArrayList<>();
    }

    public Tutor(int id, String nome) {
        this();
        this.id = id;
        this.nome = nome;
    }

    public void adicionarPet(Pet pet) {
        this.listaPets.add(pet);
        if (pet.getTutor() != this) {
            pet.setTutor(this);
        }
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public List<Pet> getListaPets() { return listaPets; }
    public void setListaPets(List<Pet> listaPets) { this.listaPets = listaPets; }

    @Override
    public String toString() {
        return "Tutor [id=" + id + ", nome=" + nome + ", totalPets=" + listaPets.size() + "]";
    }
}
