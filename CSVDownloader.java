import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;


public class CSVDownloader extends JFrame {

	JLabel label;
	JLabel label2;
	JTextField tf;
	JTextField tf2;
	JLabel label3;
	JButton button;
	String dir;
	String u;
	
	public CSVDownloader() {
		setLayout(new FlowLayout());
		label = new JLabel("Enter URL: ");
		add(label);
		label2 = new JLabel("Enter Path: ");
		tf = new JTextField(50);
		add(tf);
		add(label2);
		tf2 = new JTextField(50);
		add(tf2);
		button = new JButton("Download");
		add(button);
		label3 = new JLabel("");
		add(label3);
		event e = new event();
		button.addActionListener(e);
	}
	
	public void changeText(String txt) {
		label3.setText(txt);
	}
	
	public String getDir() {
		return dir;
	}
	
	public String getU() {
		return u;
	}
	
	public class event implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				u = tf.getText();
				dir = tf2.getText();
				URL url;
			    InputStream is = null;
			    BufferedReader br;
			    String line;
			    int groupCount = 0;
			    int[] nums = new int[50];
			    String[][] array = new String[50][2000];
			    String[][] names = new String[50][2000];
			    File[] files = new File[1000];
			    //String dir = "C:\\Users\\Robyn\\Documents\\Test CSVDownloader";
			    boolean regular = true;
			    
			    //get user input
			    //Scanner scan = new Scanner(System.in);
			    //System.out.println("Enter URL: ");
			    //u = "http://cfe.cboe.com/data/historicaldata.aspx";
			    //System.out.println("Enter Path: ");
			    //dir = "C:\\Users\\Robyn\\Documents\\Test CSVDownloader";	    
			    try {
			    	url = new URL(getU());
			        is = url.openStream();  // throws an IOException
			        br = new BufferedReader(new InputStreamReader(is));
			        
			        //Find all of the csv files and put it in String[] array
			        //Find names of the csv files and put it in String[] names
			        while ((line = br.readLine()) != null) {
			            regular = true;
			        	if(line.contains(".csv")) {
			                //System.out.println(line.trim() + "\n" + count);
			            	//find link substring
			                int firstQ = line.indexOf("\"");
			                int lastQ = line.indexOf("\"", firstQ + 1);
			                //put in correct sub array
			                int underscore = line.lastIndexOf("_");
			                if (underscore == -1) regular = false;
			                int period = line.lastIndexOf('.');
			                //find last occurrence of /
			                int lastSlash = line.indexOf('/');
			                for (int x = 0; x < period; x++) {
			                	if (line.charAt(x) == '/') lastSlash = x;
			                }
			                boolean exists = false;
			                if (regular) {
			                	for (int x = 0; x < groupCount && !exists; x++) {
			                		if (array[x][0].equals(line.substring(underscore + 1, period))) {
			                			nums[x]++;
			                			array[x][nums[x]] = "http://cfe.cboe.com" + line.substring(firstQ + 1, lastQ);
			                			names[x][nums[x]] = line.substring(lastSlash + 1, lastQ); 
			                			exists = true;
			                		}
			                	}
			                	if (!exists) {
			                		array[groupCount][0] = line.substring(underscore + 1, period);
			                		array[groupCount][1] = "http://cfe.cboe.com" + line.substring(firstQ + 1, lastQ);
			                		names[groupCount][0] = line.substring(underscore + 1, period);
			                		names[groupCount][1] = line.substring(lastSlash + 1, lastQ);
			                		nums[groupCount]++;
			                		groupCount++;
			                	}
			                }
			            }
			        }
			        
			        
			        
			        
			        //Print the names of the URLS of the files and the file names
			        changeText("Downloading Files...");
			        for (int x = 0; x < groupCount; x++) {
			        	for (int y = 0; y < nums[x]; y++) {
			        		//System.out.println(array[x][y + 1]);
			        		//System.out.println(names[x][y + 1]);
			        		String file = names[x][y + 1];
			        		new File(getDir(), file);
			        		Path p = FileSystems.getDefault().getPath(getDir(), file);
			        		URL website = new URL(array[x][y + 1]);
			        		Files.copy(website.openStream(), p, StandardCopyOption.REPLACE_EXISTING);
			        		
			        	}
			        }
			        //Download the files to String dir
			    } catch (MalformedURLException mue) {
			         mue.printStackTrace();
			    } catch (IOException ioe) {
			         ioe.printStackTrace();
			    } finally {
			        try {
			            if (is != null) is.close();
			        } catch (IOException ioe) {
			            //exception
			        }
			    }
			    //combine files of the same type and save
			    changeText("Combining Files...");
			    try {
			    	for (int x = 0; x < groupCount; x++) {
			    		File f = new File(getDir(), array[x][0] + ".csv");
			    		//System.out.println("Condensing " + array[x][0]);
			    		FileOutputStream fos = new FileOutputStream(f);
			    		//System.out.println("check 1");
			    		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			    		//System.out.println("check 2");
			    		bw.write(array[x][0]);
			    		bw.newLine();
			    		//System.out.println("check 3");
			    		for (int y = 0; y < nums[x]; y++) {
			    			File fi = new File(getDir(), names[x][y + 1]);
			    			//System.out.println("check 4");
			    			BufferedReader buff = new BufferedReader(new FileReader(fi));
			    			//System.out.println("check 5");
			    			String l;
			    			buff.readLine();
			    			if (y != 0) buff.readLine();
			    			while ((l = buff.readLine()) != null) {
			    				bw.write(l);
			    				bw.newLine();
			    			}
			    			buff.close();
			    			File file = new File(getDir() + "\\" + names[x][y + 1]);
			    			file.delete();
			    		}
			    		bw.close();
			    	}
			    } catch (IOException ioe) {
			    	//exception
			    }
			    changeText("Complete");
			} catch (Exception ex) {}
		}
	}
	
	
	public static void main(String[] args) {
	    CSVDownloader gui = new CSVDownloader();
	    gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    gui.setSize(700, 150);
	    gui.setTitle("CSVDownloader");
	    gui.setVisible(true);
		
		/*URL url;
	    InputStream is = null;
	    BufferedReader br;
	    String line;
	    int groupCount = 0;
	    int[] nums = new int[50];
	    String[][] array = new String[50][2000];
	    String[][] names = new String[50][2000];
	    //String dir = "C:\\Users\\Robyn\\Documents\\Test CSVDownloader";
	    boolean regular = true;
	    
	    //get user input
	    //Scanner scan = new Scanner(System.in);
	    //System.out.println("Enter URL: ");
	    //u = "http://cfe.cboe.com/data/historicaldata.aspx";
	    //System.out.println("Enter Path: ");
	    //dir = "C:\\Users\\Robyn\\Documents\\Test CSVDownloader";	    
	    try {
	    	url = new URL(gui.getU());
	        is = url.openStream();  // throws an IOException
	        br = new BufferedReader(new InputStreamReader(is));
	        
	        //Find all of the csv files and put it in String[] array
	        //Find names of the csv files and put it in String[] names
	        while ((line = br.readLine()) != null) {
	            regular = true;
	        	if(line.contains(".csv")) {
	                //System.out.println(line.trim() + "\n" + count);
	            	//find link substring
	                int firstQ = line.indexOf("\"");
	                int lastQ = line.indexOf("\"", firstQ + 1);
	                //put in correct sub array
	                int underscore = line.lastIndexOf("_");
	                if (underscore == -1) regular = false;
	                int period = line.lastIndexOf('.');
	                //find last occurrence of /
	                int lastSlash = line.indexOf('/');
	                for (int x = 0; x < period; x++) {
	                	if (line.charAt(x) == '/') lastSlash = x;
	                }
	                boolean exists = false;
	                if (regular) {
	                	for (int x = 0; x < groupCount && !exists; x++) {
	                		if (array[x][0].equals(line.substring(underscore + 1, period))) {
	                			nums[x]++;
	                			array[x][nums[x]] = "http://cfe.cboe.com" + line.substring(firstQ + 1, lastQ);
	                			names[x][nums[x]] = line.substring(lastSlash + 1, lastQ); 
	                			exists = true;
	                		}
	                	}
	                	if (!exists) {
	                		array[groupCount][0] = line.substring(underscore + 1, period);
	                		array[groupCount][1] = "http://cfe.cboe.com" + line.substring(firstQ + 1, lastQ);
	                		names[groupCount][0] = line.substring(underscore + 1, period);
	                		names[groupCount][1] = line.substring(lastSlash + 1, lastQ);
	                		nums[groupCount]++;
	                		groupCount++;
	                	}
	                }
	            }
	        }
	        
	        
	        
	        
	        //Print the names of the URLS of the files and the file names
	        gui.changeText("Downloading Files...");
	        for (int x = 0; x < groupCount; x++) {
	        	for (int y = 0; y < nums[x]; y++) {
	        		//System.out.println(array[x][y + 1]);
	        		//System.out.println(names[x][y + 1]);
	        		String file = names[x][y + 1];
	        		new File(gui.getDir(), file);
	        		Path p = FileSystems.getDefault().getPath(gui.getDir(), file);
	        		URL website = new URL(array[x][y + 1]);
	        		Files.copy(website.openStream(), p, StandardCopyOption.REPLACE_EXISTING);
	        		
	        	}
	        }
	        //Download the files to String dir
	    } catch (MalformedURLException mue) {
	         mue.printStackTrace();
	    } catch (IOException ioe) {
	         ioe.printStackTrace();
	    } finally {
	        try {
	            if (is != null) is.close();
	        } catch (IOException ioe) {
	            //exception
	        }
	    }
	    //combine files of the same type and save
	    gui.changeText("Combining Files...\n");
	    try {
	    	for (int x = 0; x < groupCount; x++) {
	    		File f = new File(gui.getDir(), array[x][0] + ".csv");
	    		//System.out.println("Condensing " + array[x][0]);
	    		FileOutputStream fos = new FileOutputStream(f);
	    		//System.out.println("check 1");
	    		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
	    		//System.out.println("check 2");
	    		bw.write(array[x][0]);
	    		bw.newLine();
	    		//System.out.println("check 3");
	    		for (int y = 0; y < nums[x]; y++) {
	    			File fi = new File(gui.getDir(), names[x][y + 1]);
	    			//System.out.println("check 4");
	    			BufferedReader buff = new BufferedReader(new FileReader(fi));
	    			//System.out.println("check 5");
	    			String l;
	    			buff.readLine();
	    			if (y != 0) buff.readLine();
	    			while ((l = buff.readLine()) != null) {
	    				bw.write(l);
	    				bw.newLine();
	    			}
	    			buff.close();
	    			File file = new File(gui.getDir() + "\\" + names[x][y + 1]);
	    			file.delete();
	    		}
	    		bw.close();
	    	}
	    } catch (IOException ioe) {
	    	//exception
	    }
	    gui.changeText("Complete");*/
	}

}
