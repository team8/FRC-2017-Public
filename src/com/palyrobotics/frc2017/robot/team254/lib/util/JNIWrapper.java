package com.palyrobotics.frc2017.robot.team254.lib.util;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class JNIWrapper
{
  public JNIWrapper() {}
  
  public static native ByteBuffer getPortWithModule(byte paramByte1, byte paramByte2);
  
  static boolean libraryLoaded = false;
  static File jniLibrary = null;
  
  public static native ByteBuffer getPort(byte paramByte);
  
  static {
    try { if (!libraryLoaded)
      {

        jniLibrary = File.createTempFile("libwpilibJavaJNI", ".so");
        
        jniLibrary.deleteOnExit();
        
        byte[] buffer = new byte['e'];
        


        InputStream is = JNIWrapper.class.getResourceAsStream("/linux-arm/libwpilibJavaJNI.so");
        
        OutputStream os = new java.io.FileOutputStream(jniLibrary);
        try
        {
          int readBytes;
          while ((readBytes = is.read(buffer)) != -1)
          {
            os.write(buffer, 0, readBytes);
          }
          
        }
        finally
        {
          os.close();
          is.close();
        }
        

        libraryLoaded = true;
      }
      
      System.load(jniLibrary.getAbsolutePath());
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      System.exit(1);
    }
  }
}
