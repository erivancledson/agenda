package erivan.com.agenda.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import erivan.com.agenda.modelo.Aluno;

public class AlunoDAO  extends SQLiteOpenHelper {


    public AlunoDAO( Context context) {
        //Agenda é o nome do banco de dados
        super(context, "Agenda", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //sempre que ele precisar criar o banco de dados
        String sql = "CREATE TABLE Alunos (id INTEGER PRIMARY KEY, nome TEXT NOT NULL, endereco TEXT, telefone TEXT, site TEXT, nota REAL);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //atualiza o banco de dados de acordo com a versão que for alterada
        String sql = "DROP TABLE IF EXISTS Alunos";
        db.execSQL(sql);
        //chama  o onCreate e cria as tabelas
        onCreate(db);

    }

    public void insere(Aluno aluno) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues dados = pegaDadosDoAluno(aluno);

        db.insert("Alunos", null, dados );
    }

    @NonNull
    private ContentValues pegaDadosDoAluno(Aluno aluno) {
        ContentValues dados = new ContentValues();
        dados.put("nome", aluno.getNome());
        dados.put("endereco", aluno.getEndereco());
        dados.put("telefone", aluno.getTelefone());
        dados.put("site", aluno.getSite());
        dados.put("nota", aluno.getNota());
        return dados;
    }

    public List<Aluno> buscaAlunos()  {

        String sql = "SELECT * FROM Alunos;";
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);

        List<Aluno> alunos = new ArrayList<Aluno>();

        while (c.moveToNext()){
            Aluno aluno = new Aluno();
            aluno.setId(c.getLong(c.getColumnIndex("id")));
            aluno.setNome(c.getString(c.getColumnIndex("nome")));
            aluno.setEndereco(c.getString(c.getColumnIndex("endereco")));
            aluno.setTelefone(c.getString(c.getColumnIndex("telefone")));
            aluno.setSite(c.getString(c.getColumnIndex("site")));
            aluno.setNota(c.getDouble(c.getColumnIndex("nota")));
             //adiciona o aluno na lista
            alunos.add(aluno);
        }

        c.close();

        return alunos;
    }

    public void deleta(Aluno aluno) {

        SQLiteDatabase db = getWritableDatabase();

        String [] params = {String.valueOf(aluno.getId())};
        db.delete("Alunos", "id = ?", params);
    }

    public void altera(Aluno aluno) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues dados = pegaDadosDoAluno(aluno);

        String[] params ={aluno.getId().toString()};
        db.update("Alunos", dados, "id = ?", params);
    }

    //para saber se foi um aluno que enviou a mensagem
    public boolean ehAluno(String telefone){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM Alunos WHERE telefone = ?", new String[]{telefone});
        int resultados = c.getCount();
        c.close();
        //se tem telefone returna falando que é aluno, para exibir o som e a mensagem do toast
        return resultados > 0;
    }
}
