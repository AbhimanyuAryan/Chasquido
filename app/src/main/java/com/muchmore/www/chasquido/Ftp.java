package com.muchmore.www.chasquido;

import android.util.Log;
import android.widget.TextView;

import org.apache.commons.net.ftp.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.spec.ECField;

public class Ftp {
    public FTPClient mFTPClient = null;

    // Connect to the FTP server
    public boolean ftpConnect(String host, String username, String password, int port){
        try{
            mFTPClient = new FTPClient();
            // connecting to the host

            Log.d("MyMessage","got entered");
            mFTPClient.connect(host, port);
            Log.d("MyMessage", "Connected");
            // now check the reply code, if positive mean connection success
            if(FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())){
                //login using username & password
                Log.d("MyMessage", "Getting Reply From Server");
                boolean status = mFTPClient.login(username, password);

                /*Set File Transfer
                To avoid corruption issue you must specified a correct transfer mode,
                such as ASCII_FILE_TYPE, BINARY_FILE_TYPE, etc. Here, I use Binary file type
                for transferring text, image, and compressed files.
                 */
                mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
                mFTPClient.enterLocalPassiveMode();

                return status;
            }

        }catch (Exception e){
            Log.d("MyMessage", "Exception log starts here");
            e.printStackTrace();
            Log.d("MyMessage", "Error: could not connect to host" + host);
        }

        return false;
    }

    // METHOD TO DISCONNECT FROM FTP server
    public boolean ftpDisconnect(){
        try{
            mFTPClient.logout();
            mFTPClient.disconnect();
            return true;
        }catch(Exception e){
            Log.d("ERRO", "Error occured while disconnecting from ftp server.");
        }

        return false;
    }

    //Method to get current working directory
    public String ftpGetCurrentWorkingDirectory(){
        try{
            String workingDir = mFTPClient.printWorkingDirectory();
            return workingDir;
        }catch (Exception e){
            Log.d("ERRO", "Error: could not get current working directory");
        }

        return null;
    }

    // method to change working directory
    public boolean ftpChangeDirectory(String directory_path, String userName){

        try{
            Log.d("ftplog for" + userName ,"Entered Try");
            int returnCode = mFTPClient.getReplyCode();
            if(returnCode == 550){
                Log.d("ftplog for" + userName ,"file directory is unavailable");

                this.ftpMakeDirectory("vikas/" + userName);
                Log.d("ftplog for" + userName, "Created Directory");

            }else {
                Log.d("ftplog for" + userName ,"File Directory Present changing dir");
                mFTPClient.changeWorkingDirectory(directory_path);
                returnCode = mFTPClient.getReplyCode();
                if(returnCode == 550){
                    return false;
                }
            }
        }catch (Exception e){
            Log.d("ERRO", "Error: could not change directory to " + directory_path);
        }
        return true;
    }

    //Method to create a new directory
    public boolean ftpMakeDirectory(String new_dir_path){
        try{
            Log.d("ftplog for"  ," creating directory " + new_dir_path);
            boolean status = mFTPClient.makeDirectory(new_dir_path);
            return status;
        }catch (Exception e){
            Log.d("ERRO", "Error: could not create new directory named" + new_dir_path);
        }
        return false;
    }


    // Method to list all files in directory
    public void ftpPrintFilesList(String dir_path){
        try{
            FTPFile[] ftpFiles = mFTPClient.listFiles(dir_path);
            int length = ftpFiles.length;

            for(int i=0; i<length; i++){
                String name = ftpFiles[i].getName();
                boolean isFile = ftpFiles[i].isFile();

                if(isFile){
                    Log.i("ERRO", "File: " + name);
                }else{
                    Log.i("ERRO", "Directory: " + name);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Method to delete/remove a directory

    public boolean ftpRemoveDirectory(String dir_path){
        try{
            boolean status = mFTPClient.removeDirectory(dir_path);
            return status;
        }catch (Exception e){
            Log.d("ERRO", "Error: could not remove directory named" + dir_path);
        }
        return false;
    }

    // Method to delete a file:

    public boolean ftpRemoveFile(String filePath){
        try{
            boolean status = mFTPClient.deleteFile(filePath);
            return status;
        }catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }

    //method to rename a file
    public boolean ftpRenameFile(String from, String to){
        try{
            boolean status = mFTPClient.rename(from,to);
            return status;
        }catch (Exception e){
            Log.d("ERRO", "couldn't not rename file:" + from + " to" + to);
        }

        return false;
    }


    //Method to download a file from FTP server:

    public boolean ftpDownload(String srcFilePath, String desFilePath){
        boolean status = false;
        try{
            FileOutputStream desFileStream = new FileOutputStream(desFilePath);
            status = mFTPClient.retrieveFile(srcFilePath, desFileStream);
            desFileStream.close();

            return status;
        }catch (Exception e){
            Log.d("ERRO", "Download failed");
        }
        return status;
    }

    //Method to upload a file to FTP server:
    public boolean ftpUpload(String srcFilePath, String desFileName, String desDirectory, String userName){
        boolean status = false;
        try {
            FileInputStream srcFileStream = new FileInputStream(srcFilePath);

            // change working directory to the destination directory
            if (ftpChangeDirectory(desDirectory, userName)) {
                status = mFTPClient.storeFile(desFileName, srcFileStream);
            }
            Log.d("MyMessage", "Upload Successful");
            srcFileStream.close();
            return status;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("MyMessage", "upload failed");
        }
        return status;
    }

    public void ftpMyUpload(String path, String name){
        String imagePath = path;
        try {
            FileInputStream in = new FileInputStream(new File(imagePath));

            boolean result = mFTPClient.storeFile("/vikas/"+name+".jpg", in);
            Log.d("MyMessage", "File Upload Successful");
            in.close();
        }catch(FileNotFoundException e){
            Log.d("MyMessage", "Could get file");
            e.printStackTrace();
        }catch(IOException e){
            Log.d("MyMessage", "Couldn't Upload");
            e.printStackTrace();
        }

    }
}
