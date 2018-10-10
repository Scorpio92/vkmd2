package ru.scorpio92.vkmd2.data.repository.internal.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.FileInputStream;
import java.io.FileOutputStream;


public class AndroidPrivateStorage {

    private Context context;
    private ISecurityProvider securityProvider;
    private boolean encryptionEnabled;

    public AndroidPrivateStorage(@NonNull Context context, @Nullable ISecurityProvider securityProvider) {
        this.context = context;
        this.securityProvider = securityProvider;
    }

    public void setEncryptionEnabled(boolean encryptionEnabled) {
        this.encryptionEnabled = encryptionEnabled;
    }

    public void setDataInFile(String fileName, String data) throws Exception {
        FileOutputStream outputStream = null;
        try {
            outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            if (securityProvider != null && encryptionEnabled) {
                data = securityProvider.encrypt(data);
            }
            outputStream.write(data.getBytes());
        } finally {
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
        }
    }

    public String getDataFromFile(String fileName) throws Exception {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = context.openFileInput(fileName);
            int ch;
            StringBuilder temp = new StringBuilder();
            while ((ch = fileInputStream.read()) != -1) {
                temp.append(Character.toString((char) ch));
            }
            String dataFromFile = temp.toString();
            if (securityProvider != null && encryptionEnabled) {
                dataFromFile = securityProvider.decrypt(dataFromFile);
            }
            return dataFromFile;
        } finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
    }

    public boolean fileExist(String fileName) throws Exception {
        String[] arrFileName = context.fileList();
        for (String item : arrFileName) {
            if (item.equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    public boolean deleteFile(String fileName) throws Exception {
        return context.deleteFile(fileName);
    }
}
