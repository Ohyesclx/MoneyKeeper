package me.bakumon.moneykeeper.utill;

import com.snatik.storage.Storage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.bakumon.moneykeeper.App;
import me.bakumon.moneykeeper.database.AppDatabase;
import me.bakumon.moneykeeper.ui.setting.BackupBean;

/**
 * 备份相关工具类
 *
 * @author Bakumon https:/bakumon.me
 */
public class BackupUtil {
    public static final String BACKUP_DIR = "backup_moneykeeper";
    public static final String AUTO_BACKUP_PREFIX = "MoneyKeeperBackupAuto";
    public static final String USER_BACKUP_PREFIX = "MoneyKeeperBackupUser";
    public static final String SUFFIX = ".db";

    private static boolean backupDB(String fileName) {
        Storage storage = new Storage(App.getINSTANCE());
        boolean isWritable = Storage.isExternalWritable();
        if (!isWritable) {
            return false;
        }
        String path = storage.getExternalStorageDirectory() + File.separator + BACKUP_DIR;
        if (!storage.isDirectoryExists(path)) {
            storage.createDirectory(path);
        }
        return storage.copy(App.getINSTANCE().getDatabasePath(AppDatabase.DB_NAME).getPath(), path + File.separator + fileName);
    }

    public static boolean autoBackup() {
        String fileName = AUTO_BACKUP_PREFIX + SUFFIX;
        return backupDB(fileName);
    }

    public static boolean userBackup() {
        String fileName = USER_BACKUP_PREFIX + SUFFIX;
        return backupDB(fileName);
    }

    public static boolean restoreDB(String restoreFile) {
        Storage storage = new Storage(App.getINSTANCE());
        if (storage.isFileExist(restoreFile)) {
            return storage.copy(restoreFile, App.getINSTANCE().getDatabasePath(AppDatabase.DB_NAME).getPath());
        }
        return false;
    }

    public static List<BackupBean> getBackupFiles() {
        Storage storage = new Storage(App.getINSTANCE());
        String dir = storage.getExternalStorageDirectory() + File.separator + BACKUP_DIR;
        List<BackupBean> backupBeans = new ArrayList<>();
        BackupBean bean;
        List<File> files = storage.getFiles(dir);
        if (files == null){
            return backupBeans;
        }
        File fileTemp;
        for (int i = 0; i < files.size(); i++) {
            fileTemp = files.get(i);
            bean = new BackupBean();
            bean.file = fileTemp;
            bean.name = fileTemp.getName();
            bean.size = storage.getReadableSize(fileTemp);
            bean.time = fileTemp.lastModified() + "前";
            backupBeans.add(bean);
        }
        return backupBeans;
    }
}