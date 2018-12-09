package erivan.com.agenda;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import erivan.com.agenda.dao.AlunoDAO;
import erivan.com.agenda.modelo.Aluno;

public class FormularioActivity extends AppCompatActivity {

    private FormularioHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        //instancia
        helper = new FormularioHelper(this);
         //exibe todos os dados do aluno para ser feito a alteração
        Intent intent = getIntent();
        //a chave aluno ela recebe os dados que foi passado pela mesma chave no listar
        Aluno aluno = (Aluno) intent.getSerializableExtra("aluno");
        //se o aluno nao vier nulo ele preenche os dados para o editar
        if(aluno != null){
            helper.preencheFormulario(aluno);
        }


    }

    //coloca o botão salvar em cima na barra
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        //cria um menu xml com o icone
        inflater.inflate(R.menu.menu_formulario, menu);

        return super.onCreateOptionsMenu(menu);
    }

    //para o menu que fica no topo com o icone de salvar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_formulario_ok:
                Aluno aluno = helper.pegaAluno();

                AlunoDAO dao = new AlunoDAO(this);
                //se for diferente de nullo é uma alteracao se não for nulo é um insert
                if (aluno.getId() != null)  {
                    dao.altera(aluno);
                } else {
                    dao.insere(aluno);
                }
                dao.close();
                Toast.makeText(FormularioActivity.this, "Aluno " + aluno.getNome() + " salvo!", Toast.LENGTH_SHORT).show();

                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
