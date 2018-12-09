package erivan.com.agenda;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Browser;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import erivan.com.agenda.dao.AlunoDAO;
import erivan.com.agenda.modelo.Aluno;

public class ListaAlunosActivity extends AppCompatActivity {

    private ListView listaAlunos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_alunos);
         listaAlunos =  (ListView) findViewById(R.id.lista_alunos);

          //para o alterar, click simples
         listaAlunos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> lista, View item, int position, long id) {

                  Aluno aluno = (Aluno) listaAlunos.getItemAtPosition(position);

                  Intent intentVaiProFormulario = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
                  //implements o serializable em aluno para enviar os dados para a outra activity
                  intentVaiProFormulario.putExtra("aluno", aluno);
                  startActivity(intentVaiProFormulario);

             }
         });



        Button novoAluno = (Button) findViewById(R.id.novo_aluno);
        novoAluno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //vai para o formulario
                Intent intentVaiParaoFormulario = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
                 startActivity(intentVaiParaoFormulario);

            }
        });

        //registra o meno de contexto do deletar, para o clique longo
        registerForContextMenu(listaAlunos);
    }




     //carrega a lista
    private void carregaLista() {

        //busca o aluno
        AlunoDAO dao = new AlunoDAO(this);
        List<Aluno> alunos = dao.buscaAlunos();
        dao.close();

        //home/erivan/agenda


        //contexto passar para a propria activity usa o this, o layout que quero utilizar e passa o array por ultimo
        ArrayAdapter<Aluno> adapter = new ArrayAdapter<Aluno>(this, android.R.layout.simple_list_item_1, alunos);

        listaAlunos.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //carrega a lista com o cadastro novo quando volta para o listar
        carregaLista();
    }
      //menu que aparece quando pressionar o nome do aluno, podendo deletar o mesmo (menu de contexto)
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, final ContextMenu.ContextMenuInfo menuInfo) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        //posicao da lista que foi clicada
        final Aluno aluno = (Aluno) listaAlunos.getItemAtPosition(info.position);

        MenuItem itemLigar = menu.add("Ligar");

          itemLigar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
              @Override
              public boolean onMenuItemClick(MenuItem item) {
                    //verifica se tem a permissao no Manifest
                  if(ActivityCompat.checkSelfPermission(ListaAlunosActivity.this, Manifest.permission.CALL_PHONE)
                          != PackageManager.PERMISSION_GRANTED){
                      ActivityCompat.requestPermissions(ListaAlunosActivity.this,
                              new String[]{Manifest.permission.CALL_PHONE}, 123);
                  }else{
                        //se tem a permissao faz a chamada
                      Intent intentLigar = new Intent(Intent.ACTION_CALL);
                      intentLigar.setData(Uri.parse("tel:" + aluno.getTelefone()));
                      startActivity(intentLigar);

                  }



                  return false;
              }
          });




          //enviar sms
         MenuItem itemSMS = menu.add("Enviar SMS");
         Intent intentSMS =  new Intent(Intent.ACTION_VIEW);
         intentSMS.setData(Uri.parse("sms:" + aluno.getTelefone()));
         itemSMS.setIntent(intentSMS);

         //visualizar o endereco no mapa
        MenuItem itemMapa = menu.add("Visualizar no mapa");
        Intent intentMapa = new Intent(Intent.ACTION_VIEW);
        intentMapa.setData(Uri.parse("geo:0,0?q=" + aluno.getEndereco()));
        itemMapa.setIntent(intentMapa);


           //clique longo, mostrar tbm o site do aluno para ser visitado
        MenuItem itemSite = menu.add("visitar Site");
        //quando clicado em visitar site,  com o action_view o android ver que ele necessita de um navegador e mostra
        //a lista de navegadores você escolhe o navegador e ele abre a url
        Intent intentSite = new Intent(Intent.ACTION_VIEW);
        //se no campo o usuario não digitou o http:// ele coloca para me
        String site = aluno.getSite();
        if(!site.startsWith("http://")){
            site = "http://" + site;
        }

        //pega a url informada para o aluno
        intentSite.setData(Uri.parse(aluno.getSite()));
        itemSite.setIntent(intentSite);
        

       MenuItem deletar = menu.add("Deletar");
       deletar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
           @Override
           public boolean onMenuItemClick(MenuItem item) {


               Toast.makeText(ListaAlunosActivity.this, "Deletar o aluno " + aluno.getNome(),Toast.LENGTH_SHORT).show();


               //deleta o aluno
               AlunoDAO dao = new AlunoDAO(ListaAlunosActivity.this);
               dao.deleta(aluno);
               dao.close();

               carregaLista();

               return false;
           }
       });
    }


}
