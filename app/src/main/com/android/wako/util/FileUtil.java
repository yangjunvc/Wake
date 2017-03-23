package com.android.wako.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class FileUtil {
    private static final String TAG = "FileUtil";

    public static String saveImage(Bitmap bitmap, String fileName,String path){
        File file = new File(path + fileName);
        FileOutputStream outputStream;
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();
            } catch (Exception e) {
                Log.e("", e.toString());
                e.printStackTrace();
            }
        }
        return file.getAbsolutePath();
    }
    
    /**
     * 获取文件MD5值
     * 
     * @param file
     * @return
     */
    public static String getMd5ByFile(File file) {
        String value = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }

    /**
     * 获取文件大小
     * 
     * @param file
     * @return
     */
    public static long getFileLength(File file) throws IOException {
        FileInputStream fis = null;
        fis = new FileInputStream(file);
        return fis.available();
    }

    /**
     * 读取文件到二进制
     * 
     * @author WikerYong Email:<a href="#">yw_312@foxmail.com</a>
     * @version 2012-3-23 上午11:47:06
     * @param file
     * @return
     * @throws IOException
     */
    public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();

        if (length > Integer.MAX_VALUE) {
            // File is too large
        }

        byte[] bytes = new byte[(int) length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            LogUtil.d(TAG, "不能读取文件: " + file.getName());
        }

        is.close();
        return bytes;
    }

    /**
     * 获取标准文件大小，如30KB，15.5MB
     * 
     * @param file
     * @return
     * @throws IOException
     */
    public static String getFileSize(File file) throws IOException {
        long size = getFileLength(file);
        DecimalFormat df = new DecimalFormat("###.##");
        float f;
        if (size < 1024 * 1024) {
            f = (float) ((float) size / (float) 1024);
            return (df.format(new Float(f).doubleValue()) + " KB");
        } else {
            f = (float) ((float) size / (float) (1024 * 1024));
            return (df.format(new Float(f).doubleValue()) + " MB");
        }

    }

    /**
     * 复制文件
     * 
     * @param f1
     *            源文件
     * @param f2
     *            目标文件
     * @throws Exception
     */
    public static void copyFile(File f1, File f2) throws Exception {
        int length = 2097152;
        FileInputStream in = new FileInputStream(f1);
        FileOutputStream out = new FileOutputStream(f2);
        FileChannel inC = in.getChannel();
        FileChannel outC = out.getChannel();
        ByteBuffer b = null;
        while (true) {
            if (inC.position() == inC.size()) {
                inC.close();
                outC.close();
            }
            if ((inC.size() - inC.position()) < length) {
                length = (int) (inC.size() - inC.position());
            } else
                length = 2097152;
            b = ByteBuffer.allocateDirect(length);
            inC.read(b);
            b.flip();
            outC.write(b);
            outC.force(false);
        }
    }

    public static boolean copyFile(String fileFrom, String fileTo) {
        try {
            FileInputStream in = new FileInputStream(fileFrom);
            FileOutputStream out = new FileOutputStream(fileTo);
            byte[] bt = new byte[1024];
            int count;
            while ((count = in.read(bt)) > 0) {
                out.write(bt, 0, count);
            }
            in.close();
            out.close();
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /** 
     * 复制单个文件 
     * @param oldPath String 原文件路径 如：c:/fqf.txt 
     * @param newPath String 复制后路径 如：f:/fqf.txt 
     * @return boolean 
     */ 
   public static String copyFileStream(FileInputStream inStream, File newfile) { 
	   FileOutputStream fs = null ;
       try { 
           int bytesum = 0; 
           int byteread = 0; 
           if (!newfile.exists()) { //文件存在时 
               fs = new FileOutputStream(newfile); 
               byte[] buffer = new byte[1444]; 
               while ( (byteread = inStream.read(buffer)) != -1) { 
                   bytesum += byteread; //字节数 文件大小 
                   fs.write(buffer, 0, byteread); 
               } 
               inStream.close(); 
           }
           return newfile.getAbsolutePath() ;
       } 
       catch (Exception e) { 
           LogUtil.d("fileutil","复制单个文件操作出错"); 
           e.printStackTrace(); 
       } finally{
			try {
				 if(inStream != null)
					 inStream.close();
				 if(fs != null)
					 fs.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
       }
       return null ;
   } 
   
    /**
     * 检查文件是否存在
     * 
     * @param fileName
     * @return
     * @throws IOException
     */
    public static boolean existFile(String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            LogUtil.d(TAG, "文件未找到:" + fileName);
        }
        return file.exists();
    }

    /**
     * 删除文件
     * 
     * @param fileName
     */
    public static void deleteFile(String fileName) {
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                LogUtil.d(TAG, "文件未找到:" + fileName);
            }
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取文件到字符串
     * 
     * @param fileName
     * @return
     * @throws IOException
     */
    public static String readFile(String fileName) {
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                LogUtil.d(TAG, "文件未找到:" + fileName);
            }

            BufferedReader in = new BufferedReader(new FileReader(file));
            StringBuffer sb = new StringBuffer();
            String str = "";
            while ((str = in.readLine()) != null) {
                sb.append(str);
            }
            in.close();
            return sb.toString();
        } catch (Exception e) {
            LogUtil.d(TAG, " readFile exception" + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取目录所有所有文件和文件夹
     * 
     * @param fileName
     * @return
     * @throws IOException
     */
    public static List<File> listFiles(String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            LogUtil.d(TAG, "文件未找到:" + fileName);
        }
        return Arrays.asList(file.listFiles());
    }

    /**
     * 创建目录
     * 
     * @param dir
     */
    public static void mkdir(String dir) {
        String dirTemp = dir;
        File dirPath = new File(dirTemp);
        if (!dirPath.exists()) {
            dirPath.mkdir();
        }
    }

    /**
     * 新建文件
     * 
     * @param fileName
     *            String 包含路径的文件名 如:E:\phsftp\src\123.txt
     * @param content
     *            String 文件内容
     */
    public static void createNewFile(String fileName, String content) {
        try {
            String fileNameTemp = fileName;
            File filePath = new File(fileNameTemp);
            if (!filePath.exists()) {
                filePath.createNewFile();
            }
            FileWriter fw = new FileWriter(filePath);
            PrintWriter pw = new PrintWriter(fw);
            String strContent = content;
            pw.println(strContent);
            pw.flush();
            pw.close();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 删除文件夹
     * 
     * @param folderPath
     *            文件夹路径
     */
    public static void delFolder(String folderPath) {
        // 删除文件夹里面所有内容
        delAllFile(folderPath);
        String filePath = folderPath;
        File myFilePath = new File(filePath);
        // 删除空文件夹
        myFilePath.delete();
    }

    /**
     * 删除文件夹里面的所有文件
     * 
     * @param path
     *            文件夹路径
     */
    public static void delAllFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        String[] childFiles = file.list();
        File temp = null;
        for (int i = 0; i < childFiles.length; i++) {
            // File.separator与系统有关的默认名称分隔符
            // 在UNIX系统上，此字段的值为'/'；在Microsoft Windows系统上，它为 '\'。
            if (path.endsWith(File.separator)) {
                temp = new File(path + childFiles[i]);
            } else {
                temp = new File(path + File.separator + childFiles[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + File.separatorChar + childFiles[i]);// 先删除文件夹里面的文件
                delFolder(path + File.separatorChar + childFiles[i]);// 再删除空文件夹
            }
        }
    }


    /**
     * 复制文件夹
     * 
     * @param oldPath
     *            String 源文件夹路径 如：E:/phsftp/src
     * @param newPath
     *            String 目标文件夹路径 如：E:/phsftp/dest
     * @return boolean
     */
    public static void copyFolder(String oldPath, String newPath) throws IOException {
        // 如果文件夹不存在 则新建文件夹
        mkdir(newPath);
        File file = new File(oldPath);
        String[] files = file.list();
        File temp = null;
        for (int i = 0; i < files.length; i++) {
            if (oldPath.endsWith(File.separator)) {
                temp = new File(oldPath + files[i]);
            } else {
                temp = new File(oldPath + File.separator + files[i]);
            }

            if (temp.isFile()) {
                FileInputStream input = new FileInputStream(temp);
                FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
                byte[] buffer = new byte[1024 * 2];
                int len;
                while ((len = input.read(buffer)) != -1) {
                    output.write(buffer, 0, len);
                }
                output.flush();
                output.close();
                input.close();
            }
            if (temp.isDirectory()) {// 如果是子文件夹
                copyFolder(oldPath + "/" + files[i], newPath + "/" + files[i]);
            }
        }
    }

    /**
     * 移动文件到指定目录
     * 
     * @param oldPath
     *            包含路径的文件名 如：E:/phsftp/src/ljq.txt
     * @param newPath
     *            目标文件目录 如：E:/phsftp/dest
     */
    public static void moveFile(String oldPath, String newPath) throws IOException {
        copyFile(oldPath, newPath);
        deleteFile(oldPath);
    }

    /**
     * 移动文件到指定目录，不会删除文件夹
     * 
     * @param oldPath
     *            源文件目录 如：E:/phsftp/src
     * @param newPath
     *            目标文件目录 如：E:/phsftp/dest
     */
    public static void moveFiles(String oldPath, String newPath) throws IOException {
        copyFolder(oldPath, newPath);
        delAllFile(oldPath);
    }

    /**
     * 移动文件到指定目录，会删除文件夹
     * 
     * @param oldPath
     *            源文件目录 如：E:/phsftp/src
     * @param newPath
     *            目标文件目录 如：E:/phsftp/dest
     */
    public static void moveFolder(String oldPath, String newPath) throws IOException {
        copyFolder(oldPath, newPath);
        delFolder(oldPath);
    }

    /**
     * 解压zip文件 说明:本程序通过ZipOutputStream和ZipInputStream实现了zip压缩和解压功能.
     * 问题:由于java.util.zip包并不支持汉字,当zip文件中有名字为中文的文件时,
     * 就会出现异常:"Exception  in thread "main " java.lang.IllegalArgumentException
     * at java.util.zip.ZipInputStream.getUTF8String(ZipInputStream.java:285)
     * 
     * @param srcDir
     *            解压前存放的目录
     * @param destDir
     *            解压后存放的目录
     * @throws Exception
     */
    public static void unZip(String srcDir, String destDir) throws IOException {
        int leng = 0;
        byte[] b = new byte[1024 * 2];
        /** 获取zip格式的文件 **/
        File[] zipFiles = new ExtensionFileFilter("zip").getFiles(srcDir);
        if (zipFiles != null && !"".equals(zipFiles)) {
            for (int i = 0; i < zipFiles.length; i++) {
                File file = zipFiles[i];
                /** 解压的输入流 * */
                ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
                ZipEntry entry = null;
                while ((entry = zis.getNextEntry()) != null) {
                    File destFile = null;
                    if (destDir.endsWith(File.separator)) {
                        destFile = new File(destDir + entry.getName());
                    } else {
                        destFile = new File(destDir + File.separator + entry.getName());
                    }
                    /** 把解压包中的文件拷贝到目标目录 * */
                    FileOutputStream fos = new FileOutputStream(destFile);
                    while ((leng = zis.read(b)) != -1) {
                        fos.write(b, 0, leng);
                    }
                    fos.close();
                }
                zis.close();
            }
        }
    }

    /**
     * 压缩文件 说明:本程序通过ZipOutputStream和ZipInputStream实现了zip压缩和解压功能.
     * 问题:由于java.util.zip包并不支持汉字,当zip文件中有名字为中文的文件时,
     * 就会出现异常:"Exception  in thread "main " java.lang.IllegalArgumentException
     * at java.util.zip.ZipInputStream.getUTF8String(ZipInputStream.java:285)
     * 
     * @param srcDir
     *            压缩前存放的目录
     * @param destDir
     *            压缩后存放的目录
     * @throws Exception
     */
    public static void zip(String srcDir, String destDir) throws IOException {
        String tempFileName = null;
        byte[] buf = new byte[1024 * 2];
        int len;
        // 获取要压缩的文件
        File[] files = new File(srcDir).listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    FileInputStream fis = new FileInputStream(file);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    if (destDir.endsWith(File.separator)) {
                        tempFileName = destDir + file.getName() + ".zip";
                    } else {
                        tempFileName = destDir + File.separator + file.getName() + ".zip";
                    }
                    FileOutputStream fos = new FileOutputStream(tempFileName);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    ZipOutputStream zos = new ZipOutputStream(bos);// 压缩包

                    ZipEntry ze = new ZipEntry(file.getName());// 压缩包文件名
                    zos.putNextEntry(ze);// 写入新的ZIP文件条目并将流定位到条目数据的开始处

                    while ((len = bis.read(buf)) != -1) {
                        zos.write(buf, 0, len);
                        zos.flush();
                    }
                    bis.close();
                    zos.close();

                }
            }
        }
    }

    /**
     * 读取数据
     * 
     * @param inSream
     * @param charsetName
     * @return
     * @throws Exception
     */
    public static String readData(InputStream inSream, String charsetName) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = inSream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inSream.close();
        return new String(data, charsetName);
    }

    /**
     * 一行一行读取文件，适合字符读取，若读取中文字符时会出现乱码
     * 
     * @param path
     * @return
     * @throws Exception
     */
    public static Set<String> readFileLine(String path) throws IOException {
        Set<String> datas = new HashSet<String>();
        FileReader fr = new FileReader(path);
        BufferedReader br = new BufferedReader(fr);
        String line = null;
        while ((line = br.readLine()) != null) {
            datas.add(line);
        }
        br.close();
        fr.close();
        return datas;
    }

    public static void main(String[] args) {
        try {
            unZip("c:/test", "c:/test");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 根据uri获取真实路径和名称
     * @param context
     * @param contentUri
     * @return
     */
    public static String getRealPathFromURI(Context context,Uri contentUri) 
	{
		if(contentUri == null){
			return null ;
		}
		
		if(contentUri.toString().startsWith("file")){
			return contentUri.getPath();
		}else{
			 contentUri = Uri.parse(contentUri.toString().replaceAll("%3A", ":").replaceAll("%2F", "/"));
		     String[] proj = { MediaStore.Audio.Media.DATA };
		     Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
		     if(cursor!=null && cursor.moveToFirst()){
			     int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
			     cursor.moveToFirst();
			     String result =  cursor.getString(column_index);
			     cursor.close();
			     return result ;
		     }
		     return null ;
		}
	}

    public static String getFileName(String filePath) {
		if (StringUtil.isEmpty(filePath))
			return "";
		return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
	}
	
	public static String getFileName(URL url) {
		if (StringUtil.isEmpty(url.toString()))
			return "";
		int pos = url.toString().lastIndexOf("/"); 
		if(pos == -1){
			return "" ;
		}
		return url.toString().substring(pos+1);
	}

	public static boolean checkSDCardAvailable(){
		return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}
	
    public static String getPathFormUri(Activity con, Uri uri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = con.managedQuery(uri, proj, null, null, null);
        String path = "";
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(column_index);
        }
        return path;
    }
	
}
