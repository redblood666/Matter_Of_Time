package br.com.matteroftime.data.realm;

import android.content.Context;

import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import br.com.matteroftime.R;
import br.com.matteroftime.core.MatterOfTimeApplication;
import br.com.matteroftime.core.events.MusicListChangedEvent;
import br.com.matteroftime.core.listeners.OnDatabaseOperationCompleteListener;
import br.com.matteroftime.models.Compasso;
import br.com.matteroftime.models.Musica;
import br.com.matteroftime.ui.downloadMusic.DownloadMusicContract;
import br.com.matteroftime.util.EventBus;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by RedBlood on 12/05/2017.
 */

public class DownloadMusicRepository implements DownloadMusicContract.Repository {
    File file;
    Musica musica1 = new Musica();

    @Override
    public void downloadMusica(Musica musica, final OnDatabaseOperationCompleteListener listener, final Context context) {
        long musicaId = musica.getId();
        String id = String.valueOf(musicaId);

        Future<File> downloading;

        downloading = Ion.with(context)
                .load("https://matteroftime-redblood666.c9users.io/download.php")

                .setBodyParameter("id", id)
                .write(context.getFileStreamPath("archive_"+System.currentTimeMillis()+"_music.met"))
                .setCallback(new FutureCallback<File>() {
                    @Override
                    public void onCompleted(Exception e, File result) {
                        if (e != null){
                            return;
                        } else {
                            file = result;
                            try {
                                FileInputStream fileInputStream = new FileInputStream(file);
                                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                                musica1 = (Musica) objectInputStream.readObject();
                                objectInputStream.close();
                                fileInputStream.close();
                                listener.onSQLOperationSucceded(context.getString(R.string.sucesso_importar));
                            } catch (FileNotFoundException ee) {
                                ee.printStackTrace();
                                listener.onSQLOperationFailed(context.getString(R.string.falha_importar));
                            } catch (IOException ee) {
                                ee.printStackTrace();
                                listener.onSQLOperationFailed(context.getString(R.string.falha_importar));
                            } catch (ClassNotFoundException ee) {
                                ee.printStackTrace();
                                listener.onSQLOperationFailed(context.getString(R.string.falha_importar));
                            }

                            final Realm realm = Realm.getDefaultInstance();
                            final long idMusica = MatterOfTimeApplication.musicaPrimarykey.incrementAndGet();
                            realm.executeTransactionAsync(new Realm.Transaction() {
                                                              @Override
                                                              public void execute(Realm backgroundRealm) {
                                                                  musica1.setId(idMusica);
                                                                  RealmList<Compasso> compassos = new RealmList<Compasso>();
                                                                  for (Compasso compasso : musica1.getCompassosList()){
                                                                      compasso.setId(MatterOfTimeApplication.compassoPrimarykey
                                                                              .getAndIncrement());
                                                                      compassos.add(compasso);
                                                                  }
                                                                  musica1.setCompassos(compassos);
                                                                  musica1.setCompassosList(null);
                                                                  backgroundRealm.copyToRealmOrUpdate(musica1);

                                                              }
                                                          }, new Realm.Transaction.OnSuccess() {
                                                              @Override
                                                              public void onSuccess() {
                                                                  realm.close();
                                                                  EventBus.getInstance().post(new MusicListChangedEvent());
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
                            listener.onSQLOperationSucceded(context.getString(R.string.sucesso_baixar));
                        }
                    }
                });
    }

    @Override
    public List<Musica> getAllMusics() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Musica> musicas = realm.where(Musica.class).findAllSorted("ordem");
        List<Musica> result = realm.copyFromRealm(musicas);
        realm.close();
        return result;
    }
}
