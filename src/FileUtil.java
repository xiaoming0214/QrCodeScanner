import java.io.File;

/**
 * Created by bujue on 16/11/26.
 */
public class FileUtil {

    public static void deleteFile(String path){
        File file = new File(path);
        if(file == null || !file.exists()){
            System.out.println("删除文件不存在>>path = "+path);
            return;
        }
        deleteFile(file);
    }

    //递归删除文件夹
    public static void deleteFile(File file) {
        if (file.exists()) {//判断文件是否存在
            if (file.isFile()) {//判断是否是文件
                file.delete();//删除文件
            } else if (file.isDirectory()) {//否则如果它是一个目录
                File[] files = file.listFiles();//声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) {//遍历目录下所有的文件
                    deleteFile(files[i]);//把每个文件用这个方法进行迭代
                }
                file.delete();//删除文件夹
            }
        } else {
            System.out.println("所删除的文件不存在");
        }
    }
}
