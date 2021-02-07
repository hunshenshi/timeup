package com.szw.timeup.helper;

import android.content.Context;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by monkey on 2021/02/02.
 */
public class Util {
    public static void writeFile(Context context, String filename, String filecontent) throws IOException {
        FileOutputStream output = context.openFileOutput(filename, Context.MODE_APPEND);
        output.write(filecontent.getBytes());  //将String字符串以字节流的形式写入到输出流中
        output.close();         //关闭输出流
    }
}
