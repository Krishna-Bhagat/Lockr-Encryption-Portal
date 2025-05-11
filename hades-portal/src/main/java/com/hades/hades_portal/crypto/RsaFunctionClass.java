package com.hades.hades_portal.crypto;

/*This Class Contains Functions for RSA encryption::
 * phi function
 * privatekey Function
 * EncDec function
 * StringToBytes function
 * BytesToString function
 * WriteEncKey function
 *EdGen function
 */
import com.hades.hades_portal.model.User;
import com.hades.hades_portal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.util.Date;
import java.util.Random;
import java.util.Optional;

@Component
public class RsaFunctionClass {

    @Autowired
    private UserRepository userRepository;

    //Calculate phi(p,q)=(p-1)(q-1)
    static public BigInteger phi(BigInteger p,BigInteger q)
    {
        BigInteger pMinusOne=p.subtract(BigInteger.ONE);		//p-1
        BigInteger qMinusOne=q.subtract(BigInteger.ONE);		//q-1
        BigInteger phi=pMinusOne.multiply(qMinusOne);						//phi=(p-1)(q-1)
        return phi;
    }


    // Compute the private key 'd' from Public key 'e' and phi
    private static BigInteger privateKey(BigInteger e,
                                         BigInteger phi) {

        BigInteger elocal = e;
        BigInteger philocal = phi;
        BigInteger xOld = new BigInteger("1");
        BigInteger yOld = new BigInteger("0");
        BigInteger x = new BigInteger("0");
        BigInteger y = new BigInteger("1");
        BigInteger xNew, yNew, q, r;
        BigInteger zero = new BigInteger("0");

        // Extended Euclidean algorithm
        while(!philocal.equals(zero))		//phi!=0
        {
            q = elocal.divide(philocal);		//quotient=e/phi
            r = elocal.mod(philocal);			//remainder=e%phi
            xNew = xOld.subtract(q.multiply(x));
            yNew = yOld.subtract(q.multiply(y));
            elocal = philocal;
            philocal = r;
            xOld = x;
            yOld = y;
            x = xNew;
            y = yNew;
        }

        // We want d to be positive
        // if xOld is less than zero we add it to x else, we just return it
        if(xOld.compareTo(zero) == -1) {
            return xOld.add(x);
        }
        return xOld;

    }


    // Do modular exponentiation for the expression c_m^e_dmodn=c_m -->for encryption use m, e And for dec use c,d
    // Can be used for encryption (with public key e) or decryption (with private key d).m stands for plain text and c for cipher text

    public BigInteger EncDec(BigInteger m_c,
                             BigInteger e_d, BigInteger n)
    {
        BigInteger zero = new BigInteger("0");
        BigInteger one = new BigInteger("1");
        BigInteger two = one.add(one);

        // Base Case
        if (e_d.equals(zero))
            return one;				//m^e mod n-->e=0
        if (e_d.equals(one))
            return m_c.mod(n);		//m^e mod n-->e=1

        if (e_d.mod(two).equals(zero)) {
            // Calculates the square root of the answer
            BigInteger answer = EncDec(m_c, e_d.divide(two), n);
            // Reuses the result of the square root
            return (answer.multiply(answer)).mod(n);
        }

        return (m_c.multiply(EncDec(m_c,e_d.subtract(one),n))).mod(n);
    }


    public String WriteEncKey(BigInteger Enkey,
                              BigInteger e,
                              String dirname,
                              String filename,int rounds)
    {
        String returnname="",name="";
        filename=filename.substring(filename.lastIndexOf("/")+1);
        if (filename.contains("."))
            name="CipherKey-"+filename.substring(0,filename.lastIndexOf("."))+".txt";
        else
            name="CipherKey-"+filename+".txt";

        // Fetch the username from the database using the public key 'e'
        String unm = "";
        Optional<User> userOptional = userRepository.findByPublicKey(e.toString());
        if (userOptional.isPresent()) {
            unm = userOptional.get().getUsername();
        }

        Date dt=new Date();
        try
        {
            File ptr=new File(dirname+"/"+name);
            if(ptr.exists())
            {
                RandomAccessFile out=
                        new RandomAccessFile(dirname+"/"+"Copy-"+name,"rw");
                out.seek(0);
                out.writeBytes("Encrypted Key for File : "+filename+" Generated on: "+dt+"\n\nMode Used:'"+rounds+"'\\n\nNote: This is NOT the Secret(private) Key, but Just the RSA Encrypted Cipher text, No Need To keep It a secret:)\n\nEncrypted Key:\n"+Enkey+"\n\nEncrypted for the user '"+unm+"' using the following Public Key:\n"+e);
                out.close();
                returnname=dirname+"/"+"Copy-"+name;
            }
            else
            {
                RandomAccessFile out=
                        new RandomAccessFile(dirname+"/"+name,"rw");
                out.seek(0);
                out.writeBytes("Encrypted Key for File : "+filename+" Generated on: "+dt+"\n\nMode Used:'"+rounds+"'\n\nNote: This is NOT the Secret(private) Key, but Just the RSA Encrypted Cipher text, No Need To keep It a secret:)\n\nEncrypted Key:\n"+Enkey+"\n\nEncrypted for the user '"+unm+"' using the following Public Key:\n"+e);
                out.close();
                returnname=dirname+"/"+name;
            }

        } catch (IOException ex) {
            System.out.println(ex);
            //e.printStackTrace();
        }
        return returnname;
    }

    public void generateKeysIfNotExists(String uname)
    {
        Optional<User> userOptional = userRepository.findByUsername(uname);
        if (userOptional.isEmpty()) return;

        User user = userOptional.get();
        if (user.getPublicKey() != null && !user.getPublicKey().equals("0")) return;

        Random rand = new Random();
        BigInteger p = BigInteger.probablePrime(1000, rand);
        BigInteger q = BigInteger.probablePrime(990, rand);
        BigInteger e = BigInteger.probablePrime(1010, rand);
        // Get the modulus n=p*q
        BigInteger n = p.multiply(q);
        BigInteger phi = phi(p, q);
        BigInteger d = privateKey(e, phi);

        user.setPublicKey(e.toString());
        user.setPrivateKey(d.toString());
        user.setNValue(n.toString());
        userRepository.save(user);

    }
}
