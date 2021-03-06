package br.com.matteroftime.models;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by RedBlood on 30/03/2017.
 */

public class Compasso extends RealmObject implements Serializable{

    @PrimaryKey
    private long id;
    private int bpm;
    private int tempos;
    private int nota;
    private int repeticoes;
    private double intervalo;
    private int ordem;


    public Compasso() {
    }

    public Compasso(long id, int bpm, int tempos, int nota, int repeticoes,
                    int ordem) {
        this.id = id;
        this.bpm = bpm;
        this.tempos = tempos;
        this.nota = nota;
        this.repeticoes = repeticoes;
        this.ordem = ordem;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    public int getTempos() {
        return tempos;
    }

    public void setTempos(int tempos) {
        this.tempos = tempos;
    }

    public int getNota() {
        return nota;
    }

    public void setNota(int nota) {
        this.nota = nota;
    }

    public int getRepeticoes() {
        return repeticoes;
    }

    public void setRepeticoes(int repeticoes) {
        this.repeticoes = repeticoes;
    }

    public double getIntervalo() {
        return intervalo;
    }

    public void setIntervalo(double intervalo) {
        this.intervalo = intervalo;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

}
