package br.com.matteroftime.ui.addMusic;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import br.com.matteroftime.R;
import br.com.matteroftime.models.Compasso;
import br.com.matteroftime.models.Musica;
import br.com.matteroftime.util.Constants;
import br.com.matteroftime.util.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmList;


public class AddMusicDialogFragment extends DialogFragment  implements AddMusicContract.View  {
    private AddMusicContract.Action presenter;
    private boolean editMode = false;
    private Context context;
    private Context editContext;
    @BindView(R.id.edt_nome_da_musica) EditText edtNomeDaMusica;
    @BindView(R.id.edt_quantidade_compassos) EditText edtQtdCompassos;
    public AddMusicDialogFragment() {
        // Required empty public constructor
    }
    public static AddMusicDialogFragment newInstance(long id) {
        AddMusicDialogFragment fragment = new AddMusicDialogFragment();
        if (id > 0){
            Bundle args = new Bundle();
            args.putLong(Constants.COLUMN_ID, id);
            fragment.setArguments(args);
        }
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new AddMusicPresenter(this);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogFragment = new AlertDialog.Builder(getActivity());
        if (savedInstanceState == null){
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.fragment_add_music, null);
            dialogFragment.setView(view);
            ButterKnife.bind(this, view);
            if (getArguments() != null && getArguments().containsKey(Constants.COLUMN_ID)){
                presenter.checkStatus(getArguments().getLong(Constants.COLUMN_ID));
            }
            View titleView = inflater.inflate(R.layout.dialog_title, null);
            TextView titleText = (TextView)titleView.findViewById(R.id.txt_view_dialog_title);
            titleText.setText(editMode ? getString(R.string.atualizar_musica) : getString(R.string.adicionar_musica));
            dialogFragment.setCustomTitle(titleView);
            dialogFragment.setPositiveButton(editMode ? getString(R.string.atualizar) : getString(R.string.adicionar), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialogFragment.setNegativeButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }

        edtNomeDaMusica.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    Utils.hideKeyboard(getActivity().getBaseContext(), edtNomeDaMusica);
                }

            }
        });

        edtQtdCompassos.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    Utils.hideKeyboard(getActivity().getBaseContext(), edtQtdCompassos);
                }
            }
        });

        return dialogFragment.create();
    }
    @Override
    public void populateForm(Musica musica) {
        edtNomeDaMusica.setText(musica.getNome());
        edtQtdCompassos.setText(String.valueOf(musica.getQtdCompassos()));
    }
    @Override
    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }
    @Override
    public void displayMessage(String message) {

    }
    private boolean validateInputs(){
        if (edtNomeDaMusica.getText().toString().isEmpty()){
            edtNomeDaMusica.setError(getString(R.string.name_is_required));
            edtNomeDaMusica.requestFocus();
            return false;
        }
        if (edtQtdCompassos.getText().toString().isEmpty()){
            edtQtdCompassos.setError(getString(R.string.quantidadecompassos));
            edtQtdCompassos.requestFocus();
            return false;
        }
        if (!edtQtdCompassos.getText().toString().isEmpty()){
            int val = Integer.parseInt(edtQtdCompassos.getText().toString());
            if (val > 999){
                edtQtdCompassos.setError(getString(R.string.compassos_excedentes));
                return false;
            }
        }
        return true;
    }
    @Override
    public void onStart() {
        super.onStart();
        AlertDialog d = (AlertDialog)getDialog();
        if (d != null){
            Button positiveButton = (Button)d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean readyToCloseDialog = false;
                    if (validateInputs()){
                        saveMusic();
                        readyToCloseDialog = true;
                    }
                    if (readyToCloseDialog){
                        dismiss();
                    }
                }
            });
        }
    }
    public void saveMusic(){
        Musica musica = new Musica();
        musica.setNome(edtNomeDaMusica.getText().toString());
        musica.setQtdCompassos(Integer.parseInt(edtQtdCompassos.getText().toString()));
        presenter.ondAddMusicButtonClick(musica, getActivity().getBaseContext());
    }

   /* @Override
    public void recebeContext(Context context) {
        presenter.recebeContext(context);
    }*/
}
