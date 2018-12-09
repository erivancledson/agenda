package erivan.com.agenda.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.telephony.SmsMessage;
import android.widget.Toast;

import erivan.com.agenda.R;
import erivan.com.agenda.dao.AlunoDAO;

public class SMSReceiver extends BroadcastReceiver {

    //o aluno envia um sms e o celular recebe este sms enviando um toast, para o celular. a aplicação não precisa está aberta
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        //gera um pdu para cada parte da mensagem enviada e forma um array de pdus para serem enviadas
        Object[] pdus = (Object[]) intent.getSerializableExtra("pdus");
        //pega a primeira pdu
        byte[] pdu = (byte[]) pdus[0];
        String formato = (String) intent.getSerializableExtra("format");

        SmsMessage sms = SmsMessage.createFromPdu(pdu, formato);
        //devolve qual o telefone de quem está enviando o sms
        String telefone = sms.getDisplayOriginatingAddress();
        AlunoDAO dao = new AlunoDAO(context);
        if(dao.ehAluno(telefone)){
            Toast.makeText(context, "Chegou um SMS de Aluno(a)!", Toast.LENGTH_SHORT).show();
            //som da mensagem, está dentro da pasta  raw
            MediaPlayer mp = MediaPlayer.create(context, R.raw.msg);
            mp.start();

        }

        dao.close();


    }
}
