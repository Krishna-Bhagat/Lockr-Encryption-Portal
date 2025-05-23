package com.hades.hades_portal.crypto;

/*This Class Contains 02 Functions:
 * Encryption Function
 * Decryption Function
 */
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class EncDec {
    int flag=0,shiftby=2;String key="";
    double percent;

    public void encrypt(String filename,
                        String dirname,String key,int rounds)			//encrypt function
    {

        try{
            String fname=filename;
            if (filename.contains("."))
                fname =filename.substring(0,filename.lastIndexOf("."));


            File dir = new File(dirname+"/TempFiles");
            if(!dir.exists())
                dir.mkdir();
            //make a folder(if donot exist) for temporary files which will be deleted at end of prg

            RandomAccessFile fn =
                    new RandomAccessFile(dirname+"/"+filename, "rw");
            RandomAccessFile in =
                    new RandomAccessFile(dirname+"/"+"TempFiles/cp-temp.enc", "rw");
            RandomAccessFile outTemp =
                    new RandomAccessFile(dirname+"/"+"TempFiles/enc-T.enc", "rw");
            RandomAccessFile out =
                    new RandomAccessFile(dirname+"/"+fname+".enc", "rw");

            FunctionSet.copyFile(dirname+"/"+filename, dirname+"/"+"TempFiles/cp-temp.enc");//Faster FileCopy using File Channels

            FunctionSet.rounds(in, outTemp, key, shiftby,rounds);	//xor

            FunctionSet.shuffle(outTemp, out);		//shuffle

            File f1 = new File(dirname+"/"+"TempFiles/cp-temp.enc");
            File f2 = new File(dirname+"/"+"TempFiles/enc-T.enc");
            f1.delete();f2.delete();

            fn.close();in.close();out.close();	//Release Resources
        }
        catch ( IOException e) {
            System.out.println(e);
        }
    }



    public void decrypt(String filename,String extname,
                        String dirname,String key,int rounds) //decrypt fxn
    {

        try{
            String fname=filename;
            if (filename.contains("."))
                fname =filename.substring(0,filename.lastIndexOf("."));
            extname=extname.substring(extname.lastIndexOf(".") + 1);
            File dir = new File(dirname+"/TempFiles");
            if(!dir.exists())
                dir.mkdir();			//make a folder(if donot exist) for temporary files which will b deleted at end of prg

            RandomAccessFile fn =
                    new RandomAccessFile(dirname+"/"+filename, "rw");
            RandomAccessFile in =
                    new RandomAccessFile(dirname+"/"+"TempFiles/cp-temp.dec", "rw");
            RandomAccessFile outTemp =
                    new RandomAccessFile(dirname+"/"+"TempFiles/dec-T.dec", "rw");
            RandomAccessFile out =
                    new RandomAccessFile(dirname+"/"+fname+"."+extname, "rw");

            FunctionSet.copyFile(dirname+"/"+filename, dirname+"/"+"TempFiles/cp-temp.dec");//Faster FileCopy using File Channels


            FunctionSet.shuffle(fn, in);							//deshuffle

            FunctionSet.rounds(in, out, key, shiftby,rounds);	//xor

            File f1 = new File(dirname+"/"+"TempFiles/cp-temp.dec");
            File f2 = new File(dirname+"/"+"TempFiles/dec-T.dec");
            f1.delete();f2.delete();


            //release resources
            in.close();out.close();fn.close();
        }
        catch ( IOException e) {
            System.out.println(e);
        }
    }
}
