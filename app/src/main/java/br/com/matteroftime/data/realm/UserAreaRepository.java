package br.com.matteroftime.data.realm;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import br.com.matteroftime.R;
import br.com.matteroftime.core.listeners.OnDatabaseOperationCompleteListener;
import br.com.matteroftime.models.Musica;
import br.com.matteroftime.ui.userArea.UserAreaContract;
import br.com.matteroftime.ui.userArea.UserAreaPresenter;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by RedBlood on 20/04/2017.
 */

public class UserAreaRepository implements UserAreaContract.Repository {
    List<Musica> musicas = new ArrayList<>();

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
    public void pesquisaMusica(String nomeMusica, final OnDatabaseOperationCompleteListener listener, final Context context, final UserAreaContract.Actions presenter) {
        nomeMusica = nomeMusica.trim();
        nomeMusica = nomeMusica.toLowerCase();
        nomeMusica = nomeMusica.replaceAll(" ", "");
        nomeMusica = Normalizer.normalize(nomeMusica, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");

        Ion.with(context)
                .load("https://matteroftime-redblood666.c9users.io/pesquisar.php")
                .setBodyParameter("nomeMusica", nomeMusica)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        if (result.size() > 0){
                            for (int i = 0; i < result.size(); i ++){
                                JsonObject jsonObject = result.get(i).getAsJsonObject();
                                musicas.add(new Musica());
                                musicas.get(i).setId(jsonObject.get("id").getAsLong());
                                musicas.get(i).setNome(jsonObject.get("nm_musica").getAsString());
                            }
                            presenter.recebeListagemMusicas(musicas);
                        } else {
                            listener.onSQLOperationFailed(context.getString(R.string.sem_resultados));
                        }
                    }
                });

        /*try{
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("https://matteroftime-redblood666.c9users.io/pesquisar.php?");
            stringBuilder.append("nomeMusica=");
            stringBuilder.append(URLEncoder.encode(nomeMusica, "UTF-8"));
            JsonArray jsonElements = new JsonArray();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("url", stringBuilder.toString());

            jsonElements.add(jsonObject);
            new HttpRequest().execute(jsonElements);
        } catch (Exception e){

        }*/

        //return musicas;
    }

    @Override
    public void baixaMusica(Musica musica, final OnDatabaseOperationCompleteListener listener, final Context context) {
        long id = musica.getId();
        JsonObject json = new JsonObject();
        json.addProperty("id", id);
        Future<File> downloading;

        downloading = Ion.with(context)
                .load("http://matteroftime.com.br/download")
                .setJsonObjectBody(json)
                .write(context.getFileStreamPath("archive_"+System.currentTimeMillis()+"_music.met"))
                .setCallback(new FutureCallback<File>() {
                    @Override
                    public void onCompleted(Exception e, File result) {
                        if (e != null){
                            listener.onSQLOperationFailed(context.getString(R.string.erro_download));
                            return;
                        } else {
                            listener.onSQLOperationSucceded(context.getString(R.string.sucesso_baixar));
                        }

                    }
                });

        Musica musica1 = new Musica();
        try {
            FileInputStream fileInputStream = new FileInputStream("data/data/br.com.matteroftime/"+musica.getNome()+"_music.met");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            musica1 = (Musica) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
            listener.onSQLOperationSucceded(context.getString(R.string.sucesso_importar));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            listener.onSQLOperationFailed(context.getString(R.string.falha_importar));
        } catch (IOException e) {
            e.printStackTrace();
            listener.onSQLOperationFailed(context.getString(R.string.falha_importar));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            listener.onSQLOperationFailed(context.getString(R.string.falha_importar));
        }
    }

    private class HttpRequest extends AsyncTask<JsonArray, Void, JsonArray> {

        @Override
        protected JsonArray doInBackground(JsonArray... params) {
            JsonArray elements= new JsonArray();
            try {
                JsonArray jsonElements = params[0];
                JsonObject jsonObject = (JsonObject) jsonElements.get(0);
                String urlHttp = jsonObject.get("url").getAsString();
                URL url = new URL(urlHttp);
                HttpURLConnection http = (HttpURLConnection)url.openConnection();
                InputStreamReader ips = new InputStreamReader(http.getInputStream());
                BufferedReader bfr = new BufferedReader(ips);
                JsonObject object;
                while(bfr.readLine() != ""){
                    String s = bfr.readLine();
                    object = new JsonObject();
                    object.addProperty("json", s);
                    elements.add(object);
                }
                //retorno = bfr.readLine();

            } catch (Exception ex){

            }

            return elements;
        }

        @Override
        protected void onPostExecute(JsonArray result){
            if (result.size() > 0){

                /*Toast.makeText(getBaseContext(), "Cliente cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getBaseContext(), ListarActivity.class));*/
            } else {
                /*Toast.makeText(getBaseContext(), "Erro ao cadastrar o cliente!", Toast.LENGTH_SHORT).show();*/
            }
        }
    }
}
