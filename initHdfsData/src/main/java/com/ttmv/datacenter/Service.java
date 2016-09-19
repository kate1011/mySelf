package com.ttmv.datacenter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * Created by zkt on 2016/4/1.
 */
public class Service {


    public static void inserDataToHdfs(String localFilePath, String targetFilePath) throws Exception {

        List<String> ls = readFileByLines(localFilePath);
        boolean flag = HadoopFSOperations.isDirExit(targetFilePath);
        if (flag) {
            HadoopFSOperations.deleteHDFSFile(targetFilePath);
        }
        long starTime=System.currentTimeMillis();
       
        Configuration hdfsConfig = new Configuration();
        FileSystem hdfs = FileSystem.newInstance(hdfsConfig);
		Path path = new Path(targetFilePath);
		if (!hdfs.exists(path)) {
			hdfs.create(path).close();
		}
        for (String data : ls) {
        	FSDataOutputStream os = hdfs.append(path);
    		os.write(data.getBytes("UTF-8"));
    		// start a newline
    		os.write("\n".getBytes());
    		os.close();

        }
        hdfs.close();
        long endTime=System.currentTimeMillis();
        System.out.println("执行总共花费时间："+(endTime-starTime)/1000+"s");

    }
    
    
    
    
    
    
    
    

    public static List<String> readFileByLines(String fileName) {

        List<String> ls = new ArrayList<String>();
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                ls.add(tempString);
                //System.out.println("从文件读取的内容：" + reader);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }

        return ls;
    }


}
