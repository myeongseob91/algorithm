import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AppUtil {
	
	static SimpleDateFormat DF_TS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static SimpleDateFormat DF_DT = new SimpleDateFormat("yyMMdd_HHmmss_S");
			
	private AppUtil() {}
	
	static public boolean isEmptyString(Object obj){
		if (obj == null){
			return true;
		}
		
		return isEmptyString(obj.toString());
	}
	
	static public boolean isEmptyString(String str){
		if (str == null 
				|| str.trim().equals("")
				|| str.trim().toUpperCase().equals("NULL")){
			return true;
		}
		
		return false;
	}
	
	static public boolean isNumericString(String str){
		try {
			Double.parseDouble(str);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	static public String[] getStringArray(String str, String regex){
		
		if (isEmptyString(str)){return null;}	
	
		String[] strArray = str.split(regex);;
		
		ArrayList<String> resultList = new ArrayList<String>();
		for (String val :  strArray){
			if (!isEmptyString(val)){
				resultList.add(val.trim());
			}
		}
		 
		return resultList.toArray(new String[resultList.size()]);
	}
	
	static public String getValueCSV(Object str){
		if (isEmptyString(str)){return "\t";}	
		return str.toString();
	}
	
	static public Double[] getDoubleArray(String str, String regex){
		
		if (isEmptyString(str)){return null;}	
		
		String[] strArray = str.split(regex);;
		
		ArrayList<Double> resultList = new ArrayList<Double>();
		for (String val :  strArray){
			if (!isEmptyString(val)){
				try {
					resultList.add(Double.parseDouble(val.trim()));
				} catch (Exception e) {
					return null;
				}
			}
		}
		
		return resultList.toArray(new Double[resultList.size()]);
	}
	
	static public byte[] cloneInputStream(InputStream is){
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		byte[] buffer = new byte[1024];
		int len;
		try {

			while ((len = is.read(buffer)) > -1 ) {
			    baos.write(buffer, 0, len);
			}
			
			return baos.toByteArray();
			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally{
			CloseUtil.closeSafe(baos);
		}
		
	}
	
	static public String getPathString(String str){
		
		try {
			
			String pathStr = str.trim().replace("\\", "/");
			while(pathStr.startsWith("/")){
				pathStr = pathStr.substring(1, pathStr.length());
				pathStr = pathStr.trim();
			}
			while(pathStr.endsWith("/")){
				pathStr = pathStr.substring(0, pathStr.length() -1);
				pathStr = pathStr.trim();
			}
			pathStr = pathStr.trim();
			
			return pathStr;
			
		} catch (Exception e) {
		}
		
		return str;
	}
	
	public static BufferedReader getFileReader(File file) throws UnsupportedEncodingException, FileNotFoundException {
		FileInputStream fis = new FileInputStream(file);  
		return new BufferedReader(new InputStreamReader(fis,"UTF-8"));
	}
	
	public static BufferedWriter getFileWriter(File file, boolean append, String charset) throws UnsupportedEncodingException, FileNotFoundException {
		FileOutputStream fos = new FileOutputStream(file, append);  
		return new BufferedWriter(new OutputStreamWriter(fos, charset));
	}
	
	public static File findFile(File rootDir, final String fileName){
		
		File[] files = rootDir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				if (name.equalsIgnoreCase(fileName)){
					return true;
				}
				return false;
			}
		});
		
		if (files.length == 1){
			return files[0];
		}
		
		return null;
	}
	
	public static File[] findFilesFromName(File rootDir, final String fileName) {
		
		File[] files = rootDir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return removeExtension(name).equalsIgnoreCase(fileName);
			}
		});
		
		return files;
	}
	
	public static String removeExtension(String fileName) {
		
		if (isEmptyString(fileName)) {
			return null;
		}
		
        int index = indexOfExtension(fileName);
        if (index == -1) {
            return fileName;
        }
        
        return fileName.substring(0, index);
	}
	
	public static int indexOfExtension(String fileName) {
		
		if (isEmptyString(fileName)) {
			return -1;
		}
		
        int extensionPos = fileName.lastIndexOf(".");        
        
        final int lastUnixPos = fileName.lastIndexOf('/');
        final int lastWindowsPos = fileName.lastIndexOf('\\');
        int lastSeparator = Math.max(lastUnixPos, lastWindowsPos);
        
        return lastSeparator > extensionPos ? -1 : extensionPos;		
	}
	
	
	public static void copyFile(File inFile, File outFile, boolean isSub) throws IOException {
		
		if (inFile == null 
				|| !inFile.exists()){
			return;
		}
		
		if (outFile == null){
			return;
		}else if (!outFile.exists()){
			outFile.getParentFile().mkdirs();
		}
		
		if (inFile.isFile()){
			FileInputStream fis = null;
			FileOutputStream fos = null;
			try {
				fis = new FileInputStream(inFile);
				fos = new FileOutputStream(outFile);

				int bytesRead = 0;

				byte[] buffer = new byte[1024];
				while ((bytesRead = fis.read(buffer, 0, 1024)) != -1) {
					fos.write(buffer, 0, bytesRead);
				}

			} catch (IOException e) {
				throw e;
			} finally {				
				CloseUtil.closeSafe(fis, fos);
			}
		}else{
			
			for (File subFile : inFile.listFiles()){
				if (subFile.isFile() || isSub){
					copyFile(subFile, new File(outFile, subFile.getName()), isSub);
				}
			}
			
		}
	}
	
	public static byte[] addAllBytes(byte[] array1, byte[] array2){
		if (array1 == null){
			return array2.clone();
		}else if (array2 == null){
			return array1.clone();
		}
		
		byte[] joinArray = new byte[array1.length + array2.length];
		System.arraycopy(array1, 0, joinArray, 0, array1.length);
		System.arraycopy(array2, 0, joinArray, array1.length, array2.length);
		
		return joinArray;
	}
	
	public static String refineNumber(Object number, int disits) {

		NumberFormat f = NumberFormat.getInstance();
		f.setMaximumFractionDigits(disits);

		return f.format(number);

	}
	
	public static String getTimeString(long mTime) {
		return DF_TS.format(new Date(mTime));
	}
	
	public static String getDownoladTime(long mTime) {
		return DF_DT.format(new Date(mTime));
	}
	
	public static boolean isTrue(Object obj) {
		if (obj != null) {
			String bool = obj.toString();
			bool = bool.trim().toUpperCase();	
			if (bool.equalsIgnoreCase("YES") 
					|| bool.equalsIgnoreCase("TRUE") 
					|| bool.equalsIgnoreCase("1") 
					|| bool.equalsIgnoreCase("Y")){
				return true;
			}
		}
		
		return false;
	}
}
