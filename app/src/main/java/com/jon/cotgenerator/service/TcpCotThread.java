package com.jon.cotgenerator.service;

import android.content.SharedPreferences;

import com.jon.cotgenerator.cot.CursorOnTarget;
import com.jon.cotgenerator.utils.DataFormat;
import com.jon.cotgenerator.utils.Key;
import com.jon.cotgenerator.utils.PrefUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import timber.log.Timber;

class TcpCotThread extends CotThread {
    private Socket socket;
    private OutputStream outputStream;

    TcpCotThread(SharedPreferences prefs) {
        super(prefs);
        dataFormat = DataFormat.XML; // regardless of what the preference is set as
    }

    @Override
    void shutdown() {
        super.shutdown();
        if (socket != null) {
            try {
                outputStream.close();
                socket.close();
            } catch (IOException e) {
                Timber.e(e);
            }
            outputStream = null;
            socket = null;
        }
    }

    @Override
    public void run() {
        super.run();
        initialiseDestAddress();
        openSocket();
        int bufferTimeMs = periodMilliseconds() / cotIcons.size();

        while (isRunning) {
            for (CursorOnTarget cot : cotIcons) {
                if (!isRunning) break;
                sendToDestination(cot);
                bufferSleep(bufferTimeMs);
            }
            cotIcons = cotGenerator.generate();
        }
        shutdown();
    }

    @Override
    protected void sendToDestination(CursorOnTarget cot) {
        try {
            outputStream.write(cot.toBytes(dataFormat));
            Timber.i("Sent cot: %s", cot.callsign);
        } catch (IOException e) {
            Timber.e(e);
            shutdown();
        } catch (NullPointerException e) {
            /* Thrown when the thread is cancelled from another thread and we try to access the sockets */
            shutdown();
        }
    }

    protected void initialiseDestAddress() {
        try {
            destIp = InetAddress.getByName(PrefUtils.getString(prefs, Key.DEST_ADDRESS));
        } catch (UnknownHostException e) {
            Timber.e("Error parsing destination address: %s", PrefUtils.getString(prefs, Key.DEST_ADDRESS));
            shutdown();
        }
        destPort = PrefUtils.parseInt(prefs, Key.DEST_PORT);
    }

    protected void openSocket() {
        try {
            socket = new Socket(destIp, destPort);
            outputStream = socket.getOutputStream();
        } catch (ConnectException e) {
            Timber.e(e);
            Timber.e("Invalid TCP server at %s : %d", destIp.getHostAddress(), destPort);
        } catch (IOException e) {
            Timber.e(e);
        }
    }
}
