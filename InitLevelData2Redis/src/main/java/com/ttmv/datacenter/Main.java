package com.ttmv.datacenter;

import com.ttmv.datacenter.util.FileInputReadUtil;

/**
 * 
 * @author kate
 * 读取四种类型的文件，解析读取的数据，并将数据写入redis
 *
 */
public class Main {

	public static void main(String[] args) {
		FileInputReadUtil.fileRead(args[0], args[1]);
		//FileInputReadUtil.fileRead("D:\\AnchorTbean.txt", "tdou");
		System.out.println("#######数据录入完成######");
		//System.exit(0);
	}

}
