package br.com.matteroftime.ui.edit;

import android.content.Context;

import java.util.List;

import br.com.matteroftime.core.listeners.OnDatabaseOperationCompleteListener;
import br.com.matteroftime.models.Compasso;
import br.com.matteroftime.models.Musica;
import br.com.matteroftime.ui.addMusic.AddMusicPresenter;

/**
 * Created by RedBlood on 30/03/2017.
 */

public interface EditContract {
    public interface View{
        void recebeMusica(Musica musica);
        void showMusics(List<Musica> musicas);
        void showAddMusicForm();
        void showEditMusicForm(Musica musica);
        void showDeleteMusicPrompt(Musica musica);
        void showEmptyText();
        void hideEmptyText();
        void showMessage(String message);
        void atualizaViewsMusica(Musica musica);
        void atualizaViewsCompasso(Musica musica, Compasso compasso);
        void atualizaNomeMusica(Musica musicaSelecionada);

        void resetaNome();


    }
    public interface Actions{
        void loadMusics();
        Musica getMusica(long id);
        void onAddMusicButtonClicked();
        void ondAddToEditButtonClicked(Musica musica);
        void addMusic(Musica musica, Context context);
        void onDeleteMusicButtonClicked(Musica musica);
        void deleteMusic(Musica musica, Context context);
        void onEditMusicaButtonClicked(Musica musica);
        void updateMusica(Musica musica, Context context);
        List<Musica> getListaMusicas();
        void atualizarCompassodaMusica(Musica musica, Compasso compasso, Context context);
        void atualizaMusica(Musica musica);
    }

    public interface Repository{
        List<Musica> getAllMusics();
        Musica getMusicById(long id);
        void deleteMusic(Musica musica, OnDatabaseOperationCompleteListener listener, Context context);
        void addMusic(Musica musica, OnDatabaseOperationCompleteListener listener, Context context);
        void updateMusic(Musica musica, OnDatabaseOperationCompleteListener listener, Context context);
        void atualizaCompasso(Musica musica, OnDatabaseOperationCompleteListener listener, Compasso compasso, Context context);
        void atualizaMusica(Musica musica);
    }
}
