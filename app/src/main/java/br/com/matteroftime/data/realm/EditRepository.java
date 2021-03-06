package br.com.matteroftime.data.realm;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import br.com.matteroftime.R;
import br.com.matteroftime.core.MatterOfTimeApplication;
import br.com.matteroftime.core.listeners.OnDatabaseOperationCompleteListener;
import br.com.matteroftime.models.Compasso;
import br.com.matteroftime.models.Musica;
import br.com.matteroftime.ui.edit.EditContract;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.RealmClass;

/**
 * Created by RedBlood on 10/04/2017.
 */

public class EditRepository implements EditContract.Repository{

    @Override
    public void addMusic(final Musica musica, final OnDatabaseOperationCompleteListener listener, final Context context) {
        final Realm realm = Realm.getDefaultInstance();
        final long id = MatterOfTimeApplication.musicaPrimarykey.incrementAndGet();

        realm.executeTransactionAsync(new Realm.Transaction() {
                                          @Override
                                          public void execute(Realm backgroundRealm) {
                                              musica.setId(id);
                                              for (Compasso compasso : musica.getCompassos()) {
                                                  compasso.setId(MatterOfTimeApplication.compassoPrimarykey.getAndIncrement());
                                              }
                                              backgroundRealm.copyToRealmOrUpdate(musica);
                                          }
                                      }, new Realm.Transaction.OnSuccess() {
                                          @Override
                                          public void onSuccess() {
                                              realm.close();
                                              listener.onSQLOperationSucceded(context.getString(R.string.adicionado));
                                          }
                                      }, new Realm.Transaction.OnError() {
                                          @Override
                                          public void onError(Throwable error) {
                                              realm.close();
                                              listener.onSQLOperationFailed(error.getLocalizedMessage());
                                          }
                                      }
        );
    }

    @Override
    public List<Musica> getAllMusics() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Musica> musicas = realm.where(Musica.class).findAllSorted("ordem");
        List<Musica> result = realm.copyFromRealm(musicas);
        realm.close();
        return result;
    }
    @Override
    public Musica getMusicById(long id) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Musica> musicas = realm.where(Musica.class).equalTo("id", id).findAll();
        Musica result = musicas.first();
        Musica inMemoryMusic = realm.copyFromRealm(result);
        realm.close();
        return inMemoryMusic;
    }
    @Override
    public void atualizaCompasso(final Musica musica, final OnDatabaseOperationCompleteListener listener, final Compasso compasso, final Context context) {
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                try {
                    Musica managedMusic = backgroundRealm.where(Musica.class).equalTo("id", musica.getId()).findFirst();
                    managedMusic.getCompassos().get(compasso.getOrdem() - 1).setTempos(compasso.getTempos());
                    managedMusic.getCompassos().get(compasso.getOrdem() - 1).setBpm(compasso.getBpm());
                    managedMusic.getCompassos().get(compasso.getOrdem() - 1).setNota(compasso.getNota());
                    managedMusic.getCompassos().get(compasso.getOrdem() - 1).setRepeticoes(compasso.getRepeticoes());
                    managedMusic.getCompassos().get(compasso.getOrdem() - 1).setOrdem(compasso.getOrdem() - 1);



                    /*managedMusic.getCompassos().set(compasso.getOrdem() - 1,compasso);
                    managedMusic.getCompassos().get(compasso.getOrdem() - 1).setId(musica.getCompassos().get(compasso.getOrdem() - 1).getId());*/
                    listener.onSQLOperationSucceded(context.getString(R.string.atualizado));
                } catch (Exception e) {
                    listener.onSQLOperationFailed(context.getString(R.string.erro) + " " + e.getLocalizedMessage());
                }

                /*for (Compasso compasso: managedMusic.getCompassos()) {
                    int b = compasso.getBpm();
                    int c = b;
                }*/

                /*Compasso managedCompasso = managedMusic.getCompassos().get(compasso.getOrdem());
                managedCompasso.setOrdem(compasso.getOrdem());
                managedCompasso.setRepeticoes(compasso.getRepeticoes());
                managedCompasso.setNota(compasso.getNota());
                managedCompasso.setBpm(compasso.getBpm());
                managedCompasso.setTempos(compasso.getTempos());*/

                /*managedMusic.getCompassos().get(compasso.getOrdem()).setOrdem(compasso.getOrdem());
                managedMusic.getCompassos().get(compasso.getOrdem()).setNota(compasso.getNota());
                managedMusic.getCompassos().get(compasso.getOrdem()).setBpm(compasso.getBpm());
                managedMusic.getCompassos().get(compasso.getOrdem()).setTempos(compasso.getTempos());
                managedMusic.getCompassos().get(compasso.getOrdem()).setRepeticoes(compasso.getRepeticoes());*/

                //backgroundRealm.copyToRealmOrUpdate(managedMusic);
            }
        });

        /*realm.executeTransactionAsync(new Realm.Transaction() {
                                          @Override
                                          public void execute(Realm backgroundRealm) {
                                          Musica managedMusic = backgroundRealm.where(Musica.class).equalTo("id", musica.getId()).findFirst();
                                          managedMusic.getCompassos().set(compasso.getOrdem(),compasso);

                                          }
                                      }, new Realm.Transaction.OnSuccess() {
                                          @Override
                                          public void onSuccess() {
                                              realm.close();
                                              listener.onSQLOperationSucceded("Updated");
                                          }
                                      }, new Realm.Transaction.OnError() {
                                          @Override
                                          public void onError(Throwable error) {
                                              realm.close();
                                              listener.onSQLOperationFailed(error.getLocalizedMessage());
                                          }
                                      }
        );*/
    }


    @Override
    public void atualizaMusica(Musica musica) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        final Musica mangedMusica = realm.copyToRealmOrUpdate(musica);
        realm.commitTransaction();
        realm.close();
    }
    @Override
    public void deleteMusic(final Musica musica, final OnDatabaseOperationCompleteListener listener, final Context context) {
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
                                          @Override
                                          public void execute(Realm backgroundRealm) {
                                              Musica musicaToBeDeleted = backgroundRealm.where(Musica.class).equalTo("id", musica.getId()).findFirst();
                                              musicaToBeDeleted.deleteFromRealm();
                                          }
                                      }, new Realm.Transaction.OnSuccess() {
                                          @Override
                                          public void onSuccess() {
                                              realm.close();
                                              listener.onSQLOperationSucceded(context.getString(R.string.deletado));
                                          }
                                      }, new Realm.Transaction.OnError() {
                                          @Override
                                          public void onError(Throwable error) {
                                              realm.close();
                                              listener.onSQLOperationFailed(error.getLocalizedMessage());
                                          }
                                      }
        );
    }

    @Override
    public void updateMusic(final Musica musica, final OnDatabaseOperationCompleteListener listener, final Context context) {
        final Realm realm = Realm.getDefaultInstance();
/*        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm backgroundRealm) {
                musica.getCompassos().get(0).setId(1);
                musica.getCompassos().get(1).setId(2);
                backgroundRealm.copyToRealmOrUpdate(musica.getCompassos());
                backgroundRealm.copyToRealmOrUpdate(musica);
            }
        });*/
        realm.executeTransactionAsync(new Realm.Transaction() {
                                          @Override
                                          public void execute(Realm backgroundRealm) {

                                              Musica managedMusic = backgroundRealm.where(Musica.class).equalTo("id", musica.getId()).findFirst();
                                              managedMusic.setNome(musica.getNome());
                                              managedMusic.setQtdCompassos(musica.getQtdCompassos());
                                              managedMusic.setCompassos(null);
                                              RealmList<Compasso> compassos = new RealmList<Compasso>();
                                              for (int i = 0; i < musica.getQtdCompassos(); i++){
                                                  Compasso compasso = backgroundRealm.createObject(Compasso.class, MatterOfTimeApplication.compassoPrimarykey.getAndIncrement());

                                                  compassos.add(compasso);
                                                  //compassos.get(i).setId(MatterOfTimeApplication.compassoPrimarykey.getAndIncrement());
                                              }
                                              managedMusic.setCompassos(compassos);


                                              backgroundRealm.copyToRealmOrUpdate(managedMusic);
                                          }
                                      }, new Realm.Transaction.OnSuccess() {
                                          @Override
                                          public void onSuccess() {
                                              realm.close();
                                              listener.onSQLOperationSucceded(context.getString(R.string.atualizado));
                                          }
                                      }, new Realm.Transaction.OnError() {
                                          @Override
                                          public void onError(Throwable error) {
                                              realm.close();
                                              listener.onSQLOperationFailed(error.getLocalizedMessage());
                                          }
                                      }
        );
    }
}
