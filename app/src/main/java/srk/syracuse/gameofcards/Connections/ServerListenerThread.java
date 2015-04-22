package srk.syracuse.gameofcards.Connections;

import android.os.Bundle;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

import srk.syracuse.gameofcards.Fragments.HostFragment;
import srk.syracuse.gameofcards.Model.Game;
import srk.syracuse.gameofcards.Model.PlayerInfo;
import srk.syracuse.gameofcards.Utils.ServerHandler;

public class ServerListenerThread extends Thread {

    private Socket hostThreadSocket;

    ServerListenerThread(Socket soc) {
        hostThreadSocket = soc;
    }

    @Override
    public void run() {
        while (true) {
            ObjectInputStream objectInputStream;
            try {
                InputStream inputStream = null;
                inputStream = hostThreadSocket.getInputStream();
                objectInputStream = new ObjectInputStream(inputStream);
                Object gameObject ;
                while (true) {
                    Bundle data = new Bundle();
                    gameObject = objectInputStream.readObject();
                    if (gameObject != null) {
                        if (gameObject instanceof PlayerInfo) {
                            data.putSerializable(ServerHandler.DATA_KEY, (PlayerInfo) gameObject);
                            data.putString(ServerHandler.ACTION_KEY, ServerHandler.PLAYER_LIST_UPDATE);
                        } else {
                            data.putSerializable(ServerHandler.DATA_KEY, (Game) gameObject);
                        }
                        Message msg = new Message();
                        msg.setData(data);
                        HostFragment.serverHandler.sendMessage(msg);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
